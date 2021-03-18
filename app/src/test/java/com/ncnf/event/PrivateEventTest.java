package com.ncnf.event;
import com.ncnf.organizer.PublicOrganizer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static org.junit.Assert.*;

public class PrivateEventTest {

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    Location loc = new Location(46.518689, 6.568067, "Rolex Learning Center, 1015 Ecublens");
    EventType type = EventType.Conference;
    String description = "Event description goes here";
    PublicOrganizer owner = new PublicOrganizer("John Doe");


    @Test
    public void privateEventGeneratesCorrectly() {
        PrivateEvent event = new PrivateEvent(name, date, loc, description, EventType.Conference, owner);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), loc);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);
        assertEquals(event.getOrganizer(), owner);
        assertEquals(event.getNumOfAttendees(), 0);
        assertTrue(event.getAttendees().size() == 0);
        assertEquals(event.getInvited().size(), 0);

    }

    @Test
    public void inviteWorks() {
        PrivateEvent event = new PrivateEvent(name, date, loc, description, EventType.Conference, owner);
        event.invite("Sarah");
        assertEquals(event.getInvited().size(), 1);
        assertEquals(event.getInvited().get(0), "Sarah");
    }
}
