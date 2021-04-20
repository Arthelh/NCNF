package com.ncnf.event;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.ADDRESS_KEY;
import static com.ncnf.Utils.ATTENDEES_KEY;
import static com.ncnf.Utils.DATE_KEY;
import static com.ncnf.Utils.DESCRIPTION_KEY;
import static com.ncnf.Utils.INVITED_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.MIN_AGE_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static com.ncnf.Utils.OWNER_KEY;
import static com.ncnf.Utils.PRICE_KEY;
import static com.ncnf.Utils.TAGS_LIST_KEY;
import static com.ncnf.Utils.TYPE_KEY;
import static com.ncnf.Utils.UUID_KEY;
import static com.ncnf.Utils.VISIBILITY_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class EventBuilderTest {

    private DatabaseService db;
    private EventBuilder eventBuilder;

    Map<String, Object> publicEvent;
    Map<String, Object> privateEvent;
    String ownerId = "ownerId";
    String uuid = UUID.randomUUID().toString();
    String name = "name";
    Date date = new Date();
    GeoPoint location = new GeoPoint(0,0);
    String address = "address";
    String type = "Conference";
    List<String> attendees = new ArrayList<>(Collections.singleton("attendee1"));
    String description = "description";

    int minAge = 0;
    int price = 0;
    List<Tag> tags = new ArrayList<>(Collections.singleton(new Tag("\uD83C\uDFB8", "Rock Music")));

    List<String> invited = new ArrayList<>(Collections.singleton("invited"));

    @Before
    public void setup(){
        db = Mockito.mock(DatabaseService.class);
        eventBuilder = new EventBuilder(db);
        publicEvent = new HashMap<>();
        privateEvent = new HashMap<>();
    }

    @Test
    public void loadPublic(){
        publicEvent.put(OWNER_KEY, ownerId);
        publicEvent.put(UUID_KEY, this.uuid);
        publicEvent.put(NAME_KEY, this.name);
        publicEvent.put(DATE_KEY, new Timestamp(this.date));
        publicEvent.put(LOCATION_KEY, this.location);
        publicEvent.put(ADDRESS_KEY, this.address);
        publicEvent.put(VISIBILITY_KEY, "PUBLIC");
        publicEvent.put(TYPE_KEY, this.type);
        publicEvent.put(ATTENDEES_KEY, this.attendees);
        publicEvent.put(DESCRIPTION_KEY, this.description);
        publicEvent.put(OWNER_KEY, this.ownerId);

        publicEvent.put(MIN_AGE_KEY, minAge);
        publicEvent.put(PRICE_KEY, price);
        publicEvent.put(TAGS_LIST_KEY, tags);

        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(true, publicEvent, null)));

        CompletableFuture<Event> eventQuery = eventBuilder.build(uuid);
        PublicEvent event = null;
        try {
            event = (PublicEvent) eventQuery.get();
        } catch(Exception e){
            Assert.fail("Something went wrong with the Future");
        }

        assertEquals(event.getUuid().toString(), uuid);
        assertEquals(event.getName(), name);
        assertEquals(event.getDate(), date);
        assertEquals(event.getLocation(), location);
        assertEquals(event.getAddress(), address);
        assertEquals(event.getVisibility().toString(), "PUBLIC");
        assertEquals(event.getType().toString(), type);
        assertEquals(event.getAttendees().get(0), attendees.get(0));
        assertEquals(event.getDescription(), description);
        assertEquals(event.getOwnerId(), ownerId);
        assertEquals(event.getMinAge(), minAge);
        assertEquals(event.getPrice(), price, 0);

    }

    @Test
    public void nonSuccessfulTaskTest(){
        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(false, null, null)));

        CompletableFuture<Event> eventQuery = eventBuilder.build(uuid);
        try {
            PublicEvent event = (PublicEvent) eventQuery.get();
            assertEquals(event, null);
        } catch(Exception e){
            Assert.fail("Something went wrong with the Future");
        }
    }

    @Test
    public void ExceptionCatchTest(){
        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(true, new HashMap<>(), null)));
        CompletableFuture<Event> eventQuery = eventBuilder.build(uuid);
        try {
            PublicEvent event = (PublicEvent) eventQuery.get();
            assertEquals(event, null);
        } catch(Exception e){
            Assert.fail("Something went wrong with the Future");
        }
    }

    @Test
    public void loadPrivate(){
        privateEvent.put(OWNER_KEY, ownerId);
        privateEvent.put(UUID_KEY, this.uuid);
        privateEvent.put(NAME_KEY, this.name);
        privateEvent.put(DATE_KEY, new Timestamp(this.date));
        privateEvent.put(LOCATION_KEY, this.location);
        privateEvent.put(ADDRESS_KEY, this.address);
        privateEvent.put(VISIBILITY_KEY, "PRIVATE");
        privateEvent.put(TYPE_KEY, this.type);
        privateEvent.put(ATTENDEES_KEY, this.attendees);
        privateEvent.put(DESCRIPTION_KEY, this.description);
        privateEvent.put(OWNER_KEY, this.ownerId);

        privateEvent.put(INVITED_KEY, invited);
        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(true, privateEvent, null)));

        CompletableFuture<Event> eventQuery = eventBuilder.build(uuid);
        PrivateEvent event = null;
        try {
            event = (PrivateEvent) eventQuery.get();
        } catch(Exception e){
            Assert.fail("Something went wrong with the Future");
        }

        assertEquals(event.getUuid().toString(), uuid);
        assertEquals(event.getName(), name);
        assertEquals(event.getDate(), date);
        assertEquals(event.getLocation(), location);
        assertEquals(event.getAddress(), address);
        assertEquals(event.getVisibility().toString(), "PRIVATE");
        assertEquals(event.getType().toString(), type);
        assertEquals(event.getAttendees().get(0), attendees.get(0));
        assertEquals(event.getDescription(), description);
        assertEquals(event.getOwnerId(), ownerId);
        assertEquals(event.getInvited().get(0), "invited");
    }

    @Test
    public void failsOnFirstUnsuccessfulData(){
        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(false, null, null)));

        CompletableFuture<Event> eventQuery = eventBuilder.build(uuid);
        PrivateEvent event = null;
        try {
            event = (PrivateEvent) eventQuery.get();
        } catch(Exception e){
            Assert.fail("Something went wrong with the Future");
        }

        assertEquals(event, null);
    }



    @Test
    public void worksOnSnapshot(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        Map<String, Object> privateEvent = new HashMap<>();
        privateEvent.put(OWNER_KEY, ownerId);
        privateEvent.put(UUID_KEY, this.uuid);
        privateEvent.put(NAME_KEY, this.name);
        privateEvent.put(DATE_KEY, new Timestamp(this.date));
        privateEvent.put(LOCATION_KEY, this.location);
        privateEvent.put(ADDRESS_KEY, this.address);
        privateEvent.put(VISIBILITY_KEY, "PRIVATE");
        privateEvent.put(TYPE_KEY, this.type);
        privateEvent.put(ATTENDEES_KEY, this.attendees);
        privateEvent.put(DESCRIPTION_KEY, this.description);
        privateEvent.put(OWNER_KEY, this.ownerId);
        privateEvent.put(INVITED_KEY, invited);
        when(document.getData()).thenReturn(privateEvent);

        PrivateEvent event = (PrivateEvent) eventBuilder.eventFromSnapshot(document);

        assertEquals(event.getUuid().toString(), uuid);
        assertEquals(event.getName(), name);
        assertEquals(event.getDate(), date);
        assertEquals(event.getLocation(), location);
        assertEquals(event.getAddress(), address);
        assertEquals(event.getVisibility().toString(), "PRIVATE");
        assertEquals(event.getType().toString(), type);
        assertEquals(event.getAttendees().get(0), attendees.get(0));
        assertEquals(event.getDescription(), description);
        assertEquals(event.getOwnerId(), ownerId);
        assertEquals(event.getInvited().get(0), "invited");
    }
}
