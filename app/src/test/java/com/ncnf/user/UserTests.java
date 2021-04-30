package com.ncnf.user;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.event.PrivateEvent;
import com.ncnf.event.PublicEvent;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ncnf.Utils.NOTIFICATIONS_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
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
    CompletableFuture<Boolean> response = CompletableFuture.completedFuture(true);


    @Test
    public void hashCodeMatches() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        User u2 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void equalsMatches() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        User u2 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);

        assertEquals(u1, u2);
        assertEquals(u1, u1);
    }

    @Test
    public void equalsFails() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        User u2 = new User(this.db, "1234567890", "", "foo@bal.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);


        assertNotEquals(u1, u2);

        u1 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        u2 = new User(this.db, "0000000000", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);


        assertNotEquals(u1, u2);

        assertNotEquals(u1, new Object());
    }

    @Test
    public void nullArgumentsThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, null, "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "11223333", "", null,"",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "11223333", "", "","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null)
        );
    }

    @Test
    public void getId() {
        String id = "1234567890";
        User user = new User(this.db, id, "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        assertEquals(id, user.getUuid());
    }

    @Test
    public void getEmail() {
        String email = "foo@bar.com";
        User user = new User(this.db, "1234567890", "", email,"",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        assertEquals(email, user.getEmail());
    }

    @Test
    public void gettersWork(){
        String uuid = "uuid";
        String username = "username";
        String email = "email";
        String firstName = "first";
        String lastName = "last";
        List<String> friends = new ArrayList<>();
        friends.add("friend1");
        List<String> owned = new ArrayList<>();
        owned.add("event1");
        List<String> saved = new ArrayList<>();
        saved.add("event2");
        Date date = new Date();

        List<String> empty = new ArrayList<>();

        User user = new User(this.db, "1234567890", "", email,"",  "", empty, empty, empty, null, false, null);
        assertEquals(user.getUuid(), "1234567890");
        assertEquals(user.getUsername(), "");
        assertEquals(user.getEmail(), email);
        assertEquals(user.getFirstName(), "");
        assertEquals(user.getLastName(), "");
        assertEquals(user.getFriendsIds(), empty);
        assertEquals(user.getOwnedEventsIds(), empty);
        assertEquals(user.getSavedEventsIds(), empty);
        assertEquals(user.getBirthDate(), null);
        assertFalse(user.getNotifications());

        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setFriendsIds(friends);
        user.setSavedEventsIds(saved);
        user.setOwnedEventsIds(owned);
        user.setBirthDate(date);
        user.setNotifications(true);

        assertEquals(user.getUuid(), "1234567890");
        assertEquals(user.getUsername(), username);
        assertEquals(user.getFirstName(), firstName);
        assertEquals(user.getLastName(), lastName);
        assertEquals(user.getFriendsIds(), friends);
        assertEquals(user.getOwnedEventsIds(), owned);
        assertEquals(user.getSavedEventsIds(), saved);
        assertEquals(user.getBirthDate(), date);
        assertTrue(user.getNotifications());
    }

    @Test
    public void updateNotificationsWorks() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);

        assertFalse(user.getNotifications());
        user.updateNotifications(true);
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_KEY, true);
        assertTrue(user.getNotifications());
    }

    @Test
    public void updateNotificationsTokenWorks() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);

        user.updateNotificationsToken("Doe");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_TOKEN_KEY, "Doe");
    }

    @Test
    public void saveUserCallsDatabase() {
        when(db.setDocument(anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        CompletableFuture<Boolean> future = user.saveUserToDB();
        verify(db).setDocument(anyString(), anyMap());
        try {
            assertTrue(response.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void loadUserFromDBWorks(){
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        CompletableFuture future = CompletableFuture.completedFuture(user);

        when(db.getDocument(anyString(), anyObject())).thenReturn(future);
        CompletableFuture<User> loaded = user.loadUserFromDB();
        try {
            User user2 = loaded.get();
            assertEquals(user, user2);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void loadUserFromDBFails() throws ExecutionException, InterruptedException {
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        CompletableFuture future = new CompletableFuture();
        future.completeExceptionally(new IllegalStateException());

        when(db.getDocument(anyString(), anyObject())).thenReturn(future);
        CompletableFuture<User> loaded = user.loadUserFromDB();
        try {
            assertEquals(loaded.get(), null);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getFriendsWorksOnEmptyList(){
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        CompletableFuture<List<User>> future = user.getFriends();
        try {
            assertTrue(future.get().isEmpty());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getFriendsWorks(){
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        User user2 = new User(this.db, "0000000000", "", "foo@bar.com","",  "", Arrays.asList(user.getUuid()), new ArrayList<>(), new ArrayList<>(), null, false, null);

        CompletableFuture<List<User>> future = CompletableFuture.completedFuture(Arrays.asList(user));

        when(db.whereIn(anyString(), anyString(), anyList(), any())).thenReturn(future);

        CompletableFuture<List<User>> query = user2.getFriends();
        try {
            assertTrue(query.get().size() == 1);
            assertEquals(query.get().get(0), user);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getAllUsersLikeWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        User user2 = new User(this.db, "0000000000", "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);

        CompletableFuture<List<Object>> future = CompletableFuture.completedFuture(Arrays.asList(user));

        when(db.withFieldLike(anyString(), anyString(), anyString(), any())).thenReturn(future);

        CompletableFuture<List<User>> query = user2.getAllUsersLike(user.getUsername());
        try {
            assertTrue(query.get().size() == 1);
            assertEquals(query.get().get(0).getUsername(), user2.getUsername());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void saveEventWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        when(db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.addSavedEvent(privateEvent);
        try {
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void ownEventWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);
        when(db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.addOwnedEvent(privateEvent);
        try {
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getOwnedEventsWorksOnEmptyList(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);

        CompletableFuture<List<Event>> future = user.getOwnedEvents();
        try {
            assertTrue(future.get().isEmpty());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getOwnedEventsWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","",  "", new ArrayList<>(), Arrays.asList(ownerID, ownerID), new ArrayList<>(), null, false, null);
        CompletableFuture<List<Event>> events = CompletableFuture.completedFuture(Arrays.asList(publicEvent, privateEvent));
        when(db.whereIn(anyString(), anyString(), anyList(), any())).thenReturn(events);

        CompletableFuture<List<Event>> future = user.getOwnedEvents();
        try {
            assertTrue(future.get().size() == 2);
            assertEquals(publicEvent, future.get().get(0));
            assertEquals(future.get().get(0).getOwnerId(), ownerID);
            assertEquals(privateEvent, future.get().get(1));
            assertEquals(future.get().get(1).getOwnerId(), ownerID);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getSavedEventsWorksOnEmptyList(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false, null);

        CompletableFuture<List<Event>> future = user.getSavedEvents();
        try {
            assertTrue(future.get().isEmpty());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getSavedEventsWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), null, false, null);
        CompletableFuture<List<Event>> events = CompletableFuture.completedFuture(Arrays.asList(publicEvent));
        when(db.whereIn(anyString(), anyString(), anyList(), any())).thenReturn(events);

        CompletableFuture<List<Event>> future = user.getSavedEvents();
        try {
            assertTrue(future.get().size() == 1);
            assertEquals(publicEvent, future.get().get(0));
            assertEquals(future.get().get(0).getOwnerId(), ownerID);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void createEventFailsFailsOnWrongCredentials(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), null, false, null);

        CompletableFuture<Boolean> future = user.createEvent(publicEvent);
        try {
            assertFalse(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void createEventFailsFails(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), null, false, null);
        CompletableFuture<Boolean> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new IllegalStateException());
        when(this.db.setDocument(anyString(), anyObject())).thenReturn(failingFuture);
        when(this.db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.createEvent(publicEvent);
        try {
            assertFalse(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void createEventFailsWorksForPublicEvents(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), null, false, null);
        when(this.db.setDocument(anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));
        when(this.db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.createEvent(publicEvent);
        try {
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void createEventFailsWorksForPrivateEvents(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), null, false, null);
        when(this.db.setDocument(anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));
        when(this.db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.createEvent(privateEvent);
        try {
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }
}
