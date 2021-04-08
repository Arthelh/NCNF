package com.ncnf.event;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.organizer.PublicOrganizer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PrivateEventTest {

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    GeoPoint geoPoint = new GeoPoint(0., 0.);
    String address = "north pole";
    Event.Type type = Event.Type.Conference;
    String description = "Event description goes here";
    String ownerId = "00";
    UUID uuid = UUID.randomUUID();
    List<String> attendees = new ArrayList<>();
    List<String> invited = new ArrayList<>();


    @Test
    public void privateEventGeneratesCorrectly() {
        PrivateEvent event = new PrivateEvent(ownerId,name, date, geoPoint,address,description, type);
        assertEquals(event.getOwnerId(), ownerId);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), geoPoint);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);
        assertEquals(event.getNumOfAttendees(), 0);
        assertEquals(event.getAttendees().size(), 0);
        assertEquals(event.getInvited().size(), 0);

        assertEquals(event.getVisibility(), Event.Visibility.PRIVATE);
    }

    @Test
    public void secondaryConstructorTest(){
        attendees.add("Attendee1");
        invited.add("Invited1");

        PrivateEvent event = new PrivateEvent(ownerId, uuid, name, date, geoPoint, address, type, attendees, description, invited);
        assertEquals(event.getOwnerId(), ownerId);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), geoPoint);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);

        assertEquals(event.getNumOfAttendees(), 1);
        assertEquals(event.getAttendees().get(0), "Attendee1");
        assertEquals(event.getAttendees().size(), 1);

        assertEquals(event.getInvited().size(), 1);
        assertEquals(event.getInvited().get(0), "Invited1");

        assertEquals(event.getVisibility(), Event.Visibility.PRIVATE);
    }

    @Test
    public void inviteWorks() {
        PrivateEvent event = new PrivateEvent("00",name, date, geoPoint,address,description, type);
        event.invite("Sarah");
        assertEquals(event.getInvited().size(), 1);
        assertEquals(event.getInvited().get(0), "Sarah");
    }
}
