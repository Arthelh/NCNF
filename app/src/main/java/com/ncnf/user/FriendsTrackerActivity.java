package com.ncnf.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.map.MapHandler;
import com.ncnf.map.SearchBarHandler;

import java.util.List;

import javax.inject.Inject;

import static android.content.ContentValues.TAG;
import static com.ncnf.Utils.USER_LOCATION_KEY;

public class FriendsTrackerActivity extends AppCompatActivity implements OnMapReadyCallback {


    public User user;

    private List<User> friends;

    private GoogleMap mMap;
    private MapView mapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient myFusedLocationClient;

    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        user = CurrentUserModule.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_tracker);

        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        mapView = findViewById(R.id.friends_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            mMap = googleMap;
            onMapReady(mMap);
        });

    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())).title("ME !"));
                    Log.d(TAG, "Latitude " + geoPoint.getLatitude());
                    Log.d(TAG, "Longitude " + geoPoint.getLongitude());

                    user.setLocation(geoPoint);
                    saveUserLocation();
                }
            }
        });
    }

    private void saveUserLocation() {
        if(user != null) {
            user.updateUserLocation(user.getLocation());
        }
    }

    private GeoPoint getUserLocation(User u) {
        if(u != null) {
            try {
                GeoPoint point = (GeoPoint) u.getField(USER_LOCATION_KEY).get().getResult();
                u.setLocation(point);
            }
            catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private void setMarker(GeoPoint point, String title) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).title(title));
    }

    private void setMarkers() {
        marker.setPosition(new LatLng(user.getLocation().getLatitude(), user.getLocation().getLongitude()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if(mMap != null) {
            getLastKnownLocation();
            setMarker(user.getLocation(), "ME !");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        setMarkers();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
