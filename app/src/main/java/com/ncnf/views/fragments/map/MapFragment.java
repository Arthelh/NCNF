package com.ncnf.views.fragments.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.ncnf.R;
import com.ncnf.repositories.EventRepository;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.utilities.map.MapHandler;
import com.ncnf.utilities.map.MapUtilities;
import com.ncnf.utilities.settings.Settings;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment{

    @Inject
    public EventRepository eventRepository;

    @Inject
    public OrganizationRepository organizationRepository;

    private GoogleMap mMap;
    private MapView mapView;

    private MapHandler mapHandler;
    private MaterialButton searchButton;

    private final ActivityResultLauncher<Intent> searchBarLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::updateUserLocation);

    //Toolbar and location services for it
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    //Indicates whether map is ready, useful for onResume() method
    private boolean map_ready = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                returnToLocation(view);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        Places.initialize(getContext(), "AIzaSyCRFxgUBUvyw9myry2shM_dw8VphTtEyJ4");

        searchButton = requireView().findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this::launchAddressSearchBar);

        // Initialize Google Map with the callback onMapReady
        mapView = requireView().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            mMap = googleMap;

            mapHandler = new MapHandler((AppCompatActivity) requireActivity(), mMap, getChildFragmentManager(), eventRepository, organizationRepository);

            mapHandler.show_markers();

            mMap.setContentDescription("MAP_WITH_EVENTS");

            map_ready = true;
        });

        requireView().findViewById(R.id.map_switch_button).setOnClickListener(this::switchMarkers);
        requireView().findViewById(R.id.map_gps_button).setOnClickListener(this::returnToLocation);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (map_ready)
            mapHandler.update_markers();
    }

    public void switchMarkers(View view) {
        mapHandler.switchMarkers(mMap);
    }

    public void returnToLocation(View view) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location gpsLocation = task.getResult();
                if (gpsLocation != null) {
                    LatLng userLocation = new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude());
                    Settings.setUserPosition(userLocation);
                    mapHandler.update_markers();
                }
            });
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    public void updateUserLocation(ActivityResult result){
        if (result.getResultCode() == Activity.RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(result.getData());
            Settings.setUserPosition(place.getLatLng());
            Log.d("debug", "Got here 2");
        }
    }

    public void launchAddressSearchBar(View view){
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Autocomplete.IntentBuilder intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields);

        intent.setCountries(MapUtilities.supported_countries);

        Log.d("debug", "Got here 1");

        searchBarLauncher.launch(intent.build(getActivity()));
    }

}