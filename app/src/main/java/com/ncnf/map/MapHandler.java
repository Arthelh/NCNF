package com.ncnf.map;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

    // Indicate whether Events or Venues are shown. If false -> venues are shown
    private boolean eventsShown = true;
    private final float ZOOM_LEVEL = 11;

    public MapHandler(Activity context, GoogleMap mMap, EventDB eventDB, VenueProvider venueProvider){
        this.context = context;
        this.mMap = mMap;
        if (mMap != null) //This is just for MapHandler Unit test
            this.mMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
        this.eventDB = eventDB;
        this.venueProvider = venueProvider;

        userPosition = new LatLng(46.526120f, 6.576330f);
        eventMarkers = new ArrayList<>();
        venueMarkers = new ArrayList<>();
    }

    public void switchMarkers(GoogleMap mMap) {
        eventsShown = !eventsShown;
        for (Marker e : eventMarkers) {
            e.setVisible(eventsShown);
        }
        for (Marker v : venueMarkers) {
            v.setVisible(!eventsShown);
        }
        if (eventsShown) {
            mMap.setContentDescription("MAP_WITH_EVENTS");
        } else {
            mMap.setContentDescription("MAP_WITH_VENUES");
        }
    }

    public void show_markers(){
        // Add a marker near EPFL and move the camera
        MarkerOptions position_marker = new MarkerOptions().position(userPosition).title("Your Position").icon(MapUtilities.bitmapDescriptorFromVector(context));
        userMarker = mMap.addMarker(position_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        // Add a marker for each event
        List<Event> events = eventDB.toList();
        eventMarkers = new ArrayList<>();
        for (Event p : events) {
            LatLng event_position = new LatLng(p.getLocation().getLatitude(), p.getLocation().getLongitude());
            if (MapUtilities.position_in_range(event_position, userPosition)){
                eventMarkers.add(mMap.addMarker(new MarkerOptions().position(event_position).title(p.getName())));
            }
        }
        // Add a marker for each venue
        List<Venue> venues = venueProvider.getAll();
        venueMarkers = new ArrayList<>();
        for (Venue p : venues) {
            LatLng venue_position = new LatLng(p.getLatitude(), p.getLongitude());
            if (MapUtilities.position_in_range(venue_position, userPosition)){
                venueMarkers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName())));
                //Set last element (above line) to invisible
                venueMarkers.get(venueMarkers.size() - 1).setVisible(false);
            }
        }
    }

    //Removes all markers from the map and recreates them according to current position
    public void update_markers(){
        for (Marker m : eventMarkers)
            m.remove();
        for (Marker m : venueMarkers)
            m.remove();
        eventMarkers.clear();
        venueMarkers.clear();
        userMarker.remove();
        show_markers();
    }

    public void setUserPosition(LatLng userPosition) {
        this.userPosition = userPosition;
    }

    public LatLng getUserPosition(){
        return this.userPosition;
    }
}
