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

import static com.ncnf.Utils.ADDRESS_KEY;
import static com.ncnf.Utils.ATTENDEES_KEY;
import static com.ncnf.Utils.BIRTH_YEAR_KEY;
import static com.ncnf.Utils.DATE_KEY;
import static com.ncnf.Utils.DESCRIPTION_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.INVITED_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.Utils.OWNED_EVENTS_KEY;
import static com.ncnf.Utils.OWNER_KEY;
import static com.ncnf.Utils.SAVED_EVENTS_KEY;
import static com.ncnf.Utils.TYPE_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;
import static com.ncnf.Utils.UUID_KEY;
import static com.ncnf.Utils.VISIBILITY_KEY;
import static org.junit.Assert.assertEquals;
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


public class PrivateUserTests {

    DatabaseService db = mock(DatabaseService.class);

    @Test
    public void hashCodeMatches() {
        PrivateUser u1 = new PrivateUser(db, "1234567890","foo@bar.com");
        PrivateUser u2 = new PrivateUser(db, "1234567890","foo@bar.com");

        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void equalsMatches() {
        PrivateUser u1 = new PrivateUser(db, "1234567890","foo@bar.com");
        PrivateUser u2 = new PrivateUser(db, "1234567890","foo@bar.com");

        assertEquals(u1, u2);
        assertEquals(u1, u1);
    }

    @Test
    public void equalsFails() {
        PrivateUser u1 = new PrivateUser(db, "1234567890","foo@bar.com");
        PrivateUser u2 = new PrivateUser(db, "1234567890","foo@bal.com");

        assertNotEquals(u1, u2);

        u1 = new PrivateUser(db, "1234567890","foo@bar.com");
        u2 = new PrivateUser(db, "0000000000","foo@bar.com");

        assertNotEquals(u1, u2);

        assertNotEquals(u1, new Object());
    }

    @Test
    public void nullArgumentsThrows() {
        assertThrows(IllegalStateException.class, () -> new PrivateUser(db, "1234567890", null));
        assertThrows(IllegalStateException.class, () -> new PrivateUser(db,null,"foo@bar.com"));
        assertThrows(IllegalStateException.class, () -> new PrivateUser(db, "", ""));
    }

    @Test
    public void getId() {
        String id = "1234567890";
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        assertEquals(id, user.getID());
    }

    @Test
    public void getEmail() {
        String email = "foo@bar.com";
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        assertEquals(email, user.getEmail());
    }

    @Test
    public void saveUserCallsDatabase() {
        when(db.setDocument(anyString(), anyMap())).thenReturn(new CompletableFuture());
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.saveUserToDB();
        verify(db).setDocument(anyString(), anyMap());
    }

    @Test
    public void getFieldCallsDatabase() {
        when(db.getField(anyString(), anyString())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.getField(FIRST_NAME_KEY);
        verify(db).getField(USERS_COLLECTION_KEY + "1234567890", FIRST_NAME_KEY);
    }

    @Test
    public void updateLastNameCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.updateLastName("Doe");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", LAST_NAME_KEY, "Doe");
    }

    @Test
    public void updateFirstNameCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.updateFirstName("John");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", FIRST_NAME_KEY, "John");
    }

    @Test
    public void updateBirthCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.updateBirth(2011);
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", BIRTH_YEAR_KEY, 2011);
    }

    @Test
    public void updateNotificationsCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.updateNotifications(true);
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_KEY, true);
    }

    @Test
    public void updateNotificationsTokenCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.updateNotificationsToken("My token");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_TOKEN_KEY, "My token");
    }

    @Test
    public void loadUserCallsDatabase() {
        when(db.getData(anyString())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.loadUserFromDB();
        verify(db).getData(USERS_COLLECTION_KEY + "1234567890");
    }

    @Test
    public void saveEventCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        Event event = new PublicEvent("00","Conference", new Date(), new GeoPoint(0., 0.) ,"North Pole", "Description goes here", Event.Type.Movie, 0 , 0);
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.saveEvent(event);
        verify(db).updateField(eq(USERS_COLLECTION_KEY  + "1234567890"), eq(SAVED_EVENTS_KEY), anyObject());
    }

    @Test
    public void ownEventCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        Event event = new PublicEvent("00","Conference", new Date(), new GeoPoint(0., 0.) ,"North Pole", "Description goes here", Event.Type.Movie, 0 , 0);
        PrivateUser user = new PrivateUser(db, "1234567890","foo@bar.com");
        user.ownEvent(event);
        verify(db).updateField(eq(USERS_COLLECTION_KEY + "1234567890"), eq(OWNED_EVENTS_KEY), anyObject());
    }

    @Test
    public void secondConstructorAndSetTest(){
        UUID uuid = UUID.randomUUID();
        String email = "i@i.com";
        PrivateUser user = new PrivateUser(db, uuid.toString());
        assertEquals(user.getID(), uuid.toString());
        user.setEmail(email);
        assertEquals(user.getEmail(), email);
    }

    @Test
    public void throwsException(){
        UUID uuid = UUID.randomUUID();
        PrivateUser user = new PrivateUser(db, uuid.toString());
        boolean nonEmail = false;
        boolean emailIsEmpty = false;
        boolean worked;

        try {
            user.saveUserToDB();
        }catch(IllegalStateException e){
            nonEmail = true;
        }

        user.setEmail("");
        try {
            user.saveUserToDB();
        }catch(IllegalStateException e){
            emailIsEmpty = true;
        }

        user.setEmail("i@i.com");
        when(db.setDocument(anyString(), anyMap())).thenReturn(CompletableFuture.completedFuture(new DatabaseResponse(true, null, null)));
        CompletableFuture<DatabaseResponse> query = user.saveUserToDB();
        try {
            assertEquals(query.get().isSuccessful(), true);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
        assertTrue(emailIsEmpty && nonEmail);

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

        PrivateUser user = new PrivateUser(this.db, UUID.randomUUID().toString(), "i@i.com");
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
        PrivateUser user = new PrivateUser(this.db, UUID.randomUUID().toString(), "i@i.com");
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
