package com.example.bootcamp;

import androidx.annotation.Nullable;

public class Location {

    private double longitude;
    private double latitude;
    private String address;

    public Location(double longitude, double latitude, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Location otherLoc = (Location) obj;
        return (otherLoc.getLatitude() == latitude && otherLoc.longitude == longitude);
    }
}
