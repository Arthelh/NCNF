package com.ncnf.user;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import static com.ncnf.Utils.BIRTH_DATE_KEY;
import static com.ncnf.Utils.DATE_KEY;
import static com.ncnf.Utils.DESCRIPTION_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.INVITED_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.Utils.OWNER_KEY;
import static com.ncnf.Utils.SAVED_EVENTS_KEY;
import static com.ncnf.Utils.TYPE_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;
import static com.ncnf.Utils.UUID_KEY;
import static com.ncnf.Utils.VISIBILITY_KEY;
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
    CompletableFuture<Boolean> response = CompletableFuture.completedFuture(true);

//    User user = new User(db, )



    @Test
    public void hashCodeMatches() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        User u2 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void equalsMatches() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        User u2 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);

        assertEquals(u1, u2);
        assertEquals(u1, u1);
    }

    @Test
    public void equalsFails() {
        User u1 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        User u2 = new User(this.db, "1234567890", "", "foo@bal.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);


        assertNotEquals(u1, u2);

        u1 = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        u2 = new User(this.db, "0000000000", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);


        assertNotEquals(u1, u2);

        assertNotEquals(u1, new Object());
    }

    @Test
    public void nullArgumentsThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, null, "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "11223333", "", null,"",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new User(this.db, "11223333", "", "","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false)
        );
    }

    @Test
    public void getId() {
        String id = "1234567890";
        User user = new User(this.db, id, "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        assertEquals(id, user.getUuid());
    }

    @Test
    public void getEmail() {
        String email = "foo@bar.com";
        User user = new User(this.db, "1234567890", "", email,"",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
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

        User user = new User(this.db, "1234567890", "", email,"",  "", empty, empty, empty, null, false);
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
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);

        assertFalse(user.getNotifications());
        user.updateNotifications(true);
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_KEY, true);
        assertTrue(user.getNotifications());
    }

    @Test
    public void updateNotificationsTokenWorks() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);

        user.updateNotificationsToken("Doe");
        verify(db).updateField(USERS_COLLECTION_KEY + "1234567890", NOTIFICATIONS_TOKEN_KEY, "Doe");
    }

    @Test
    public void saveUserCallsDatabase() {
        when(db.setDocument(anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));
        User user = new User(this.db, "1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        CompletableFuture<Boolean> future = user.saveUserToDB();
        verify(db).setDocument(anyString(), anyMap());
        try {
            assertTrue(response.get());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }
}
