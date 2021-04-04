package com.ncnf.event;
import com.ncnf.organizer.PublicOrganizer;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.Location;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class PublicEventTest {

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    Location loc = new Location(46.518689, 6.568067, "Rolex Learning Center, 1015 Ecublens");
    EventType type = EventType.Conference;
    String description = "Event description goes here";
    PublicOrganizer owner = new PublicOrganizer("EPFL");

    @Test
    public void publicEventGeneratesCorrectly() {

        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), loc);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);
        assertEquals(event.getMinAge(), 0);
        assertEquals(event.getPrice(), 0);
        assertEquals(event.getOrganizer(), owner);
        assertEquals(event.getNumOfAttendees(), 0);
        assertTrue(event.getAttendees().size() == 0);

    }

    @Test
    public void publicEventRejectsOnInvalidAge() {
        assertThrows(IllegalArgumentException.class, () -> new PublicEvent(name, date, loc, description, EventType.Conference,-1, 0, owner, "swan_lake"));
    }

    @Test
    public void setMinAgeRejectsOnInvalidAge() {
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference, 0, 0, owner, "swan_lake");
        assertThrows(IllegalArgumentException.class, () -> event.setMinAge(-1));
    }

    @Test
    public void setMinAgeWorks() {
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference, 0, 0, owner, "swan_lake");
        event.setMinAge(0);
        assertEquals(event.getMinAge(), 0);
    }

    @Test
    public void addTagWorks() {
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference, 0, 0, owner, "swan_lake");
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        event.addTag(tag);
        assertTrue(event.getTags().contains(tag));
    }

    @Test
    public void setTagWorks() {
        List<Tag> list = new ArrayList<Tag>();
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        Tag tag2 = new Tag("\uD83C\uDFB8", "Folk Music");
        list.add(tag);
        list.add(tag2);
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        event.setTags(list);

        List<Tag> result = event.getTags();
        assertEquals(result.size(), list.size());
        for(int i = 0; i < result.size(); ++i) {
            assertEquals(list.get(i), result.get(i));
        }

    }

    @Test
    public void basicSettersWork() {
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        List<String> attendees = new ArrayList<>();
        Date newDate = new Date(2021, 3, 12);
        Location newLocation = new Location(0, 0, "North Pole");

        attendees.add("Mary");
        event.setName("Christmas Party");
        event.setPrice(4);
        event.setAttendees(attendees);
        event.setDate(newDate);
        event.setNewOrganizer(new PublicOrganizer("EPFL-IC"));
        event.setLocation(newLocation);
        event.setDescription("Another description");

        assertEquals(event.getDate(), newDate);
        assertEquals(event.getName(), "Christmas Party");
        assertEquals(event.getLocation(), newLocation);
        assertEquals(event.getDescription(), "Another description");
        assertEquals(event.getOrganizer().getName(), "EPFL-IC");
        assertEquals(event.getNumOfAttendees(), 1);
        assertTrue(event.getAttendees().size() == attendees.size());
        for(int i = 0; i < attendees.size(); ++i) {
            assertEquals(attendees.get(i), event.getAttendees().get(i));
        }
    }

    @Test
    public void gettersWork() {

        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        assertEquals(event.getImageName(), "swan_lake");
        assertEquals(event.getPubPriv(), Event.PubPriv.valueOf("PUBLIC"));
        UUID uuid = UUID.randomUUID();
        event.setUuid(uuid);
        assertEquals(event.getUuid(), uuid);
    }

    @Test
    public void serializationWorksPublicEvent() {

        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        DateAdapter adapter = new DateAdapter(date);

        String serialized = "PUBLIC@" + event.getUuid().toString() + "@" + "Jane Doe@" + adapter.toString() + "@" + loc.getLongitude() + " " + loc.getLatitude() + " " + loc.getAddress() + "@" + description + "@" + EventType.Conference.toString() + "@" + "0" + "@" + "0" + "@" + owner.getName() + "@" + "swan_lake";
        assertEquals(event.toString(), serialized);
        assertEquals(Event.toEvent(serialized), event);

    }

    @Test
    public void filterTagsWorks() {
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        Tag tag2 = new Tag("\uD83C\uDFB8", "Folk Music");
        event.addTag(tag);
        event.addTag(tag2);

        assertTrue(event.filterTags(tag2.getName()));
        assertFalse(event.filterTags("example"));
    }

    @Test
    public void compareToWorks() {
        PublicEvent event = new PublicEvent(name, date, loc, description, EventType.Conference,0, 0, owner, "swan_lake");
        Date date2 = new Date(2021, 03, 30);
        PublicEvent event2 = new PublicEvent(name, date2, loc, description, EventType.Conference,0, 0, owner, "swan_lake");

        assertEquals(event.compareTo(event2), -1);

    }


}
