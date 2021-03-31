package com.ncnf.map;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.ncnf.R;
import com.ncnf.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private float zoom_level = 11;

    private Location location;
    private LatLng user_position;
    private Marker user_marker;

    //Toolbar and location services for it
    private MaterialSearchBar materialSearchBar;
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
        zoom_level = mMap.getCameraPosition().zoom;
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
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom_level));

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

    private void setup_search_bar(){
        //Initialize Places element
        Places.initialize(MapActivity.this, "AIzaSyCRFxgUBUvyw9myry2shM_dw8VphTtEyJ4");
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        //Initialize materialSearchBar
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
                    //Define behaviour for navigation button
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK){
                    materialSearchBar.closeSearch();
                }
            }
        });

        //Configure materialSearchBar behavior
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountries(Utilities.supported_countries)
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString()).build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                        if (predictionsResponse != null){
                            predictionList = predictionsResponse.getAutocompletePredictions();
                            List<String> suggestions = new ArrayList<>();
                            for (AutocompletePrediction a : predictionList){
                                suggestions.add(a.getFullText(null).toString());
                            }
                            materialSearchBar.updateLastSuggestions(suggestions);
                            if (!materialSearchBar.isSuggestionsVisible()){
                                materialSearchBar.showSuggestionsList();
                            }
                        }
                    } else {
                        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                        Log.i("AutoCompleteTask", "Prediction fetching task unsuccessful, status is: " + predictionsResponse.toString());
                    }
                });

                //This decides what to do once the user clicked on one of the given suggestions
                materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
                    @Override
                    public void OnItemClickListener(int position, View v) {
                        if (position >= predictionList.size())
                            return;
                        AutocompletePrediction selection = predictionList.get(position);
                        //Stores the address as a human-readable String in 'suggestion'
                        String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                        materialSearchBar.setText(suggestion);

                        //Because apparently just putting mSB.clearSuggestions() does not work
                        new Handler().postDelayed(materialSearchBar::clearSuggestions, 1000);

                        //Hides the keyboard
                        InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (input != null)
                            input.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                        //Prepares the conversion from prediction to LatLng
                        final String placeId = selection.getPlaceId();
                        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                        //Converts prediction to LatLng and updates markers if successful, prints out errors if not
                        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
                            Place place = fetchPlaceResponse.getPlace();
                            Log.i("Place fetching", "Place found: " + place.getName());
                            LatLng placeLatLng = place.getLatLng();
                            if (placeLatLng != null) {
                                user_position = placeLatLng;
                                update_markers();
                            }
                        }).addOnFailureListener(e -> {
                            if (e instanceof ApiException){
                                ApiException apiException = (ApiException) e;
                                apiException.printStackTrace();
                                int statusCode = apiException.getStatusCode();
                                Log.i("Place fetching", "Place not found: " + e.getMessage());
                                Log.i("Place fetching", "Status code: " + statusCode);
                            }
                        });
                    }

                    @Override
                    public void OnItemDeleteListener(int position, View v) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void update_markers(){
        for (Marker m : event_markers)
            m.remove();
        for (Marker m : venue_markers)
            m.remove();
        event_markers.clear();
        venue_markers.clear();
        user_marker.remove();
        show_markers();
    }

}