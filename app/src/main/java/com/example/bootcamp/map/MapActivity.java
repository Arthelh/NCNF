package com.example.bootcamp.map;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bootcamp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean events_shown = true;
    private ArrayList<PlaceholderEvent> event_list;
    private ArrayList<PlaceholderVenue> venue_list;
    private ArrayList<Marker> event_markers, venue_markers;
    private MarkerOptions position_marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker near EPFL and move the camera
        LatLng position = new LatLng(46.526120f, 6.576330f);
        position_marker = new MarkerOptions().position(position).title("Your Position").icon(Utilities.bitmapDescriptorFromVector(this));
        mMap.addMarker(position_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));

        event_list = PlaceholderEvent.getTestEvents();
        event_markers = new ArrayList<>();
        for (PlaceholderEvent p : event_list) {
            event_markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName())));
        }
        venue_list = PlaceholderVenue.getTestVenues();
        venue_markers = new ArrayList<>();
        for (PlaceholderVenue p : venue_list) {
            venue_markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName())));
            venue_markers.get(venue_list.indexOf(p)).setVisible(false);
        }
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
    }

    public GoogleMap getMap(){
        return mMap;
    }

    public ArrayList<Marker> getEvent_markers(){
        return event_markers;
    }

    public ArrayList<Marker> getVenue_markers(){
        return venue_markers;
    }

    public boolean getEvents_shown(){
        return events_shown;
    }

}