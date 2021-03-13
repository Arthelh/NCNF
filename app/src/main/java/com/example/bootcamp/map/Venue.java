package com.example.bootcamp.map;

public class Venue {

    private String name;
    private float latitude;
    private float longitude;

    public Venue(String name, float latitude, float longitude) {
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
