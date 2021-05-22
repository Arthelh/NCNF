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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class GroupTest {

    String name = "Jane Doe";
    LocalDateTime date = LocalDateTime.of(2021, 03, 11, 12, 0);
    GeoPoint geoPoint = new GeoPoint(0., 0.);
    String address = "north pole";
    SocialObject.Type type = SocialObject.Type.Conference;
    String description = "SocialObject description goes here";
    String ownerId = "00";
    UUID uuid = UUID.randomUUID();
    List<String> attendees = new ArrayList<>();
    List<String> invited = new ArrayList<>();

    FirebaseDatabase db;
    Group mainEvent;
    CompletableFuture<Boolean> response;

    @Before
    public void setup(){
        db = Mockito.mock(FirebaseDatabase.class);
        mainEvent = new Group(ownerId,name, date, geoPoint,address,description, type);
        response = CompletableFuture.completedFuture(true);
    }

    @Test
    public void storeEventWorks(){
        when(db.setDocument(anyString(), anyMap())).thenReturn(response);

        CompletableFuture<Boolean> storeTest = mainEvent.store(db);

        try {
            assertTrue(storeTest.get());
        } catch (ExecutionException | InterruptedException e){
            Assert.fail("The future did not complete correctly ! " + e.getMessage());
        }
    }


    @Test
    public void privateEventGeneratesCorrectly() {
        Group event = new Group(ownerId,name, date, geoPoint,address,description, type);
        assertEquals(event.getOwnerId(), ownerId);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), geoPoint);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);
        assertEquals(event.getNumOfAttendees(), 0);
        assertEquals(event.getAttendees().size(), 0);
        assertEquals(event.getInvited().size(), 0);

    }

    @Test
    public void secondaryConstructorTest(){
        attendees.add("Attendee1");
        invited.add("Invited1");

        Group event = new Group(ownerId, uuid, name, date, geoPoint, address, type, attendees, description, invited);
        assertEquals(event.getUuid(), uuid);
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

    }

    @Test
    public void inviteWorks() {
        Group event = new Group("00",name, date, geoPoint,address,description, type);
        event.invite("Sarah");
        assertEquals(event.getInvited().size(), 1);
        assertEquals(event.getInvited().get(0), "Sarah");
    }

    @Test
    public void compareToWorks() {
        Group event = new Group("00",name, date, geoPoint,address,description, type);
        LocalDateTime date2 = LocalDateTime.of(2021, 03, 30, 12, 00);
        Group event2 = new Group("00",name, date2, geoPoint,address,description, type);

        assertEquals(event.compareTo(event2), -19);
    }

    @Test
    public void setType(){
        Group event = new Group("00",name, date, geoPoint,address,description, type);
        assertEquals(type, event.getType());
        event.setType(SocialObject.Type.Movie);
        assertEquals(SocialObject.Type.Movie, event.getType());
    }

    @Test
    public void setAddress(){
        Group event = new Group("00",name, date, geoPoint,address,description, type);
        assertEquals(address, event.getAddress());
        event.setAddress("this is the new address");
        assertEquals("this is the new address", event.getAddress());
    }
}
