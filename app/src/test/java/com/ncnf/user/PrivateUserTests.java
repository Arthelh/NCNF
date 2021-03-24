package com.ncnf.user;

import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.event.EventType;
import com.ncnf.event.PublicEvent;
import com.ncnf.organizer.PublicOrganizer;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.BIRTH_YEAR_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static com.ncnf.Utils.OWNED_EVENTS_KEY;
import static com.ncnf.Utils.SAVED_EVENTS_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class PrivateUserTests {

    private static DatabaseService db = Mockito.mock(DatabaseService.class);

    @Test
    public void equalsMatches() {
        PrivateUser u1 = new PrivateUser(db, "/users", "42", "foo@bar.com");
        PrivateUser u2 = new PrivateUser(db, "/users", "42", "foo@bar.com");

        assertEquals(u1, u2);
    }

    @Test
    public void equalsFails() {
        PrivateUser u1 = new PrivateUser(db, "/users", "42", "foo@bar.com");
        PrivateUser u2 = new PrivateUser(db, "/users", "7", "foo@bar.com");

        assertNotEquals(u1, u2);

        u1 = new PrivateUser(db, "/users", "42", "foo@bar.com");
        u2 = new PrivateUser(db, "/events", "42", "foo@bar.com");

        assertNotEquals(u1, u2);
    }

    @Test
    public void nullArgumentsThrows() {
        assertThrows(IllegalStateException.class, () -> new PrivateUser(db, null, "", ""));
        assertThrows(IllegalStateException.class, () -> new PrivateUser(db, "", null, ""));
        assertThrows(IllegalStateException.class, () -> new PrivateUser(db, "", "", null));
    }

    @Test
    public void getId() {
        String id = "42";
        PrivateUser user = new PrivateUser(db, "", id, "");
        assertEquals(id, user.getID());
    }

    @Test
    public void getEmail() {
        String email = "foo@bar.com";
        PrivateUser user = new PrivateUser(db, "", "", email);
        assertEquals(email, user.getEmail());
    }

    @Test
    public void saveUserCallsDatabase() {
        when(db.setDocument(anyString(), anyMap())).thenReturn(new CompletableFuture());
        PrivateUser user = new PrivateUser(db, "/users", "42", "foo@bar.com");
        user.saveUserToDB();
        verify(db).setDocument(anyString(), anyMap());
    }

    @Test
    public void getFieldCallsDatabase() {
        when(db.getField(anyString(), anyString())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "/users", "42", "foo@bar.com");
        user.getField(FIRST_NAME_KEY);
        verify(db).getField("/users", FIRST_NAME_KEY);
    }

    @Test
    public void updateLastNameCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "/users", "42", "foo@bar.com");
        user.updateLastName("Doe");
        verify(db).updateField("/users", LAST_NAME_KEY, "Doe");
    }

    @Test
    public void updateFirstNameCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "/users", "42", "foo@bar.com");
        user.updateFirstName("John");
        verify(db).updateField("/users", FIRST_NAME_KEY, "John");
    }

    @Test
    public void updateBirthCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "/users", "42", "foo@bar.com");
        user.updateBirth(2011);
        verify(db).updateField("/users", BIRTH_YEAR_KEY, 2011);
    }

    @Test
    public void loadUserCallsDatabase() {
        when(db.getData(anyString())).thenReturn(new CompletableFuture<>());
        PrivateUser user = new PrivateUser(db, "/users", "42", "foo@bar.com");
        user.loadUserFromDB();
        verify(db).getData("/users");
    }

    @Test
    public void saveEventCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        Event event = new PublicEvent(new PublicOrganizer("EPFL"), EventType.Movie, "Conference", "");
        PrivateUser user = new PrivateUser(db, "/users", "42", "foo@bar.com");
        user.saveEvent(event);
        verify(db).updateField(eq("/users"), eq(SAVED_EVENTS_KEY), anyObject());
    }

    @Test
    public void ownEventCallsDatabase() {
        when(db.updateField(anyString(), anyString(), anyObject())).thenReturn(new CompletableFuture<>());
        Event event = new PublicEvent(new PublicOrganizer("EPFL"), EventType.Movie, "Conference", "");
        PrivateUser user = new PrivateUser(db, "/users", "42", "foo@bar.com");
        user.ownEvent(event);
        verify(db).updateField(eq("/users"), eq(OWNED_EVENTS_KEY), anyObject());
    }



}
