package com.ncnf.models;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.authentication.firebase.AuthenticationService;
import com.ncnf.database.firebase.DatabaseService;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserTests {

    DatabaseService db = mock(DatabaseService.class);
    AuthenticationService auth = mock(AuthenticationService.class);
    String ownerID = "ownerId";
    String name = "name";
    LocalDateTime date = LocalDateTime.now();
    GeoPoint geoPoint = new GeoPoint(0,0);
    String address = "address";
    String description = "description";
    SocialObject.Type type = SocialObject.Type.Movie;
    Event event = new Event(ownerID, name, date, geoPoint, address, description, type, 0 , 0, "test@email.com");
    Group group = new Group(ownerID, name, date, geoPoint, address, description, type);
    CompletableFuture<Boolean> response = CompletableFuture.completedFuture(true);


    @Test
    public void hashCodeMatches() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        User u2 = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void equalsMatches() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        User u2 = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);

        assertEquals(u1, u2);
        assertEquals(u1, u1);
    }

    @Test
    public void equalsFails() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        User u2 = new User(this.db, "1234567890", "", "foo@bal.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);


        assertNotEquals(u1, u2);

        u1 = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        u2 = new User(this.db, "0000000000", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);


        assertNotEquals(u1, u2);

        assertNotEquals(u1, new Object());
    }

    @Test
    public void nullArgumentsThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, null, "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "11223333", "", null,"", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "11223333", "", "","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null)
        );
    }

    @Test
    public void getId() {
        String id = "1234567890";
        User user = new User(this.db, id, "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        assertEquals(id, user.getUuid());
    }

    @Test
    public void getEmail() {
        String email = "foo@bar.com";
        User user = new User(this.db, "1234567890", "", email,"", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        assertEquals(email, user.getEmail());
    }

    @Test
    public void gettersWork(){
        String uuid = "uuid";
        String username = "username";
        String email = "email";
        String newEmail = "newEmail";
        String full_name = "full_name";
        List<String> friends = new ArrayList<>();
        friends.add("friend1");
        List<String> owned = new ArrayList<>();
        owned.add("event1");
        List<String> saved = new ArrayList<>();
        saved.add("event2");
        LocalDate date = LocalDate.now();
        List<String> groups = new ArrayList<>();
        groups.add("group1");
        GeoPoint location = new GeoPoint(0,0);
        List<String> empty = new ArrayList<>();

        User user = new User(this.db, "1234567890", "", email,"", empty, empty, new ArrayList<>(), empty, false, null, null);
        assertEquals(user.getUuid(), "1234567890");
        assertEquals(user.getUsername(), "");
        assertEquals(user.getEmail(), email);
        assertEquals(user.getFullName(), "");
        assertEquals(user.getFriendsIds(), empty);
        assertEquals(user.getOwnedGroupsIds(), empty);
        assertEquals(user.getSavedEventsIds(), empty);
        assertEquals(user.getParticipatingGroupsIds(), empty);
        assertEquals(user.getBirthDate(), null);
        assertFalse(user.getNotifications());
        assertEquals(user.getLocation(), null);


        user.setUsername(username);
        user.setFullName(full_name);
        user.setFriendsIds(friends);
        user.setSavedEventsIds(saved);
        user.setOwnedGroupsIds(owned);
        user.setBirthDate(date);
        user.setNotifications(true);
        user.setParticipatingGroupsIds(groups);
        user.setLocation(location);
        user.setEmail(newEmail);

        assertEquals(user.getUuid(), "1234567890");
        assertEquals(user.getUsername(), username);
        assertEquals(user.getFullName(), full_name);
        assertEquals(user.getFriendsIds(), friends);
        assertEquals(user.getOwnedGroupsIds(), owned);
        assertEquals(user.getSavedEventsIds(), saved);
        assertEquals(user.getBirthDate(), date);
        assertTrue(user.getNotifications());
        assertEquals(user.getParticipatingGroupsIds(), groups);
        assertEquals(user.getLocation(), location);
        assertEquals(user.getEmail(), newEmail);
    }

    @Test
    public void updateNotificationsWorks() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);

        assertFalse(user.getNotifications());
        user.updateNotifications(true);
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_KEY, true);
        assertTrue(user.getNotifications());
    }

    @Test
    public void updateNotificationsTokenWorks() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);

        user.updateNotificationsToken("Doe");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_TOKEN_KEY, "Doe");
    }

    @Test
    public void saveUserCallsDatabase() {
        when(db.setDocument(anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));
        User user = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
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
        User user = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
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
        User user = new User(this.db, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        CompletableFuture future = new CompletableFuture();
        future.completeExceptionally(new IllegalStateException());

        when(db.getDocument(anyString(), anyObject())).thenReturn(future);
        CompletableFuture<User> loaded = user.loadUserFromDB();
        try {
            assertEquals(loaded.get(), null);
        } catch(Exception e){
            assertNotNull(e);
        }
    }

    @Test
    public void getAllUsersLikeWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        User user2 = new User(this.db, "0000000000", "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);

        CompletableFuture<List<Object>> future = CompletableFuture.completedFuture(Arrays.asList(user));

        when(db.withFieldContaining(anyString(), anyString(), anyString(), any())).thenReturn(future);

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
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        when(db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.addParticipatingGroup(group);
        try {
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void ownEventWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        when(db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.addOwnedGroup(group);
        try {
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getOwnedEventsWorksOnEmptyList(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);

        CompletableFuture<List<Group>> future = user.getOwnedGroups();
        try {
            assertTrue(future.get().isEmpty());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getOwnedEventsWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","", new ArrayList<>(), Arrays.asList(ownerID, ownerID), new ArrayList<>(), new ArrayList<>(), false, null, null);
        CompletableFuture<List<SocialObject>> events = CompletableFuture.completedFuture(Arrays.asList(group));
        when(db.whereIn(anyString(), anyString(), anyList(), any())).thenReturn(events);

        CompletableFuture<List<Group>> future = user.getOwnedGroups();
        try {
            assertTrue(future.get().size() == 1);
            assertEquals(group, future.get().get(0));
            assertEquals(future.get().get(0).getOwnerId(), ownerID);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getSavedEventsWorksOnEmptyList(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);

        CompletableFuture<List<Event>> future = user.getSavedEvents();
        try {
            assertTrue(future.get().isEmpty());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getSavedEventsWorks(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), false, null, null);
        CompletableFuture<List<SocialObject>> events = CompletableFuture.completedFuture(Arrays.asList(event));
        when(db.whereIn(anyString(), anyString(), anyList(), any())).thenReturn(events);

        CompletableFuture<List<Event>> future = user.getSavedEvents();
        try {
            assertTrue(future.get().size() == 1);
            assertEquals(event, future.get().get(0));
            assertEquals(future.get().get(0).getOwnerId(), ownerID);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void createEventFailsFailsOnWrongCredentials(){
        User user = new User(this.db, "1234567890", "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), false, null, null);

        CompletableFuture<Boolean> future = user.createGroup(group);
        try {
            assertFalse(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void createEventFails(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), false, null, null);
        CompletableFuture<Boolean> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new IllegalStateException());
        when(this.db.setDocument(anyString(), anyObject())).thenReturn(failingFuture);
        when(this.db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.createGroup(group);
        try {
            assertFalse(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void createEventFailsWorks(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Arrays.asList(ownerID), false, null, null);
        when(this.db.setDocument(anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));
        when(this.db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.createGroup(group);
        try {
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void addSavedEvent(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(Collections.singleton(ownerID)), false, null, null);
        when(this.db.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> future = user.addSavedEvent(event);
        try {
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getParticipatingGroupsWorksOnEmptyList(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(Collections.singleton(ownerID)), false, null, null);
        CompletableFuture<List<Group>> future = user.getParticipatingGroups();
        try{
            assertTrue(future.get().isEmpty());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }

    }

    @Test
    public void getParticipatingGroupsWorks(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(Collections.singleton(ownerID)), new ArrayList<>(), false, null, null);
        when(db.whereIn(anyString(), anyString(), anyList(), any())).thenReturn(CompletableFuture.completedFuture(Arrays.asList(group)));

        CompletableFuture<List<Group>> query = user.getParticipatingGroups();
        try{
            assertEquals(query.get().get(0), group);
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }

    }

    @Test
    public void changeEmailWorks(){
        User user = new User(this.db, ownerID, "test", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(Collections.singleton(ownerID)), new ArrayList<>(), false, null, null);
        when(auth.changeEmail(anyString())).thenReturn(CompletableFuture.completedFuture(true));
        CompletableFuture<Boolean> future = user.changeEmail(auth, "new Email");
        try{
            assertTrue(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }

        when(auth.changeEmail(anyString())).thenReturn(CompletableFuture.completedFuture(false));
        future = user.changeEmail(auth, "new Email");
        try{
            assertFalse(future.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }
}
