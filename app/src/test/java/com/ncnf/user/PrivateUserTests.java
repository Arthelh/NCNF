package com.ncnf.user;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.event.PublicEvent;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.BIRTH_YEAR_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.Utils.OWNED_EVENTS_KEY;
import static com.ncnf.Utils.SAVED_EVENTS_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class PrivateUserTests {

//    DatabaseService db = mock(DatabaseService.class);
//
//    @Test
//    public void hashCodeMatches() {
//        PrivateUser u1 = new PrivateUser("1234567890","foo@bar.com");
//        PrivateUser u2 = new PrivateUser("1234567890","foo@bar.com");
//
//        assertEquals(u1.hashCode(), u2.hashCode());
//    }
//
//    @Test
//    public void equalsMatches() {
//        PrivateUser u1 = new PrivateUser("1234567890","foo@bar.com");
//        PrivateUser u2 = new PrivateUser("1234567890","foo@bar.com");
//
//        assertEquals(u1, u2);
//        assertEquals(u1, u1);
//    }
//
//    @Test
//    public void equalsFails() {
//        PrivateUser u1 = new PrivateUser("1234567890","foo@bar.com");
//        PrivateUser u2 = new PrivateUser("1234567890","foo@bal.com");
//
//        assertNotEquals(u1, u2);
//
//        u1 = new PrivateUser("1234567890","foo@bar.com");
//        u2 = new PrivateUser("1234567890","foo@bar.com");
//
//        assertNotEquals(u1, u2);
//
//        assertNotEquals(u1, new Object());
//    }
//
//    @Test
//    public void nullArgumentsThrows() {
//        assertThrows(IllegalStateException.class, () -> new PrivateUser("1234567890", null));
//        assertThrows(IllegalStateException.class, () -> new PrivateUser(null,"foo@bar.com"));
//        assertThrows(IllegalStateException.class, () -> new PrivateUser("", ""));
//    }
//
//    @Test
//    public void getId() {
//        String id = "1234567890";
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        assertEquals(id, user.getID());
//    }
//
//    @Test
//    public void getEmail() {
//        String email = "foo@bar.com";
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        assertEquals(email, user.getEmail());
//    }
//
//    @Test
//    public void saveUserCallsDatabase() {
//        when(db.setDocument(anyString(), anyMap())).thenReturn(new CompletableFuture());
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.saveUserToDB();
//        verify(db).setDocument(anyString(), anyMap());
//    }
//
//    @Test
//    public void getFieldCallsDatabase() {
//        when(db.getField(anyString(), anyString())).thenReturn(new CompletableFuture<>());
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.getField(FIRST_NAME_KEY);
//        verify(db).getField("1234567890", FIRST_NAME_KEY);
//    }
//
//    @Test
//    public void updateLastNameCallsDatabase() {
//        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.updateLastName("Doe");
//        verify(db).updateField("1234567890", LAST_NAME_KEY, "Doe");
//    }
//
//    @Test
//    public void updateFirstNameCallsDatabase() {
//        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.updateFirstName("John");
//        verify(db).updateField("1234567890", FIRST_NAME_KEY, "John");
//    }
//
//    @Test
//    public void updateBirthCallsDatabase() {
//        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.updateBirth(2011);
//        verify(db).updateField("1234567890", BIRTH_YEAR_KEY, 2011);
//    }
//
//    @Test
//    public void updateNotificationsCallsDatabase() {
//        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.updateNotifications(true);
//        verify(db).updateField("1234567890", NOTIFICATIONS_KEY, true);
//    }
//
//    @Test
//    public void updateNotificationsTokenCallsDatabase() {
//        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.updateNotificationsToken("My token");
//        verify(db).updateField("1234567890", NOTIFICATIONS_TOKEN_KEY, "My token");
//    }
//
//    @Test
//    public void loadUserCallsDatabase() {
//        when(db.getData(anyString())).thenReturn(new CompletableFuture<>());
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.loadUserFromDB();
//        verify(db).getData("1234567890");
//    }
//
//    @Test
//    public void saveEventCallsDatabase() {
//        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
//        Event event = new PublicEvent("00","Conference", new Date(), new GeoPoint(0., 0.) ,"North Pole", "Description goes here", Event.Type.Movie, 0 , 0);
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.saveEvent(event);
//        verify(db).updateField(eq("1234567890"), eq(SAVED_EVENTS_KEY), anyObject());
//    }
//
//    @Test
//    public void ownEventCallsDatabase() {
//        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
//        Event event = new PublicEvent("00","Conference", new Date(), new GeoPoint(0., 0.) ,"North Pole", "Description goes here", Event.Type.Movie, 0 , 0);
//        PrivateUser user = new PrivateUser("1234567890","foo@bar.com");
//        user.ownEvent(event);
//        verify(db).updateField(eq("1234567890"), eq(OWNED_EVENTS_KEY), anyObject());
//    }

}
