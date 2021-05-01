package com.ncnf.event;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static org.junit.Assert.*;
public class SocialCalculatorTest {

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    GeoPoint geoPoint = new GeoPoint(0., 0.);
    String address = "north pole";
    Social.Type type = Social.Type.Conference;
    String description = "Social description goes here";
    String ownerId = "00";

    @Test
    public void eventRelevanceCalculatorWorks() {

        Event event1 = new Event(ownerId, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        Event event2 = new Event(ownerId, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        Event event3 = new Event(ownerId, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");

        event1.addTag(new Tag("\uD83D\uDCF1", "Technology"));
        event1.addTag(new Tag("\uD83D\uDCF1", "Conference"));
        event1.addTag(new Tag("\uD83D\uDCF1", "Artificial Intelligence"));

        event2.addTag(new Tag("\uD83D\uDCF1", "Opera"));
        event2.addTag(new Tag("\uD83D\uDCF1", "Singing"));
        event2.addTag(new Tag("\uD83D\uDCF1", "Art"));

        event3.addTag(new Tag("\uD83D\uDCF1", "Art"));

        List<Social> l = new ArrayList<>();
        l.add(event1);
        l.add(event2);
        l.add(event3);

        EventRelevanceCalculator rc = new EventRelevanceCalculator(l);

        assertTrue(rc.getSortedList().get(0).equals(event2) || rc.getSortedList().get(1).equals(event2));
        assertTrue(rc.getSortedList().get(0).equals(event3) || rc.getSortedList().get(1).equals(event3));
        assertTrue(rc.getSortedList().get(2).equals(event1));
    }


}
