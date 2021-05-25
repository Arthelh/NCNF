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
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.clustering.ClusterManager;
import com.ncnf.R;
import com.ncnf.database.firebase.DatabaseService;
import com.ncnf.models.Group;
import com.ncnf.models.SocialObject;
import com.ncnf.models.Tag;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.User;
import com.ncnf.storage.firebase.CacheFileStore;
import com.ncnf.utilities.GroupAttendeeMarker;
import com.ncnf.utilities.GroupAttendeeMarkerRenderer;
import com.ncnf.utilities.user.LocationService;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;
import static android.graphics.BitmapFactory.decodeResource;
import static com.ncnf.utilities.StringCodes.GROUPS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.FULL_NAME_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.USER_IMAGE_PATH;
import static com.ncnf.utilities.StringCodes.USER_LOCATION_KEY;


@AndroidEntryPoint
public class FriendsTrackerActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * This class was largely done thanks to the youtube Tutorial by CodingWithMitch.
     * Source code : https://github.com/mitchtabian/Google-Maps-2018/blob/building-a-service-for-gps-updates-end/app/src/main/java/com/codingwithmitch/googlemaps2018/services/LocationService.java
     */

    @Inject
    public User user;

    @Inject
    public FirebaseDatabase dbs;

    @Inject
    public CacheFileStore fileStore;

    private AppCompatImageButton findUserButton;

    private GoogleMap mMap;
    private MapView mapView;

    private List<String> friendsUUID;
    private List<GeoPoint> markers;
    private List<Bitmap> images = new ArrayList<>();

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
        friendsUUID = new ArrayList<>();

        String groupId = getIntent().getStringExtra("GROUP_ID");

        markers = new ArrayList<>();

        findUserButton = findViewById(R.id.find_user_button);

        user.loadUserFromDB();

        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.friends_map);
        mapView.onCreate(savedInstanceState);

        if(user.getParticipatingGroupsIds().contains(groupId)) {
            CompletableFuture<Group> thisGroup = user.getParticipatingGroup(groupId);

            thisGroup.thenAccept(group -> {
                if(group != null) {
                    friendsUUID = new ArrayList<>(group.getAttendees());

                    getImagesForClusters();

                    meetingPoint = group.getLocation();
                }
                else {
                    Log.d("TAG", "Group is null");
                }
            });
        }
        else {
            CompletableFuture<Group> thisGroup = user.getOwnedGroup(groupId);

            thisGroup.thenAccept(group -> {
                if(group != null) {
                    friendsUUID = new ArrayList<>(group.getAttendees());
                    friendsUUID.add(user.getUuid());
                    meetingPoint = group.getLocation();

                    getImagesForClusters();
                    startMap();

                }
                else {
                    Log.d("TAG", "Group is null");
                }
            });

        }


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
                if(user.getLoc() != null) {
                    setMapCamera(user.getLoc());
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

                GroupAttendeeMarker newAttendeeMarker = new GroupAttendeeMarker(new LatLng(markers.get(i).getLatitude(), markers.get(i).getLongitude()), "Friend " + i, "", friendsUUID.get(i), images.get(i));

                if(friendsUUID.get(i).equals(user.getUuid())) {
                    newAttendeeMarker.setTitle("This is me");
                    groupAttendeeMarkerRenderer.updateName(newAttendeeMarker);
                }

                else {
                    CompletableFuture<String> name = dbs.getField(USERS_COLLECTION_KEY + friendsUUID.get(i), FULL_NAME_KEY);
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

            if(userId == user.getUuid()) {
                if(i >= markers.size()) {
                    markers.add(user.getLoc());
                    bitmapSetChanged();
                }
            }

            CompletableFuture<GeoPoint> field = dbs.getField(USERS_COLLECTION_KEY + userId, USER_LOCATION_KEY);
            int finalI = i;
            field.thenAccept(point -> {

                if(finalI >= markers.size()) {
                    markers.add(point);
                    bitmapSetChanged();
                }
                else {
                    markers.set(finalI, point);
                    clusterMarkers.get(finalI).setPosition(new LatLng(point.getLatitude(), point.getLongitude()));
                    groupAttendeeMarkerRenderer.updatePosition(clusterMarkers.get(finalI));
                }

            });
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

                user.setLoc(geoPoint);

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

    public void getImagesForClusters() {

        Log.d("TAG", "uuid's " + friendsUUID.size());
        for(int i = 0; i < friendsUUID.size(); ++i) {
            fileStore.setContext(getApplicationContext());
            fileStore.setPath(USER_IMAGE_PATH, friendsUUID.get(i) + ".jpg");
            CompletableFuture<byte[]> image = fileStore.download();

            image.thenAccept(data -> {
                Log.d("TAG", "in exceptionally ");
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                images.add(bitmap);
                bitmapSetChanged();
            }).exceptionally(e -> {
                images.add(decodeResource(getApplicationContext().getResources(), R.drawable.default_profile_picture));
                bitmapSetChanged();
                return null;
            });

        }

    }

    private void bitmapSetChanged() {
        if(images.size() == friendsUUID.size()) {
            if(markers.size() == friendsUUID.size()) {
                addMapMarkers();
            }
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



