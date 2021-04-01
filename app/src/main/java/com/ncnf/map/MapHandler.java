package com.ncnf.map;

import android.app.Activity;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MapHandler {

    private final Activity context;
    private final GoogleMap mMap;
    private final EventProvider eventProvider;
    private final VenueProvider venueProvider;

    private LatLng userPosition;
    private Marker userMarker;
    private ArrayList<Marker> eventMarkers, venueMarkers;

    // Indicate whether Events or Venues are shown. If false -> venues are shown
    private boolean eventsShown = true;
    private float zoomLevel = 11;

    public MapHandler(Activity context, GoogleMap mMap, EventProvider eventProvider, VenueProvider venueProvider){
        this.context = context;
        this.mMap = mMap;
        this.eventProvider = eventProvider;
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
        MarkerOptions position_marker = new MarkerOptions().position(userPosition).title("Your Position").icon(Utilities.bitmapDescriptorFromVector(context));
        userMarker = mMap.addMarker(position_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        // Add a marker for each event
        List<Event> events = eventProvider.getAll();
        eventMarkers = new ArrayList<>();
        for (Event p : events) {
            LatLng event_position = new LatLng(p.getLatitude(), p.getLongitude());
            if (Utilities.position_in_range(event_position, userPosition)){
                eventMarkers.add(mMap.addMarker(new MarkerOptions().position(event_position).title(p.getName())));
            }
        }
        // Add a marker for each venue
        List<Venue> venues = venueProvider.getAll();
        venueMarkers = new ArrayList<>();
        for (Venue p : venues) {
            LatLng venue_position = new LatLng(p.getLatitude(), p.getLongitude());
            if (Utilities.position_in_range(venue_position, userPosition)){
                venueMarkers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName())));
                //Set last element (above line) to invisible
                venueMarkers.get(venueMarkers.size() - 1).setVisible(false);
            }
        }
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
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
}
