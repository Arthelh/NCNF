package com.ncnf.map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.event.EventDB;
import com.ncnf.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.DEBUG_TAG;

public class MapHandler {

    private final Activity context;
    private final GoogleMap mMap;
    private final DatabaseService databaseService;
    private final VenueProvider venueProvider;

    private Marker userMarker;
    private ClusterManager<NCNFMarker> clusterManager;

    //Event cache to avoid querying every time we switch from organizers to events
    private List<Event> cache;

    // Indicate whether Events or Venues are shown. If false -> venues are shown
    private boolean eventsShown = true;
    private final float ZOOM_LEVEL = 13;

    /**
     * Creates a new MapHandler, the structure that takes care of displaying the markers correctly
     * and handling the retrieval of the events and organizations to be displayed
     * @param context The activity the map will be displayed in
     * @param mMap The Google Map to display
     * @param venueProvider A provider for the organizers, or venues.
     * @param fragmentManager A children fragment manager from the MapFragment
     */
    public MapHandler(Activity context, GoogleMap mMap, VenueProvider venueProvider, FragmentManager fragmentManager){
        this.context = context;
        this.mMap = mMap;
        if (mMap != null) { //This is just for MapHandler Unit test
            MarkerInfoWindowManager markerInfoWindowManager = new MarkerInfoWindowManager(context, context.getWindow(), fragmentManager);

            this.mMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));

            this.clusterManager = new ClusterManager<>(context, mMap);
            this.clusterManager.setRenderer(new CustomRender<>(context, mMap, this.clusterManager));
            this.mMap.setInfoWindowAdapter(this.clusterManager.getMarkerManager());

            this.clusterManager.getMarkerCollection().setInfoWindowAdapter(markerInfoWindowManager);
            this.clusterManager.setOnClusterItemClickListener(markerInfoWindowManager);
            this.clusterManager.setOnClusterItemInfoWindowClickListener(markerInfoWindowManager);

            this.mMap.setOnCameraIdleListener(this.clusterManager);
            this.mMap.setOnMarkerClickListener(this.clusterManager);
            this.mMap.setOnInfoWindowClickListener(this.clusterManager);
        }
        this.databaseService = new DatabaseService();
        this.venueProvider = venueProvider;
    }

    /**
     * Switches between event markers and organizer markers on the map. Must be given the map as argument
     * @param mMap The Google Map on which the markers are displayed
     */
    public void switchMarkers(GoogleMap mMap) {
        eventsShown = !eventsShown;
        clusterManager.clearItems();
        if (eventsShown) {
            queryAndAddEvents();
            mMap.setContentDescription("MAP_WITH_EVENTS");
        } else {
            addVenueMarkers();
            mMap.setContentDescription("MAP_WITH_VENUES");
        }
        clusterManager.cluster();
    }

    /**
     * Displays the markers on the map according to current settings
     */
    public void show_markers(){
        // Add a marker near EPFL and move the camera
        MarkerOptions position_marker = new MarkerOptions().position(Settings.userPosition).title("Your Position").icon(MapUtilities.bitmapDescriptorFromVector(context));
        userMarker = mMap.addMarker(position_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Settings.userPosition));

        if (eventsShown){
            queryAndAddEvents();
        } else {
            addVenueMarkers();
        }
        clusterManager.cluster();
    }

    /**
     * Updates the markers on the map. This method should be called after a change that
     * will modify which markers will appear on the map
     */
    public void update_markers(){
        clusterManager.clearItems();
        userMarker.remove();
        show_markers();
        clusterManager.cluster();
    }

    private void addEventMarkers(List<Event> events){
        Log.d(DEBUG_TAG, "Adding event markers, event list size: " + events.size());
        Map<LatLng, List<Event>> eventMap = new HashMap<>();
        for (Event p : events) {
            LatLng event_position = new LatLng(p.getLocation().getLatitude(), p.getLocation().getLongitude());

            //Additional check in range as geoqueries sometimes have false positives (https://cloud.google.com/firestore/docs/solutions/geoqueries#javaandroid_1)
            if (MapUtilities.position_in_range(event_position, Settings.userPosition)){
                if (!eventMap.containsKey(event_position)){
                    eventMap.put(event_position, new ArrayList<>());
                }
                eventMap.get(event_position).add(p);
            }
        }
        Set<LatLng> keys = eventMap.keySet();
        for (LatLng k : keys){
            List<Event> list = eventMap.get(k);
            StringBuilder desc = new StringBuilder();
            for (Event p : list){
                desc.append(p.getName()).append("\n");
            }
            String description = desc.toString();
            clusterManager.addItem(new NCNFMarker(k, description, eventMap.get(k).get(0).getAddress(), list, true));
        }
        clusterManager.cluster();
    }

    private void addVenueMarkers(){
        List<Venue> venues = venueProvider.getAll();
        for (Venue p : venues) {
            LatLng venue_position = new LatLng(p.getLatitude(), p.getLongitude());
            if (MapUtilities.position_in_range(venue_position, Settings.userPosition)){
                clusterManager.addItem(new NCNFMarker(venue_position, p.getName(), p.getName(), new ArrayList<>(), false));
            }
        }
    }

    private void queryAndAddEvents(){
        final List<Event> result = new ArrayList<>();

        CompletableFuture<List<Event>> completableFuture = databaseService.eventGeoQuery(Settings.userPosition, Settings.getCurrent_max_distance() * 1000);
        completableFuture.thenAccept(eventList -> {

            result.addAll(eventList);
            addEventMarkers(result);

        }).exceptionally(e -> {

            Log.d(DEBUG_TAG, e.getMessage());
            return null;

        });
    }

    //This makes it so that markers cluster as soon as 2 of them are close enough
    private class CustomRender<T extends ClusterItem> extends DefaultClusterRenderer<T>{
        public CustomRender(Context context, GoogleMap map, ClusterManager clusterManager){
            super(context, map, clusterManager);
        }

        @Override
        protected boolean shouldRenderAsCluster(@NonNull Cluster<T> cluster) {
            return cluster.getSize() > 1;
        }


    }
}
