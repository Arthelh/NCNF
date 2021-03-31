package com.ncnf.map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.ncnf.R;
import com.ncnf.settings.SettingsActivity;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int INITIAL_ZOOM_LEVEL = 11;

    private GoogleMap mMap;

    private LatLng user_position;
    private Marker user_marker;

    //Toolbar and location services for it
    private MaterialSearchBar materialSearchBar;
    private View mapView;

    private FusedLocationProviderClient mFused;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    // Indicate whether Events or Venues are shown. If false -> venues are shown
    private boolean events_shown = true;
    //Indicates whether map is ready, useful for onResume() method
    private boolean map_ready = false;

    private ArrayList<Marker> event_markers, venue_markers;

    @Inject
    EventProvider eventProvider;

    @Inject
    VenueProvider venueProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        materialSearchBar = findViewById(R.id.searchBarMap);

        event_markers = new ArrayList<>();
        venue_markers = new ArrayList<>();

        //PlaceHolder position, set to Chavannes-pres-Renens
        user_position = new LatLng(46.526120f, 6.576330f);
        
        // Initialize Google Map with the callback onMapReady
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map_ready)
            update_markers();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        show_markers();

        mMap.setContentDescription("MAP_WITH_EVENTS");

        setup_search_bar();

        map_ready = true;
    }

    public void zoomOut(View view) {
        mMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    public void zoomIn(View view) {
        mMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    public void start_settings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void switchMarkers(View view) {
        events_shown = !events_shown;
        for (Marker e : event_markers) {
            e.setVisible(events_shown);
        }
        for (Marker v : venue_markers) {
            v.setVisible(!events_shown);
        }
        if (events_shown) {
            mMap.setContentDescription("MAP_WITH_EVENTS");
        } else {
            mMap.setContentDescription("MAP_WITH_VENUES");
        }
    }

    private void show_markers(){
        // Add a marker near EPFL and move the camera
        MarkerOptions position_marker = new MarkerOptions().position(user_position).title("Your Position").icon(Utilities.bitmapDescriptorFromVector(this));
        user_marker = mMap.addMarker(position_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user_position));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));

        // Add a marker for each event
        List<Event> events = eventProvider.getAll();
        event_markers = new ArrayList<>();
        for (Event p : events) {
            LatLng event_position = new LatLng(p.getLatitude(), p.getLongitude());
            if (Utilities.position_in_range(event_position, user_position)){
                event_markers.add(mMap.addMarker(new MarkerOptions().position(event_position).title(p.getName())));
            }
        }
        // Add a marker for each venue
        List<Venue> venues = venueProvider.getAll();
        venue_markers = new ArrayList<>();
        for (Venue p : venues) {
            LatLng venue_position = new LatLng(p.getLatitude(), p.getLongitude());
            if (Utilities.position_in_range(venue_position, user_position)){
                venue_markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName())));
                //Set last element (above line) to invisible
                venue_markers.get(venue_markers.size() - 1).setVisible(false);
            }
        }
    }

    /*
    This method comes for the most part from a youtube tutorial: https://www.youtube.com/watch?v=ifoVBdtXsv0
     */
    private void setup_search_bar(){
        //Initialize Places element with Places API key and create session token for Place retrieval
        Places.initialize(MapActivity.this, "AIzaSyCRFxgUBUvyw9myry2shM_dw8VphTtEyJ4");
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        //Initialize materialSearchBar
        createSearchActionListener(materialSearchBar);

        createTextWatcher(materialSearchBar, token);
    }

    private void update_markers(){
        for (Marker m : event_markers)
            m.remove();
        for (Marker m : venue_markers)
            m.remove();
        user_marker.remove();
        event_markers.clear();
        venue_markers.clear();
        show_markers();
    }

    private void createSearchActionListener(MaterialSearchBar materialSearchBar){
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION){
                    //Define Navigation Button (the three horizontal bars) behavior
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK){
                    materialSearchBar.closeSearch();
                }
            }
        });
    }

    private void createTextWatcher(MaterialSearchBar materialSearchBar, AutocompleteSessionToken token){
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This code below will give addresses suggestions as the user types in
                //Currently only in the form of addresses in Switzerland
                //TO BE COMPLETED
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}