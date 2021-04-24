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
import java.util.List;

public class MapHandler {

    private final Activity context;
    private final GoogleMap mMap;
    private final EventDB eventDB;
    private final VenueProvider venueProvider;

    private LatLng userPosition;
    private Marker userMarker;
    private ArrayList<Marker> eventMarkers, venueMarkers;
    private ClusterManager<com.ncnf.map.Marker> clusterManager;

    // Indicate whether Events or Venues are shown. If false -> venues are shown
    private boolean eventsShown = true;
    private final float ZOOM_LEVEL = 13;

    public MapHandler(Activity context, GoogleMap mMap, EventDB eventDB, VenueProvider venueProvider){
        this.context = context;
        this.mMap = mMap;
        if (mMap != null) { //This is just for MapHandler Unit test
            this.mMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
            this.clusterManager = new ClusterManager<com.ncnf.map.Marker>(context, mMap);
            mMap.setOnCameraIdleListener(this.clusterManager);
            mMap.setOnMarkerClickListener(this.clusterManager);
        }
        this.eventDB = eventDB;
        this.venueProvider = venueProvider;

        userPosition = new LatLng(46.526120f, 6.576330f);
        eventMarkers = new ArrayList<>();
        venueMarkers = new ArrayList<>();
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
        List<Event> events = eventDB.toList();
        eventMarkers = new ArrayList<>();
        for (Event p : events) {
            LatLng event_position = new LatLng(p.getLocation().getLatitude(), p.getLocation().getLongitude());
            if (MapUtilities.position_in_range(event_position, userPosition)){
                clusterManager.addItem(new com.ncnf.map.Marker(event_position, p.getName(), p.getDescription()));
            }
        }
    }

    private void addVenueMarkers(){
        List<Venue> venues = venueProvider.getAll();
        venueMarkers = new ArrayList<>();
        for (Venue p : venues) {
            LatLng venue_position = new LatLng(p.getLatitude(), p.getLongitude());
            if (MapUtilities.position_in_range(venue_position, userPosition)){
                clusterManager.addItem(new com.ncnf.map.Marker(venue_position, p.getName(), p.getName()));
            }
        }
    }
}
