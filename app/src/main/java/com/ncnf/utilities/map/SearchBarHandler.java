package com.ncnf.utilities.map;

import android.app.Activity;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.ncnf.R;
import com.ncnf.utilities.settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchBarHandler {

    private final Activity context;
    private final MapHandler mapHandler;
    private final MaterialSearchBar materialSearchBar;

    private List<AutocompletePrediction> predictionList;

    public SearchBarHandler(Activity context, MaterialSearchBar materialSearchBar, MapHandler mapHandler){
        this.context = context;
        this.mapHandler = mapHandler;
        this.materialSearchBar = materialSearchBar;
        predictionList = new ArrayList<>();
    }

    public void createOnSearchActionListener(){
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                context.startSearch(text.toString(), true, null, true);
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
    }

    //Sets the behavior for when text is changed in the search bar
    public void createTextChangeListener(AutocompleteSessionToken token, PlacesClient placesClient){
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Creates the prediction auto completer, accepts Cities, Addresses
                try{
                    FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                            .setCountries(MapUtilities.supported_countries)
                            .setSessionToken(token)
                            .setQuery(s.toString()).build();
                    placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null){
                                //Retrieves the predictions and adds them to the list to be displayed below the search bar
                                predictionList.clear();
                                predictionList.addAll(predictionsResponse.getAutocompletePredictions());
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
                    createSuggestionsClickListener(placesClient);
                } catch (Exception e){
                    Toast.makeText(context, R.string.map_toolbar_error, Toast.LENGTH_LONG).show();
                } catch (Error r){
                    Toast.makeText(context, R.string.map_toolbar_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Creates the behavior for when clicking on a suggestion
    private void createSuggestionsClickListener(PlacesClient placesClient){
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
                InputMethodManager input = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
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
                        Settings.setUserPosition(placeLatLng);
                        mapHandler.update_markers();
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
}
