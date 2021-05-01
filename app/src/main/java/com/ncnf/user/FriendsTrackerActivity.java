package com.ncnf.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EMAIL_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;
import static com.ncnf.Utils.USER_LOCATION_KEY;

@AndroidEntryPoint
public class FriendsTrackerActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Inject
    public User user;

    private AppCompatImageButton findUserButton;

    private GoogleMap mMap;
    private MapView mapView;

    private List<String> friendsUUID;
    private List<Marker> markers;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient myFusedLocationClient;

    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private Runnable runnable;
    private Runnable runnable2;
    private static final int LOCATION_UPDATE_INTERVAL = 3000;

    private int counter;

    // for testing
    private static final String uuid = "MSpKLkyyrrN3PC5KmxkoD05Vy1m2";
    private static final String uuid2 = "xsohP7PYdDQZ69STCpznOZt0Zfg2";

    private Marker marker;

    public DatabaseService dbs;

    private LatLngBounds bounds;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_tracker);

        dbs = new DatabaseService();

        //dbs.updateField(USERS_COLLECTION_KEY + uuid, USER_LOCATION_KEY, new GeoPoint(46.5201852, 6.5637122));
        //dbs.updateField(USERS_COLLECTION_KEY + uuid2, USER_LOCATION_KEY, new GeoPoint(46.516981, 6.57144331));

        counter = 0;

        friendsUUID = new ArrayList<>();
        markers = new ArrayList<>();

        friendsUUID.add(uuid);
        //friendsUUID.add(uuid2);

        findUserButton = findViewById(R.id.find_user_button);

        //user = CurrentUserModule.getCurrentUser();
        user.loadUserFromDB();

        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.friends_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            mMap = googleMap;
            onMapReady(mMap);
            getLastKnownLocation();

            //changeUserLocs();

            findUserButton.setOnClickListener(v -> {
                if(user.getLoc() != null) {
                    setMapCamera(user.getLoc());
                }
            });
        });


    }

    private void startLocationUpdates() {
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                getUserLocations();
                handler.postDelayed(runnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    // ONLY FOR TESTING / DEMOS
    private void changeUserLocs() {
        handler2.postDelayed(runnable2 = new Runnable() {
            @Override
            public void run() {
                dbs.updateField(USERS_COLLECTION_KEY + uuid, USER_LOCATION_KEY, new GeoPoint(46.51612823811807 + counter*0.0005, 6.560711384991796)).thenAccept(aBoolean -> {
                    dbs.updateField(USERS_COLLECTION_KEY + uuid2, USER_LOCATION_KEY, new GeoPoint(46.51612823811807, 6.560711384991796+ counter*0.0005)).thenAccept(aBoolean1 -> {
                        counter += 1;
                    });
                });
                handler2.postDelayed(runnable2, 2*LOCATION_UPDATE_INTERVAL);
            }
        }, 2*LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates(){
        handler.removeCallbacks(runnable);
        //handler2.removeCallbacks(runnable2);
    }

    private void getUserLocations() {
        for(int i = 0; i < friendsUUID.size(); ++i) {

            String userId = friendsUUID.get(i);
            CompletableFuture<GeoPoint> field = dbs.getField(USERS_COLLECTION_KEY + userId, USER_LOCATION_KEY);
            int finalI = i;
            field.thenAccept(point -> {
                if(finalI >= markers.size()) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude()))));
                    CompletableFuture<String> name = dbs.getField(USERS_COLLECTION_KEY + userId, FIRST_NAME_KEY);
                    name.thenAccept(s -> {
                        if(s != null) {
                            markers.get(finalI).setTitle(s);
                        }
                    });
                }
                else {
                    markers.get(finalI).setPosition(new LatLng(point.getLatitude(), point.getLongitude()));
                }
            });
        }
        if(marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(user.getLoc().getLatitude(), user.getLoc().getLongitude())));
            marker.setTitle(user.getFirstName());
        }
        else {
            marker.setPosition(new LatLng(user.getLoc().getLatitude(), user.getLoc().getLongitude()));
        }
    }

    private void setMapCamera(GeoPoint point) {

        double zoom = .05;

        bounds = new LatLngBounds(
                new LatLng(point.getLatitude() - zoom, point.getLongitude() - zoom),
                new LatLng(point.getLatitude() + zoom, point.getLongitude() + zoom)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
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
        myFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                user.setLoc(geoPoint);

                if (marker == null) {
                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())));
                    marker.setTitle(user.getFirstName());
                } else {
                    marker.setPosition(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                }
                startLocationService();
                setMapCamera(geoPoint);
                saveUserLocation();

            }
            Log.d(TAG, "location null");
        });
    }

    private void saveUserLocation() {
        if(user != null) {
            dbs.updateField(USERS_COLLECTION_KEY + user.getUuid(), USER_LOCATION_KEY, user.getLoc());
        }
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

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                FriendsTrackerActivity.this.startForegroundService(serviceIntent);
            }
            else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.ncnf.user.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (mMap != null) {
            getLastKnownLocation();
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
        startLocationUpdates();
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
        stopLocationUpdates();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}



