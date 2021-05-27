package com.ncnf.utilities.event;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.models.Event;
import com.ncnf.models.EventTag;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
public class EventRelevanceCalculatorTests {

    String name = "Jane Doe";
    LocalDateTime date = LocalDateTime.of(2021, 3, 11, 12, 0);
    GeoPoint geoPoint = new GeoPoint(0., 0.);
    String address = "north pole";
    Event.Type type = Event.Type.Conference;
    String description = "SocialObject description goes here";
    String ownerId = "00";

    @Test
    public void eventRelevanceCalculatorWorks() {

        Event event1 = new Event(ownerId, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        Event event2 = new Event(ownerId, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        Event event3 = new Event(ownerId, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");

        event1.addTag(new EventTag("\uD83D\uDCF1", "Technology"));
        event1.addTag(new EventTag("\uD83D\uDCF1", "Conference"));
        event1.addTag(new EventTag("\uD83D\uDCF1", "Artificial Intelligence"));

        event2.addTag(new EventTag("\uD83D\uDCF1", "Opera"));
        event2.addTag(new EventTag("\uD83D\uDCF1", "Singing"));
        event2.addTag(new EventTag("\uD83D\uDCF1", "Art"));

        event3.addTag(new EventTag("\uD83D\uDCF1", "Art"));

        List<Event> l = new ArrayList<>();
        l.add(event1);
        l.add(event2);
        l.add(event3);

        EventRelevanceCalculator rc = new EventRelevanceCalculator(l);

        assertTrue(rc.getSortedList().get(0).equals(event2) || rc.getSortedList().get(1).equals(event2));
        assertTrue(rc.getSortedList().get(0).equals(event3) || rc.getSortedList().get(1).equals(event3));
        assertTrue(rc.getSortedList().get(2).equals(event1));
    }


}
