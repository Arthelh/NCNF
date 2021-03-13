package com.ncnf.map;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import com.ncnf.R;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // Indicate whether Events or Venues are shown
    private boolean events_shown = true;

    private ArrayList<Marker> event_markers, venue_markers;

    @Inject
    EventProvider eventProvider;

    @Inject
    VenueProvider venueProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        // Initialize Google Map with the callback onMapReady
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker near EPFL and move the camera
        LatLng position = new LatLng(46.526120f, 6.576330f);
        MarkerOptions position_marker = new MarkerOptions().position(position).title("Your Position").icon(Utilities.bitmapDescriptorFromVector(this));
        mMap.addMarker(position_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));

        // Add a marker for each event
        List<Event> events = eventProvider.getAll();
        event_markers = new ArrayList<>();
        for (Event p : events) {
            event_markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName())));
        }
        // Add a marker for each venue
        List<Venue> venues = venueProvider.getAll();
        venue_markers = new ArrayList<>();
        for (Venue p : venues) {
            venue_markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName())));
            venue_markers.get(venues.indexOf(p)).setVisible(false);
        }

        mMap.setContentDescription("MAP_WITH_EVENTS");
    }

    public void zoomOut(View view) {
        mMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    public void zoomIn(View view) {
        mMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    public void switchMarkers(View view) {
        for (Marker m : event_markers) {
            m.setVisible(!events_shown);
        }
        for (Marker m : venue_markers) {
            m.setVisible(events_shown);
        }
        events_shown = !events_shown;
        if (events_shown) {
            mMap.setContentDescription("MAP_WITH_EVENTS");
        } else {
            mMap.setContentDescription("MAP_WITH_VENUES");
        }
    }

}