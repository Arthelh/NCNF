package com.ncnf.user;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.event.PrivateEvent;
import com.ncnf.event.PublicEvent;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.utilities.StringCodes.ADDRESS_KEY;
import static com.ncnf.utilities.StringCodes.ATTENDEES_KEY;
import static com.ncnf.utilities.StringCodes.BIRTH_YEAR_KEY;
import static com.ncnf.utilities.StringCodes.DATE_KEY;
import static com.ncnf.utilities.StringCodes.DESCRIPTION_KEY;
import static com.ncnf.utilities.StringCodes.FIRST_NAME_KEY;
import static com.ncnf.utilities.StringCodes.INVITED_KEY;
import static com.ncnf.utilities.StringCodes.LAST_NAME_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.utilities.StringCodes.OWNER_KEY;
import static com.ncnf.utilities.StringCodes.SAVED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.TYPE_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;
import static com.ncnf.utilities.StringCodes.VISIBILITY_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserTests {

    DatabaseService db = mock(DatabaseService.class);
    String ownerID = "ownerId";
    String name = "name";
    Date date = new Date();
    GeoPoint geoPoint = new GeoPoint(0,0);
    String address = "address";
    String description = "description";
    Event.Type type = Event.Type.Movie;
    PublicEvent publicEvent = new PublicEvent(ownerID, name, date, geoPoint, address, description, type, 0 , 0, "test@email.com");
    PrivateEvent privateEvent = new PrivateEvent(ownerID, name, date, geoPoint, address, description, type);
    CompletableFuture<DatabaseResponse> response = CompletableFuture.completedFuture(new DatabaseResponse(true, null, null));


    @Test
    public void createPublicEventWorks(){
        when(db.setDocument(anyString(), anyMap())).thenReturn(response);
        when(db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(response);

        User user = new User(ownerID, db);
        CompletableFuture<CompletableFuture<DatabaseResponse>> response = user.createEvent(publicEvent);
        try {
            assertTrue(response.get().get().isSuccessful());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void createPrivateEventWorks(){
        when(db.setDocument(anyString(), anyMap())).thenReturn(response);
        when(db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(response);

        User user = new User(ownerID, db);
        CompletableFuture<CompletableFuture<DatabaseResponse>> response = user.createEvent(privateEvent);
        try {
            assertTrue(response.get().get().isSuccessful());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void crashesWhenWrongId(){
        when(db.setDocument(anyString(), anyMap())).thenReturn(response);
        when(db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(response);

        User user = new User("owner", db);
        CompletableFuture<CompletableFuture<DatabaseResponse>> response = user.createEvent(privateEvent);
        try {
            DatabaseResponse done = response.get().get();
            assertFalse(done.isSuccessful());
            assertTrue(done.getException() instanceof IllegalStateException);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void crashesWhenSetDocumentCrashes(){
        CompletableFuture<DatabaseResponse> wrongResponse = CompletableFuture.completedFuture(new DatabaseResponse(false, null, null));
        when(db.setDocument(anyString(), anyMap())).thenReturn(wrongResponse);

        User user = new User("owner", db);
        CompletableFuture<CompletableFuture<DatabaseResponse>> response = user.createEvent(privateEvent);
        try {
            DatabaseResponse done = response.get().get();
            assertFalse(done.isSuccessful());
            assertTrue(done.getException() instanceof Exception);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void hashCodeMatches() {
        User u1 = new User(db, "1234567890","foo@bar.com");
        User u2 = new User(db, "1234567890","foo@bar.com");

        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void equalsMatches() {
        User u1 = new User(db, "1234567890","foo@bar.com");
        User u2 = new User(db, "1234567890","foo@bar.com");

        assertEquals(u1, u2);
        assertEquals(u1, u1);
    }

    @Test
    public void equalsFails() {
        User u1 = new User(db, "1234567890","foo@bar.com");
        User u2 = new User(db, "1234567890","foo@bal.com");

        assertNotEquals(u1, u2);

        u1 = new User(db, "1234567890","foo@bar.com");
        u2 = new User(db, "0000000000","foo@bar.com");

        assertNotEquals(u1, u2);

        assertNotEquals(u1, new Object());
    }

    @Test
    public void nullArgumentsThrows() {
        assertThrows(IllegalStateException.class, () -> new User(db, "1234567890", null));
        assertThrows(IllegalStateException.class, () -> new User(db,null,"foo@bar.com"));
        assertThrows(IllegalStateException.class, () -> new User(db, "", ""));
    }

    @Test
    public void getId() {
        String id = "1234567890";
        User user = new User(db, "1234567890","foo@bar.com");
        assertEquals(id, user.getID());
    }

    @Test
    public void getEmail() {
        String email = "foo@bar.com";
        User user = new User(db, "1234567890","foo@bar.com");
        assertEquals(email, user.getEmail());
    }

    @Test
    public void saveUserCallsDatabase() {
        when(db.setDocument(anyString(), anyMap())).thenReturn(new CompletableFuture());
        User user = new User(db, "1234567890","foo@bar.com");
        user.saveUserToDB();
        verify(db).setDocument(anyString(), anyMap());
    }

    @Test
    public void getFieldCallsDatabase() {
        when(db.getField(anyString(), anyString())).thenReturn(new CompletableFuture<>());
        User user = new User(db, "1234567890","foo@bar.com");
        user.getField(FIRST_NAME_KEY);
        verify(db).getField(USERS_COLLECTION_KEY + "1234567890", FIRST_NAME_KEY);
    }

    @Test
    public void updateLastNameCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(db, "1234567890","foo@bar.com");
        user.updateLastName("Doe");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", LAST_NAME_KEY, "Doe");
    }

    @Test
    public void updateFirstNameCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(db, "1234567890","foo@bar.com");
        user.updateFirstName("John");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", FIRST_NAME_KEY, "John");
    }

    @Test
    public void updateBirthCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(db, "1234567890","foo@bar.com");
        user.updateBirth(2011);
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", BIRTH_YEAR_KEY, 2011);
    }

    @Test
    public void updateNotificationsCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(db, "1234567890","foo@bar.com");
        user.updateNotifications(true);
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_KEY, true);
    }

    @Test
    public void updateNotificationsTokenCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(db, "1234567890","foo@bar.com");
        user.updateNotificationsToken("My token");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_TOKEN_KEY, "My token");
    }

    @Test
    public void loadUserCallsDatabase() {
        when(db.getData(anyString())).thenReturn(new CompletableFuture<>());
        User user = new User(db, "1234567890","foo@bar.com");
        user.loadUserFromDB();
        verify(db).getData(USERS_COLLECTION_KEY + "1234567890");
    }

    @Test
    public void saveEventCallsDatabase() {
        when(db.updateArrayField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        Event event = new PublicEvent("00","Conference", new Date(), new GeoPoint(0., 0.) ,"North Pole", "Description goes here", Event.Type.Movie, 0 , 0, "test@email.com");
        User user = new User(db, "1234567890","foo@bar.com");
        user.saveEvent(event);
        verify(db).updateArrayField(eq(USERS_COLLECTION_KEY  + "1234567890"), eq(SAVED_EVENTS_KEY), anyObject());
    }

    @Test
    public void secondConstructorAndSetTest(){
        UUID uuid = UUID.randomUUID();
        String email = "i@i.com";
        User user = new User(uuid.toString(), db);
        assertEquals(user.getID(), uuid.toString());
        user.setEmail(email);
        assertEquals(user.getEmail(), email);
    }

    @Test
    public void throwsException(){
        UUID uuid = UUID.randomUUID();
        User user = new User(uuid.toString(), db);

        CompletableFuture<DatabaseResponse> query = user.saveUserToDB();
        query = user.saveUserToDB();
        try {
            assertEquals(query.get().isSuccessful(), false);
            assertTrue(query.get().getException() instanceof IllegalStateException);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }

        user.setEmail("");
        query = user.saveUserToDB();
        try {
            assertEquals(query.get().isSuccessful(), false);
            assertTrue(query.get().getException() instanceof IllegalStateException);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }

        user.setEmail("i@i.com");
        when(db.setDocument(anyString(), anyMap())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(true, null, null)));
        query = user.saveUserToDB();
        try {
            assertEquals(query.get().isSuccessful(), true);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void allEventsAreRetrieved(){
        String ownerId = "ownerId";
        Map<String, Object> event = new HashMap<>();
        String uuid = UUID.randomUUID().toString();
        String name = "name";
        Date date = new Date();
        GeoPoint location = new GeoPoint(0,0);
        String address = "address";
        String type = "Conference";
        List<String> attendees = new ArrayList<>(Collections.singleton("attendee1"));
        String description = "description";
        List<String> invited = new ArrayList<>(Collections.singleton("invited"));


        event.put(OWNER_KEY, ownerId);
        event.put(UUID_KEY, uuid);
        event.put(NAME_KEY, name);
        event.put(DATE_KEY, new Timestamp(date));
        event.put(LOCATION_KEY, location);
        event.put(ADDRESS_KEY, address);
        event.put(VISIBILITY_KEY, "PRIVATE");
        event.put(TYPE_KEY, type);
        event.put(ATTENDEES_KEY, attendees);
        event.put(DESCRIPTION_KEY, description);
        event.put(OWNER_KEY, ownerId);
        event.put(INVITED_KEY, invited);
        List<String> events = new ArrayList<>();
        events.add("hello");
        events.add("Hello2");

        User user = new User(this.db, UUID.randomUUID().toString(), "i@i.com");
        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(true, event, null)));
        when(db.getField(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(true, events, null)));
        CompletableFuture<CompletableFuture<List<Event>>> eventList = user.getAllEvents(SAVED_EVENTS_KEY);
        try{
            List<Event> list = eventList.get().get();
            assertTrue(list.size() != 0);
            PrivateEvent eventInstance = (PrivateEvent) list.get(0);
            assertEquals(eventInstance.getUuid().toString(), uuid);
            eventInstance = (PrivateEvent) list.get(1);
            assertEquals(eventInstance.getDate(), date);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getAllEventsFailure(){
        User user = new User(this.db, UUID.randomUUID().toString(), "i@i.com");
        when(db.getField(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(false, null, null)));
        CompletableFuture<CompletableFuture<List<Event>>> eventList = user.getAllEvents(SAVED_EVENTS_KEY);
        try{
            List<Event> list = eventList.get().get();
            assertEquals(list, null);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

}
