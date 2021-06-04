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
    String description = "SocialObject description goes here";
    String ownerId = "00";
    UUID uuid = UUID.randomUUID();
    List<String> members = new ArrayList<>();

    FirebaseDatabase db;
    Group mainEvent;
    CompletableFuture<Boolean> response;

    @Before
    public void setup(){
        db = Mockito.mock(FirebaseDatabase.class);
        mainEvent = new Group(ownerId,name, date, geoPoint,address,description);
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
        Group group = new Group(ownerId,name, date, geoPoint,address,description);
        assertEquals(group.getOwnerId(), ownerId);
        assertEquals(group.getDate(), date);
        assertEquals(group.getName(), name);
        assertEquals(group.getLocation(), geoPoint);
        assertEquals(group.getDescription(), description);
        assertEquals(group.getMembers().size(), 1);
    }

    @Test
    public void secondaryConstructorTest(){
        members.add("Invited1");

        Group group = new Group(ownerId, uuid, name, date, geoPoint, address, description, members);
        assertEquals(group.getUuid(), uuid);
        assertEquals(group.getOwnerId(), ownerId);
        assertEquals(group.getDate(), date);
        assertEquals(group.getName(), name);
        assertEquals(group.getLocation(), geoPoint);
        assertEquals(group.getDescription(), description);
        assertEquals(group.getMembers().size(), 2);
        assertEquals(group.getMembers().get(0), ownerId);

    }

    @Test
    public void inviteWorks() {
        Group event = new Group("00",name, date, geoPoint,address,description);
        event.addMember("Sarah");
        assertEquals(event.getMembers().size(), 2);
        assertEquals(event.getMembers().get(0), "00");
    }

    @Test
    public void compareToWorks() {
        Group event = new Group("00",name, date, geoPoint,address,description);
        LocalDateTime date2 = LocalDateTime.of(2021, 03, 30, 12, 00);
        Group event2 = new Group("00",name, date2, geoPoint,address,description);

        assertEquals(event.compareTo(event2), -19);
    }

    @Test
    public void setAddress(){
        Group event = new Group("00",name, date, geoPoint,address,description);
        assertEquals(address, event.getAddress());
        event.setAddress("this is the new address");
        assertEquals("this is the new address", event.getAddress());
    }
}
