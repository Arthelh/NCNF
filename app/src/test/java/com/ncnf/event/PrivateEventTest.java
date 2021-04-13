package com.ncnf.event;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

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

    DatabaseService db;
    PrivateEvent mainEvent;
    CompletableFuture<DatabaseResponse> response;

    @Before
    public void setup(){
        db = Mockito.mock(DatabaseService.class);
        mainEvent = new PrivateEvent(ownerId,name, date, geoPoint,address,description, type);
        response = CompletableFuture.completedFuture(new DatabaseResponse(true, false, null));
    }

    @Test
    public void storeEventWorks(){
        when(db.setDocument(anyString(), anyMap())).thenReturn(response);

        CompletableFuture<DatabaseResponse> storeTest = mainEvent.store(db);

        try {
            assertEquals(true, storeTest.get().isSuccessful());
            assertEquals(false, storeTest.get().getResult());
        } catch (ExecutionException | InterruptedException e){
            Assert.fail("The future did not complete correctly ! " + e.getMessage());
        }
    }


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

        assertEquals(event.getVisibility(), Event.Visibility.PRIVATE);
    }

//    @Test
//    public void serializationWorksPrivateEvent() {
//        PrivateEvent event = new PrivateEvent("00",name, date, geoPoint,address,description, type);
//        DateAdapter adapter = new DateAdapter(date);
//
//        String serialized = "PRIVATE@" + event.getUuid().toString() + "@" + name + "@" + adapter.toString() + "@" + loc.getLongitude() + " " + loc.getLatitude() + " " + loc.getAddress() + "@" + description + "@" + EventType.Conference.toString() + "@" + owner.getName() + "@" + "swan_lake";
//        assertEquals(event.toString(), serialized);
//        assertEquals(Event.toEvent(serialized), event);
//    }

    @Test
    public void inviteWorks() {
        PrivateEvent event = new PrivateEvent("00",name, date, geoPoint,address,description, type);
        event.invite("Sarah");
        assertEquals(event.getInvited().size(), 1);
        assertEquals(event.getInvited().get(0), "Sarah");
    }

    @Test
    public void compareToWorks() {
        PrivateEvent event = new PrivateEvent("00",name, date, geoPoint,address,description, type);
        Date date2 = new Date(2021, 03, 30);
        PrivateEvent event2 = new PrivateEvent("00",name, date2, geoPoint,address,description, type);

        assertEquals(event.compareTo(event2), -1);
    }

    @Test
    public void setType(){
        PrivateEvent event = new PrivateEvent("00",name, date, geoPoint,address,description, type);
        assertEquals(type, event.getType());
        event.setType(Event.Type.Movie);
        assertEquals(Event.Type.Movie, event.getType());
    }

    @Test
    public void setAddress(){
        PrivateEvent event = new PrivateEvent("00",name, date, geoPoint,address,description, type);
        assertEquals(address, event.getAddress());
        event.setAddress("this is the new address");
        assertEquals("this is the new address", event.getAddress());
    }
}
