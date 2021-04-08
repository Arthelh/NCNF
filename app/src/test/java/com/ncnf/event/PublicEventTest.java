package com.ncnf.event;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.organizer.PublicOrganizer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class PublicEventTest {

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    GeoPoint geoPoint = new GeoPoint(0., 0.);
    String address = "north pole";
    Event.Type type = Event.Type.Conference;
    String description = "Event description goes here";
    String ownerID = "00";

    @Test
    public void publicEventGeneratesCorrectly() {

        PublicEvent event = new PublicEvent(ownerID,name, date, geoPoint,address,description, type, 0 , 0);
        assertEquals(event.getOwnerId(), ownerID);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), geoPoint);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);
        assertEquals(event.getMinAge(), 0);
        assertEquals(event.getPrice(), 0);
        assertEquals(event.getNumOfAttendees(), 0);
        assertEquals(event.getAttendees().size(), 0);

        assertEquals(event.getVisibility(), Event.Visibility.PUBLIC);

    }

    @Test
    public void publicEventRejectsOnInvalidAge() {
        assertThrows(IllegalArgumentException.class, () -> new PublicEvent(ownerID,name, date, geoPoint,address,description, type, -1, 0));
    }

    @Test
    public void setMinAgeRejectsOnInvalidAge() {
        PublicEvent event = new PublicEvent(ownerID,name, date, geoPoint,address,description, type, 0 , 0);
        assertThrows(IllegalArgumentException.class, () -> event.setMinAge(-1));
    }

    @Test
    public void setMinAgeWorks() {
        PublicEvent event = new PublicEvent(ownerID,name, date, geoPoint,address,description, type, 0 , 0);
        event.setMinAge(18);
        assertEquals(event.getMinAge(), 18);
    }

    @Test
    public void addTagWorks() {
        PublicEvent event = new PublicEvent(ownerID,name, date, geoPoint,address,description, type, 0 , 0);
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        event.addTag(tag);
        assertTrue(event.getTags().contains(tag));
    }

    @Test
    public void addTagRejectsOnDuplicateTag() {
        PublicEvent event = new PublicEvent(ownerID,name, date, geoPoint,address,description, type, 0 , 0);
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        event.addTag(tag);
        assertThrows(IllegalArgumentException.class, () -> event.addTag(tag));
    }

    @Test
    public void setTagWorks() {
        List<Tag> list = new ArrayList<Tag>();
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        Tag tag2 = new Tag("\uD83C\uDFB8", "Folk Music");
        list.add(tag);
        list.add(tag2);
        PublicEvent event = new PublicEvent(ownerID,name, date, geoPoint,address,description, type, 0 , 0);
        event.setTags(list);

        List<Tag> result = event.getTags();
        assertEquals(result.size(), list.size());
        for(int i = 0; i < result.size(); ++i) {
            assertEquals(list.get(i), result.get(i));
        }

    }

    @Test
    public void basicSettersWork() {
        PublicEvent event = new PublicEvent(ownerID,name, date, geoPoint,address,description, type, 0 , 0);
        List<String> attendees = new ArrayList<>();
        Date newDate = new Date(2021, 3, 12);
        GeoPoint newGeoPoint = new GeoPoint(1., 1.);

        attendees.add("Mary");
        event.setName("Christmas Party");
        event.setPrice(4);
        event.setAttendees(attendees);
        event.setDate(newDate);
        event.setOwnerId("EPFL-IC");
        event.setLocation(newGeoPoint);
        event.setDescription("Another description");

        assertEquals(event.getDate(), newDate);
        assertEquals(event.getName(), "Christmas Party");
        assertEquals(event.getLocation(), newGeoPoint);
        assertEquals(event.getDescription(), "Another description");
        assertEquals(event.getOwnerId(), "EPFL-IC");
        assertEquals(event.getNumOfAttendees(), 1);
        assertTrue(event.getAttendees().size() == attendees.size());
        for(int i = 0; i < attendees.size(); ++i) {
            assertEquals(attendees.get(i), event.getAttendees().get(i));
        }
    }


}
