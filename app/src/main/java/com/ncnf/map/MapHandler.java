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
//    private List<AutocompletePrediction> predictionList;

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
//        predictionList = new ArrayList<>();
    }

//    public void createOnSearchActionListener(MaterialSearchBar materialSearchBar){
//        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//            @Override
//            public void onSearchStateChanged(boolean enabled) {
//
//            }
//
//            @Override
//            public void onSearchConfirmed(CharSequence text) {
//                context.startSearch(text.toString(), true, null, true);
//            }
//
//            @Override
//            public void onButtonClicked(int buttonCode) {
//                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION){
//                    //Define behaviour for navigation button
//                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK){
//                    materialSearchBar.closeSearch();
//                }
//            }
//        });
//    }
//
//    //Sets the behavior for when text is changed in the search bar
//    public void createTextChangeListener(MaterialSearchBar materialSearchBar, AutocompleteSessionToken token, PlacesClient placesClient){
//        materialSearchBar.addTextChangeListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //Creates the prediction auto completer, accepts Cities, Addresses
//                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
//                        .setCountries(Utilities.supported_countries)
//                        .setSessionToken(token)
//                        .setQuery(s.toString()).build();
//                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()){
//                        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
//                        if (predictionsResponse != null){
//                            //Retrieves the predictions and adds them to the list to be displayed below the search bar
//                            predictionList.clear();
//                            predictionList.addAll(predictionsResponse.getAutocompletePredictions());
//                            List<String> suggestions = new ArrayList<>();
//                            for (AutocompletePrediction a : predictionList){
//                                suggestions.add(a.getFullText(null).toString());
//                            }
//                            materialSearchBar.updateLastSuggestions(suggestions);
//                            if (!materialSearchBar.isSuggestionsVisible()){
//                                materialSearchBar.showSuggestionsList();
//                            }
//                        }
//                    } else {
//                        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
//                        Log.i("AutoCompleteTask", "Prediction fetching task unsuccessful, status is: " + predictionsResponse.toString());
//                    }
//                });
//
//                //This decides what to do once the user clicked on one of the given suggestions
//                createSuggestionsClickListener(materialSearchBar, placesClient);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//    }
//
//    //Creates the behavior for when clicking on a suggestion
//    private void createSuggestionsClickListener(MaterialSearchBar materialSearchBar, PlacesClient placesClient){
//        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
//            @Override
//            public void OnItemClickListener(int position, View v) {
//                if (position >= predictionList.size())
//                    return;
//                AutocompletePrediction selection = predictionList.get(position);
//                //Stores the address as a human-readable String in 'suggestion'
//                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
//                materialSearchBar.setText(suggestion);
//
//                //Because apparently just putting mSB.clearSuggestions() does not work
//                new Handler().postDelayed(materialSearchBar::clearSuggestions, 1000);
//
//                //Hides the keyboard
//                InputMethodManager input = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
//                if (input != null)
//                    input.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//
//                //Prepares the conversion from prediction to LatLng
//                final String placeId = selection.getPlaceId();
//                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
//
//                //Converts prediction to LatLng and updates markers if successful, prints out errors if not
//                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
//                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
//                    Place place = fetchPlaceResponse.getPlace();
//                    Log.i("Place fetching", "Place found: " + place.getName());
//                    LatLng placeLatLng = place.getLatLng();
//                    if (placeLatLng != null) {
//                        userPosition = placeLatLng;
//                        update_markers();
//                    }
//                }).addOnFailureListener(e -> {
//                    if (e instanceof ApiException){
//                        ApiException apiException = (ApiException) e;
//                        apiException.printStackTrace();
//                        int statusCode = apiException.getStatusCode();
//                        Log.i("Place fetching", "Place not found: " + e.getMessage());
//                        Log.i("Place fetching", "Status code: " + statusCode);
//                    }
//                });
//            }
//
//            @Override
//            public void OnItemDeleteListener(int position, View v) {
//
//            }
//        });
//    }

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

    public void show_markers(GoogleMap mMap, EventProvider eventProvider, VenueProvider venueProvider){
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
        show_markers(mMap, eventProvider, venueProvider);
    }

    public void setUserPosition(LatLng userPosition) {
        this.userPosition = userPosition;
    }
}
