package com.example.bootcamp.map;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class VenueProvider {

    @Inject
    public VenueProvider() {

    }

    public List<Venue> getAll() {
        ArrayList<Venue> list = new ArrayList<>();
        //list.add(new PlaceholderEvent("Roméo et Juliette, Theatre du Leman", 46.2101f, 6.1510f));
        list.add(new Venue("EPFL", 46.5191f, 6.5668f));
        list.add(new Venue("Lausanne Opera", 46.5180f, 6.6369f));
        list.add(new Venue("Les Docks", 46.5224f, 6.6193f));
        list.add(new Venue("Espace Arsenic", 46.5227f, 6.6216f));
        list.add(new Venue("Théâtre Sévelin 36", 46.5225f, 6.6197f));
        list.add(new Venue("MAD", 46.5219f, 6.6272f));
        list.add(new Venue("UniL", 46.5211f, 6.5802f));
        return list;
    }
}
