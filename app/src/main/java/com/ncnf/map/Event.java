package com.ncnf.map;

public class Event {

    private final String name;
    private final float latitude;
    private final float longitude;

    public Event(String name, float latitude, float longitude){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

}
