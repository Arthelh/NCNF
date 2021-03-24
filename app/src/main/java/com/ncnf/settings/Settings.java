package com.ncnf.settings;

public class Settings {

    //Distance in km
    public final static int MAX_ACCEPTED_DISTANCE = 100;
    private static int current_max_distance = 25;

    public static void setCurrent_max_distance(int current_max_distance) {
        Settings.current_max_distance = current_max_distance;
    }

    public static int getCurrent_max_distance() {
        return current_max_distance;
    }

}
