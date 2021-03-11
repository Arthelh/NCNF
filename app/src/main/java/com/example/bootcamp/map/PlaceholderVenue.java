package com.example.bootcamp.map;

import java.util.ArrayList;

public class PlaceholderVenue {

    private String name;
    private float latitude;
    private float longitude;

    public PlaceholderVenue(String name, float latitude, float longitude) {
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

    public static ArrayList<PlaceholderVenue> getTestVenues() {
        ArrayList<PlaceholderVenue> list = new ArrayList<>();
        //list.add(new PlaceholderEvent("Roméo et Juliette, Theatre du Leman", 46.2101f, 6.1510f));
        list.add(new PlaceholderVenue("EPFL", 46.5191f, 6.5668f));
        list.add(new PlaceholderVenue("Lausanne Opera", 46.5180f, 6.6369f));
        list.add(new PlaceholderVenue("Les Docks", 46.5224f, 6.6193f));
        list.add(new PlaceholderVenue("Espace Arsenic", 46.5227f, 6.6216f));
        list.add(new PlaceholderVenue("Théâtre Sévelin 36", 46.5225f, 6.6197f));
        list.add(new PlaceholderVenue("MAD", 46.5219f, 6.6272f));
        list.add(new PlaceholderVenue("UniL", 46.5211f, 6.5802f));

        return list;
    }

}
