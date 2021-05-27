package com.ncnf.utilities.user;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.models.User;

import static android.content.ContentValues.TAG;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.USER_LOCATION_KEY;


/**
 * This class was done thanks to the youtube Tutorial by CodingWithMitch.
 * Source code : https://github.com/mitchtabian/Google-Maps-2018/blob/building-a-service-for-gps-updates-end/app/src/main/java/com/codingwithmitch/googlemaps2018/services/LocationService.java
 */

public class LocationService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;

    private FirebaseDatabase dbs;

    private final static long UPDATE_INTERVAL = 3 * 1000;  /* 3 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocationServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.dbs = new FirebaseDatabase();


        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "location_service_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "called onStartCommand");
        getLocation();
        return START_NOT_STICKY;
    }

    private void getLocation() {
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }

        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Log.d(TAG, "onLocationResult: got location result.");

                Location location = locationResult.getLastLocation();

                if (location != null) {
                    User user = CurrentUserModule.getCurrentUser();
                    if(user != null && user.getUuid() != null) {

                        user.loadUserFromDB().thenAccept(user1 -> {
                            if (user1 != null) {
                                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                dbs.updateField(USERS_COLLECTION_KEY + user1.getUuid(), LOCATION_KEY, geoPoint).thenAccept(bool ->{
                                    if(bool){
                                        user1.setLocation(geoPoint);
                                    }
                                }).exceptionally(exception -> {
                                    stopSelf();
                                    return null;
                                });
                            } else {
                                stopSelf();
                            }
                        });
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    private void saveUserLocation(final GeoPoint location, String uuid){

        try{
            dbs.updateField(USERS_COLLECTION_KEY + uuid, USER_LOCATION_KEY, location).thenAccept(aBoolean -> {
                if (aBoolean) {
                    Log.d(TAG, "onComplete: \ninserted user location into database." +
                            "\n latitude: " + location.getLatitude() +
                            "\n longitude: " + location.getLongitude());
                }
            }
        },
        Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    public class LocationServiceBinder extends Binder {

        public LocationService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return LocationService.this;
        }
    }
}
