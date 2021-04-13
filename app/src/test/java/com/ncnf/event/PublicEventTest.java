package com.ncnf.event;
import com.ncnf.organizer.PublicOrganizer;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.Location;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.organizer.PublicOrganizer;
import com.ncnf.user.PrivateUser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class PublicEventTest {

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    GeoPoint geoPoint = new GeoPoint(0., 0.);
    String address = "north pole";
    Event.Type type = Event.Type.Conference;
    String description = "Event description goes here";
    String ownerID = "00";
    UUID uuid = UUID.randomUUID();
    List<String> attendees = new ArrayList<>();
    int minAge = 0;
    int price = 0;
    List<Tag> tags = new ArrayList<>();

    DatabaseService db;
    PublicEvent mainEvent;
    CompletableFuture<DatabaseResponse> response;
    CompletableFuture<DatabaseResponse> response2;

    @Before
    public void setup(){
        db = Mockito.mock(DatabaseService.class);
        mainEvent = new PublicEvent(ownerID, uuid, name, date, geoPoint, address, description, type, attendees, minAge, price, tags);
        response = CompletableFuture.completedFuture(new DatabaseResponse(true, false, null));
    }

    @Test
    public void storeEventWorks() {
        when(db.setDocument(anyString(), anyMap())).thenReturn(response);

        CompletableFuture<DatabaseResponse> storeTest = mainEvent.store(db);

        try {
            assertEquals(true, storeTest.get().isSuccessful());
            assertEquals(false, storeTest.get().getResult());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly ! " + e.getMessage());
        }
    }

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
    public void secondaryConstructorTest(){
        attendees.add("Attendee1");
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        tags.add(tag);


        PublicEvent event = new PublicEvent(ownerID, uuid, name, date, geoPoint, address, description, type, attendees, minAge, price, tags);
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

        assertEquals(event.getTags().size(), 1);
        assertEquals(event.getTags().get(0), tag);

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

    @Test
    public void gettersWork() {

        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        assertEquals(event.getImageName(), "swan_lake");
        assertEquals(event.getPubPriv(), Event.PubPriv.valueOf("PUBLIC"));
        UUID uuid = UUID.randomUUID();
        event.setUuid(uuid);
        assertEquals(event.getUuid(), uuid);
    }

    @Test
    public void serializationWorksPublicEvent() {

        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        DateAdapter adapter = new DateAdapter(date);

        String serialized = "PUBLIC@" + event.getUuid().toString() + "@" + "Jane Doe@" + adapter.toString() + "@" + loc.getLongitude() + " " + loc.getLatitude() + " " + loc.getAddress() + "@" + description + "@" + EventType.Conference.toString() + "@" + "0" + "@" + "0" + "@" + owner.getName() + "@" + "swan_lake";
        assertEquals(event.toString(), serialized);
        assertEquals(Event.toEvent(serialized), event);

    }

    @Test
    public void filterTagsWorks() {
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        Tag tag2 = new Tag("\uD83C\uDFB8", "Folk Music");
        event.addTag(tag);
        event.addTag(tag2);

        assertTrue(event.filterTags(tag2.getName()));
        assertFalse(event.filterTags("example"));
    }

    @Test
    public void compareToWorks() {
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        Date date2 = new Date(2021, 03, 30);
        PublicEvent event2 = new PublicEvent(name, date2, loc, description, EventType.Conference,0, 0, owner, "swan_lake");

        assertEquals(event.compareTo(event2), -1);

    }





}
