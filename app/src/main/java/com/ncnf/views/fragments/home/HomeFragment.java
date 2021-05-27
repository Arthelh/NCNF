package com.ncnf.views.fragments.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.views.activities.login.LoginActivity;
import com.ncnf.views.activities.friends.FriendsActivity;
import com.ncnf.views.activities.group.FriendsTrackerActivity;

import com.ncnf.views.activities.group.GroupCreateActivity;
import com.ncnf.views.activities.event.EventNewsActivity;
import com.ncnf.views.activities.user.UserTabActivity;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static com.ncnf.utilities.StringCodes.NEXT_ACTIVITY_EXTRA_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.getExtras() != null) {
            boolean gotDisconnected = intent.getExtras().getBoolean("disconnected");
            if (gotDisconnected) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Successfully disconnected", LENGTH_SHORT).show();
            }
        }
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.homeProfileButton).setOnClickListener(this::gotToProfile);
        getView().findViewById(R.id.homeCreateEventButton).setOnClickListener(this::goToEventCreation);
        getView().findViewById(R.id.homeEventNewsButton).setOnClickListener(this::goToEventNews);
        getView().findViewById(R.id.homeFriendsButton).setOnClickListener(this::goToFriends);
        getView().findViewById(R.id.track_friends_button).setOnClickListener(v -> gpsIsEnabled());
    }

    public void gotToProfile(View view){
        goToActivity(UserTabActivity.class);
    }

    public void goToEventCreation(View view){
        goToActivity(GroupCreateActivity.class);
    }

    private void goToActivity(Class<?> activity){
        if(!isConnected()){
            createLoginPop(activity);
        } else {
            Intent intent = new Intent(getContext(), activity);
            startActivity(intent);
        }
    }

    public void goToFriends(View view){
        goToActivity(FriendsActivity.class);
    }

    // Temporary
    public void goToEventNews(View view){
        Intent intent = new Intent(getContext(), EventNewsActivity.class);
        intent.putExtra(UUID_KEY, "361f8d6f-ccf0-4ee3-a596-d62a910874f6");
        startActivity(intent);
    }

    private boolean isConnected(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private final static String POPUP_TITLE = "Connection Alert";
    private final static String POPUP_MESSAGE = "To do so, a user account is required. Please connect";
    private final static String POPUP_POSITIVE_BUTTON = "Log in / Register";
    private final static String POPUP_NEGATIVE_BUTTON = "Cancel";


    private void createLoginPop(Class<?> activity){
        AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
        popup.setCancelable(true);
        popup.setTitle(POPUP_TITLE);
        popup.setMessage(POPUP_MESSAGE);
        popup.setPositiveButton(POPUP_POSITIVE_BUTTON, (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.putExtra(NEXT_ACTIVITY_EXTRA_KEY, activity);
            startActivity(intent);
        });
        popup.setNegativeButton(POPUP_NEGATIVE_BUTTON, (dialog, which) -> dialog.cancel());
        popup.show();

    }

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 3001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3002;
    private boolean locationServicesEnabled;

    private void sendGPSEnablerAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("This feature cannot be accessed without GPS. Do you want to enable it ?").setCancelable(false).setTitle("GPS Alert")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent enableGPS = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGPS, PERMISSIONS_REQUEST_ENABLE_GPS);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void gpsIsEnabled() {
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            sendGPSEnablerAlert();
        }
        else {
            getLocationPermission();
        }
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationServicesEnabled = true;
            goToActivity(FriendsTrackerActivity.class);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(locationServicesEnabled){
                    goToActivity(FriendsTrackerActivity.class);
                }
                else {
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        locationServicesEnabled = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationServicesEnabled = true;
                }
            }
        }
    }

}