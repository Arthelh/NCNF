package com.ncnf.views.activities.group;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.clustering.ClusterManager;
import com.ncnf.R;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.repositories.UserRepository;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.utilities.GroupAttendeeMarker;
import com.ncnf.utilities.GroupAttendeeMarkerRenderer;
import com.ncnf.utilities.user.LocationService;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;
import static android.graphics.BitmapFactory.decodeResource;
import static com.ncnf.utilities.StringCodes.GROUP_ID_EXTRA_KEY;
import static com.ncnf.utilities.StringCodes.USER_IMAGE_PATH;


@AndroidEntryPoint
public class FriendsTrackerActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * This class was largely done thanks to the youtube Tutorial by CodingWithMitch.
     * Source code : https://github.com/mitchtabian/Google-Maps-2018/blob/building-a-service-for-gps-updates-end/app/src/main/java/com/codingwithmitch/googlemaps2018/services/LocationService.java
     */

    @Inject
    public User user;

    @Inject
    public GroupRepository groupRepository;

    @Inject
    public UserRepository userRepository;

    @Inject
    public FirebaseCacheFileStore fileStore;

    private MaterialButton findUserButton;

    private GoogleMap mMap;
    private MapView mapView;

    private List<String> friendsUUID;
    private Map<String, GeoPoint> markers;
    private Map<String, Bitmap> images = new HashMap<>();

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient myFusedLocationClient;

    private Handler handler = new Handler();
    private Runnable runnable;
    private static final int LOCATION_UPDATE_INTERVAL = 3000;

    //private Marker marker;
    private Marker meetingPointMarker;

    private GeoPoint meetingPoint;

    private LatLngBounds bounds;

    private ClusterManager clusterManager;
    private GroupAttendeeMarkerRenderer groupAttendeeMarkerRenderer;
    private ArrayList<GroupAttendeeMarker> clusterMarkers = new ArrayList<>();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_tracker);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        friendsUUID = new ArrayList<>();

        String groupId = getIntent().getStringExtra(GROUP_ID_EXTRA_KEY);

        markers = new HashMap<>();

        findUserButton = findViewById(R.id.find_user_button);

        user.loadUserFromDB();

        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.friends_map);
        mapView.onCreate(savedInstanceState);

        CompletableFuture<Group> thisGroup = groupRepository.loadGroup(groupId);
        thisGroup.thenAccept(group -> {
            if(group != null) {
                friendsUUID = new ArrayList<>(group.getMembers());
                meetingPoint = group.getLocation();

                getImagesForClusters();
                startMap();
            }
        });

    }

    private void startMap() {

        mapView.getMapAsync(googleMap -> {
            mMap = googleMap;
            onMapReady(mMap);
            getLastKnownLocation();

            if(meetingPoint != null) {
                meetingPointMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(meetingPoint.getLatitude(), meetingPoint.getLongitude())));
                meetingPointMarker.setTitle("Meeting Point");
            }


            findUserButton.setOnClickListener(v -> {
                if(user.getLocation() != null) {
                    setMapCamera(user.getLocation());
                }
            });
        });

    }

    private void addMapMarkers() {

        if(mMap != null) {

            if(clusterManager == null) {
                clusterManager = new ClusterManager<GroupAttendeeMarker>(this, mMap);
            }

            if(groupAttendeeMarkerRenderer == null) {
                groupAttendeeMarkerRenderer = new GroupAttendeeMarkerRenderer(
                        this,
                        mMap,
                        clusterManager
                );

                clusterManager.setRenderer(groupAttendeeMarkerRenderer);
            }

            for(int i = 0; i < markers.size(); ++i) {

                GroupAttendeeMarker newAttendeeMarker = new GroupAttendeeMarker(new LatLng(markers.get(friendsUUID.get(i)).getLatitude(), markers.get(friendsUUID.get(i)).getLongitude()), "Friend " + i, "", friendsUUID.get(i), images.get(friendsUUID.get(i)));

                if(friendsUUID.get(i).equals(user.getUuid())) {
                    newAttendeeMarker.setTitle("This is me");
                    groupAttendeeMarkerRenderer.updateName(newAttendeeMarker);
                }

                else {
                    CompletableFuture<String> name = userRepository.getUserFullName(friendsUUID.get(i));
                    name.thenAccept(s -> {
                        if (s != null) {
                            newAttendeeMarker.setTitle(s);
                            groupAttendeeMarkerRenderer.updateName(newAttendeeMarker);
                        }
                    });
                }

                clusterManager.addItem(newAttendeeMarker);
                clusterMarkers.add(newAttendeeMarker);

            }
            clusterManager.cluster();
        }
    }

    private void startLocationUpdates() {
        handler.postDelayed(runnable = () -> {
            getUserLocations();
            handler.postDelayed(runnable, LOCATION_UPDATE_INTERVAL);
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates(){
        handler.removeCallbacks(runnable);
    }

    private void getUserLocations() {

        for(int i = 0; i < friendsUUID.size(); ++i) {

            String userId = friendsUUID.get(i);

            if(userId.equals(user.getUuid())) {
                if(!markers.keySet().contains(userId)) {
                    markers.put(userId, user.getLocation());
                    bitmapSetChanged();
                } else {
                    markers.put(userId, user.getLocation());
                    clusterMarkers.get(i).setPosition(new LatLng(user.getLocation().getLatitude(), user.getLocation().getLongitude()));
                    groupAttendeeMarkerRenderer.updatePosition(clusterMarkers.get(i));
                }
            } else {
                CompletableFuture<GeoPoint> field = userRepository.getUserPosition(userId);
                int finalI = i;
                field.thenAccept(point -> {
                    if(point != null) {
                        if (!markers.keySet().contains(userId)) {

                            markers.put(userId, point);
                            bitmapSetChanged();
                        } else {
                            markers.put(userId, point);
                            clusterMarkers.get(finalI).setPosition(new LatLng(point.getLatitude(), point.getLongitude()));
                            groupAttendeeMarkerRenderer.updatePosition(clusterMarkers.get(finalI));

                        }
                    }

                }).exceptionally(throwable -> {throwable.printStackTrace(); return null;});
            }
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
            if (location != null) {
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                user.setLocation(geoPoint);

                startLocationService();
                setMapCamera(geoPoint);
                saveUserLocation();

            }

        });
    }

    private void saveUserLocation() {
        if(user != null) {
            userRepository.updateUserPosition(user.getUuid(), user.getLocation());
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

    /**
     * function getImagesForClusters gets the profile pictures of all users in the group from the database
     */
    public void getImagesForClusters() {

        for(int i = 0; i < friendsUUID.size(); ++i) {
            fileStore.setContext(getApplicationContext());
            fileStore.setPath(USER_IMAGE_PATH, friendsUUID.get(i) + ".jpg");
            CompletableFuture<byte[]> image = fileStore.download();

            int finalI = i;
            image.thenAccept(data -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                images.put(friendsUUID.get(finalI), bitmap);
                bitmapSetChanged();
            }).exceptionally(e -> {
                images.put(friendsUUID.get(finalI), decodeResource(getApplicationContext().getResources(), R.drawable.default_profile_picture));
                bitmapSetChanged();
                return null;
            });

        }

    }

    private void bitmapSetChanged() {
        if(images.keySet().size() == friendsUUID.size()) {
            addMapMarkers();
        }
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
    public void onMapReady(GoogleMap map) {
        startLocationUpdates();
        if (mMap != null) {
            getLastKnownLocation();
        }
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
        stopLocationUpdates();
    }

}



