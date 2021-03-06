package com.ncnf.models;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class EventTest {

    String name = "Jane Doe";
    LocalDateTime date = LocalDateTime.of(2021, 3, 11, 12, 0);
    GeoPoint geoPoint = new GeoPoint(0., 0.);
    String address = "north pole";
    Event.Type type = Event.Type.Conference;
    String description = "SocialObject description goes here";
    String ownerID = "00";
    UUID uuid = UUID.randomUUID();
    List<String> attendees = new ArrayList<>();
    int minAge = 0;
    int price = 0;

    FirebaseDatabase db;
    Event mainEvent;
    CompletableFuture<Boolean> response;
    CompletableFuture<Boolean> response2;

    @Before
    public void setup(){
        db = Mockito.mock(FirebaseDatabase.class);
        mainEvent = new Event(ownerID, uuid, name, date, geoPoint, address, description, type, attendees, minAge, price, "test@email.com");
        response = CompletableFuture.completedFuture(true);
    }

    @Test
    public void storeEventWorks() {
        when(db.setDocument(anyString(), anyMap())).thenReturn(response);

        CompletableFuture<Boolean> storeTest = mainEvent.store(db);

        try {
            assertTrue(storeTest.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly ! " + e.getMessage());
        }
    }

    @Test
    public void publicEventGeneratesCorrectly() {

        Event event = new Event(ownerID,name, date, geoPoint, address, description, type, 0, 0, "test@email.com");
        assertEquals(event.getOwnerId(), ownerID);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), geoPoint);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);
        assertEquals(event.getMinAge(), 0);
        assertEquals(event.getPrice(), 0, 0);
        assertEquals(event.getNumOfAttendees(), 0);
        assertEquals(event.getAttendees().size(), 0);

    }

    @Test
    public void secondaryConstructorTest(){
        attendees.add("Attendee1");

        Event event = new Event(ownerID, uuid, name, date, geoPoint, address, description, type, attendees, minAge, price, "test@email.com");
        assertEquals(event.getUuid(), uuid);
        assertEquals(event.getOwnerId(), ownerID);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), geoPoint);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);

        assertEquals(event.getNumOfAttendees(), 1);
        assertEquals(event.getAttendees().get(0), "Attendee1");
        assertEquals(event.getAttendees().size(), 1);

    }

    @Test
    public void publicEventRejectsOnInvalidAge() {
        assertThrows(IllegalArgumentException.class, () -> new Event(ownerID,name, date, geoPoint,address,description, type, -1, 0, "test@email.com"));
    }

    @Test
    public void setMinAgeRejectsOnInvalidAge() {
        Event event = new Event(ownerID,name, date, geoPoint,address,description, type, 0 , 0, "test@email.com");
        assertThrows(IllegalArgumentException.class, () -> event.setMinAge(-1));
    }

    @Test
    public void setMinAgeWorks() {
        Event event = new Event(ownerID,name, date, geoPoint,address,description, type, 0 , 0, "test@email.com");
        event.setMinAge(18);
        assertEquals(event.getMinAge(), 18);
    }

    @Test
    public void basicSettersGettersWork() {
        Event event = new Event(ownerID,name, date, geoPoint,address,description, type, 0 , 0, "test@email.com");
        List<String> attendees = new ArrayList<>();
        LocalDateTime newDate = LocalDateTime.of(2021, 3, 12, 12, 0);
        GeoPoint newGeoPoint = new GeoPoint(1., 1.);

        attendees.add("Mary");
        event.setName("Christmas Party");
        event.setPrice(4);
        event.setAttendees(attendees);
        event.setDate(newDate);
        event.setOwnerId("EPFL-IC");
        event.setLocation(newGeoPoint);
        event.setDescription("Another description");
        event.setEmail("newEmail@test.ch");

        assertEquals(event.getDate(), newDate);
        assertEquals(event.getName(), "Christmas Party");
        assertEquals(event.getLocation(), newGeoPoint);
        assertEquals(event.getDescription(), "Another description");
        assertEquals(event.getOwnerId(), "EPFL-IC");
        assertEquals(event.getEmail(), "newEmail@test.ch");
        assertEquals(event.getNumOfAttendees(), 1);
        assertEquals(event.getAttendees().size(), attendees.size());
        for(int i = 0; i < attendees.size(); ++i) {
            assertEquals(attendees.get(i), event.getAttendees().get(i));
        }
    }

    @Test
    public void compareToWorks() {
        Event event = new Event("ownerId", name, date, geoPoint, address, description, type, 0, 0, "test@email.com");
        LocalDateTime date2 = LocalDateTime.of(2021, 3, 30, 12, 0);
        Event event2 = new Event("ownerId", name, date2, geoPoint, address, description, type, 0, 0, "test@email.com");

        assertEquals(event.compareTo(event2), -19);
    }
}
