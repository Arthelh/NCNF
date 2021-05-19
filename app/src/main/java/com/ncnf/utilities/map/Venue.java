package com.ncnf.utilities.map;

import java.util.Objects;

public class Venue {

    private final String name;
    private final float latitude;
    private final float longitude;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return Float.compare(venue.latitude, latitude) == 0 &&
                Float.compare(venue.longitude, longitude) == 0 &&
                Objects.equals(name, venue.name);
    }
}
