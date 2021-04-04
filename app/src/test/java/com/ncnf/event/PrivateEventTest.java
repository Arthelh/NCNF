package com.ncnf.event;
import com.ncnf.organizer.PublicOrganizer;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.Location;

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
        PrivateEvent event = new PrivateEvent(name, date, loc, description, EventType.Conference, owner, "swan_lake");
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
    public void serializationWorksPrivateEvent() {

        PrivateEvent event = new PrivateEvent(name, date, loc, description, EventType.Conference, owner, "swan_lake");
        DateAdapter adapter = new DateAdapter(date);

        String serialized = "PRIVATE@" + event.getUuid().toString() + "@" + name + "@" + adapter.toString() + "@" + loc.getLongitude() + " " + loc.getLatitude() + " " + loc.getAddress() + "@" + description + "@" + EventType.Conference.toString() + "@" + owner.getName() + "@" + "swan_lake";
        assertEquals(event.toString(), serialized);
        assertEquals(Event.toEvent(serialized), event);

    }

    @Test
    public void inviteWorks() {
        PrivateEvent event = new PrivateEvent(name, date, loc, description, EventType.Conference, owner, "swan_lake");
        event.invite("Sarah");
        assertEquals(event.getInvited().size(), 1);
        assertEquals(event.getInvited().get(0), "Sarah");
    }

    @Test
    public void compareToWorks() {
        PrivateEvent event = new PrivateEvent(name, date, loc, description, EventType.Conference, owner, "swan_lake");
        Date date2 = new Date(2021, 03, 30);
        PrivateEvent event2 = new PrivateEvent(name, date2, loc, description, EventType.Conference, owner, "swan_lake");

        assertEquals(event.compareTo(event2), -1);

    }
}
