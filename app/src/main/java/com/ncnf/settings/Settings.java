package com.ncnf.settings;

import com.google.android.gms.maps.model.LatLng;

public class Settings {

    //Distance in km
    public final static int MAX_ACCEPTED_DISTANCE = 100;
    private static int current_max_distance = 25;
    public static LatLng userPosition = new LatLng(46.526120f, 6.576330f);

    public static void setCurrent_max_distance(int current_max_distance) {
        Settings.current_max_distance = current_max_distance;
    }

    public static int getCurrent_max_distance() {
        return current_max_distance;
    }

    public static void setUserPosition(LatLng userPosition) {
        Settings.userPosition = userPosition;
    }

    public static LatLng getUserPosition(){
        return Settings.userPosition;
    }

}
