package com.ncnf.views.fragments.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ncnf.R;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.repositories.EventRepository;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.utilities.map.MapHandler;
import com.ncnf.utilities.map.SearchBarHandler;
import com.ncnf.utilities.settings.Settings;

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
    private SearchBarHandler searchBarHandler;

    //Toolbar and location services for it
    private MaterialSearchBar materialSearchBar;
    private PlacesClient placesClient;
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

        materialSearchBar = requireView().findViewById(R.id.searchBarMap);

        // Initialize Google Map with the callback onMapReady
        mapView = requireView().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            mMap = googleMap;

            mapHandler = new MapHandler((AppCompatActivity) requireActivity(), mMap, getChildFragmentManager(), eventRepository, organizationRepository);
            searchBarHandler = new SearchBarHandler(getActivity(), materialSearchBar, mapHandler);

            mapHandler.show_markers();

            mMap.setContentDescription("MAP_WITH_EVENTS");

            setup_search_bar();

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


    /*
    This method comes for the most part from a youtube tutorial: https://www.youtube.com/watch?v=ifoVBdtXsv0
     */
    private void setup_search_bar(){
        //Initialize Places element
        Places.initialize(requireActivity(), "AIzaSyCRFxgUBUvyw9myry2shM_dw8VphTtEyJ4");
        placesClient = Places.createClient(requireActivity());
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        searchBarHandler.createOnSearchActionListener();

        //Configure materialSearchBar behavior
        searchBarHandler.createTextChangeListener(token, placesClient);
    }

}