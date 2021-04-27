package com.ncnf.map;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.ncnf.event.Event;
import com.ncnf.event.EventDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapHandler {

    private final Activity context;
    private final GoogleMap mMap;
    private final EventDB eventDB;
    private final VenueProvider venueProvider;

    private LatLng userPosition;
    private Marker userMarker;
    private ClusterManager<NCNFMarker> clusterManager;

    // Indicate whether Events or Venues are shown. If false -> venues are shown
    private boolean eventsShown = true;
    private final float ZOOM_LEVEL = 13;

    public MapHandler(Activity context, GoogleMap mMap, EventDB eventDB, VenueProvider venueProvider){
        this.context = context;
        this.mMap = mMap;
        if (mMap != null) { //This is just for MapHandler Unit test
            MarkerInfoWindowManager markerInfoWindowManager = new MarkerInfoWindowManager(context);

            this.mMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));

            this.clusterManager = new ClusterManager<>(context, mMap);
            this.mMap.setInfoWindowAdapter(this.clusterManager.getMarkerManager());

            this.clusterManager.getMarkerCollection().setInfoWindowAdapter(markerInfoWindowManager);
            this.clusterManager.setOnClusterItemClickListener(markerInfoWindowManager);
            this.clusterManager.setOnClusterItemInfoWindowClickListener(markerInfoWindowManager);

            this.mMap.setOnCameraIdleListener(this.clusterManager);
            this.mMap.setOnMarkerClickListener(this.clusterManager);
            this.mMap.setOnInfoWindowClickListener(this.clusterManager);
        }
        this.eventDB = eventDB;
        this.venueProvider = venueProvider;

        this.userPosition = new LatLng(46.526120f, 6.576330f);
    }

    public void switchMarkers(GoogleMap mMap) {
        eventsShown = !eventsShown;
        clusterManager.clearItems();
        if (eventsShown) {
            addEventMarkers();
            mMap.setContentDescription("MAP_WITH_EVENTS");
        } else {
            addVenueMarkers();
            mMap.setContentDescription("MAP_WITH_VENUES");
        }
        clusterManager.cluster();
    }

    public void show_markers(){
        // Add a marker near EPFL and move the camera
        MarkerOptions position_marker = new MarkerOptions().position(userPosition).title("Your Position").icon(MapUtilities.bitmapDescriptorFromVector(context));
        userMarker = mMap.addMarker(position_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));

        if (eventsShown){
            addEventMarkers();
        } else {
            addVenueMarkers();
        }
        clusterManager.cluster();
    }

    //Removes all markers from the map and recreates them according to current position
    public void update_markers(){
        clusterManager.clearItems();
        userMarker.remove();
        show_markers();
    }

    public void setUserPosition(LatLng userPosition) {
        this.userPosition = userPosition;
    }

    public LatLng getUserPosition(){
        return this.userPosition;
    }

    private void addEventMarkers(){
        List<Event> events = queryEvents();
        Map<LatLng, List<Event>> eventMap = new HashMap<>();
        for (Event p : events) {
            LatLng event_position = new LatLng(p.getLocation().getLatitude(), p.getLocation().getLongitude());
            if (MapUtilities.position_in_range(event_position, userPosition)){
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
            clusterManager.addItem(new NCNFMarker(k, description, eventMap.get(k).get(0).getAddress(), list, NCNFMarker.TYPE.EVENT));
        }
    }

    private void addVenueMarkers(){
        List<Venue> venues = venueProvider.getAll();
        for (Venue p : venues) {
            LatLng venue_position = new LatLng(p.getLatitude(), p.getLongitude());
            if (MapUtilities.position_in_range(venue_position, userPosition)){
                clusterManager.addItem(new NCNFMarker(venue_position, p.getName(), p.getName(), new ArrayList<>(), NCNFMarker.TYPE.ORGANIZER));
            }
        }
    }

    private List<Event> queryEvents(){
        return Collections.unmodifiableList(eventDB.toList());
    }
}
