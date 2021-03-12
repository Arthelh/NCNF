package com.example.bootcamp;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PrivateEventTest {

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    Location loc = new Location(46.518689, 6.568067, "Rolex Learning Center, 1015 Ecublens");
    Event.Type type = Event.Type.PRIVATE;
    String description = "Event description goes here";
    String owner = "John Doe";


    @Test
    public void privateEventGeneratesCorrectly() {
        PrivateEvent event = new PrivateEvent(name, date, loc, description, owner);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), loc);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);
        assertEquals(event.getOwner(), owner);
        assertEquals(event.getNumOfAttendees(), 0);
        assertTrue(event.getAttendees().size() == 0);
        assertEquals(event.getInvited().size(), 0);

    }

    @Test
    public void inviteWorks() {
        PrivateEvent event = new PrivateEvent(name, date, loc, description, owner);
        event.invite("Sarah");
        assertEquals(event.getInvited().size(), 1);
        assertEquals(event.getInvited().get(0), "Sarah");
    }
}