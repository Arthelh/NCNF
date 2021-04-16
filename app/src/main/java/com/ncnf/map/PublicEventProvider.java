package com.ncnf.map;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.event.Event;
import com.ncnf.event.PublicEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class PublicEventProvider {

    @Inject
    public PublicEventProvider() {}

    public List<PublicEvent> getAll() {
        ArrayList<PublicEvent> list = new ArrayList<>();

        // PublicEvent constructor: PublicEvent(String ownerId, String name, Date date, GeoPoint location, String address, String description, Type type, int minAge, int price)

        String ownerId = "lol";
        Date date = new Date();

        list.add(new PublicEvent(ownerId, "Math Conference", date, new GeoPoint(46.5191f, 6.5668f), "EPFL", "Math Conference", Event.Type.Conference, 0, 0));
        list.add(new PublicEvent(ownerId, "Les Noces de Figaro", date, new GeoPoint(46.5180f, 6.6369f), "Lausanne Opera", "Opera Play", Event.Type.Opera, 0, 0));
        list.add(new PublicEvent(ownerId, "Caravan Palace", date, new GeoPoint(46.5224f, 6.6193f), "Les Docks", "Concert", Event.Type.NOTHING, 0, 0));
        list.add(new PublicEvent(ownerId, "Cours de danse", date, new GeoPoint(46.5227f, 6.6216f), "Théâtre Arsenic", "Dance Lesson", Event.Type.NOTHING, 0, 0));
        return list;
    }

}
