package com.ncnf.event;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static org.junit.Assert.*;

public class PublicEventTest {

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    Location loc = new Location(46.518689, 6.568067, "Rolex Learning Center, 1015 Ecublens");
    Event.Type type = Event.Type.PUBLIC;
    String description = "Event description goes here";
    String owner = "EPFL";

    @Test
    public void publicEventGeneratesCorrectly() {

        PublicEvent event = new PublicEvent(name, date, loc, description, 0, 0, owner);
        assertEquals(event.getDate(), date);
        assertEquals(event.getName(), name);
        assertEquals(event.getLocation(), loc);
        assertEquals(event.getType(), type);
        assertEquals(event.getDescription(), description);
        assertEquals(event.getMinAge(), 0);
        assertEquals(event.getPrice(), 0);
        assertEquals(event.getOwner(), owner);
        assertEquals(event.getNumOfAttendees(), 0);
        assertTrue(event.getAttendees().size() == 0);

    }

    @Test
    public void publicEventRejectsOnInvalidAge() {
        assertThrows(IllegalArgumentException.class, () -> new PublicEvent(name, date, loc, description, -1, 0, owner));
    }

    @Test
    public void setMinAgeRejectsOnInvalidAge() {
        PublicEvent event = new PublicEvent(name, date, loc, description, 0, 0, owner);
        assertThrows(IllegalArgumentException.class, () -> event.setMinAge(-1));
    }

    @Test
    public void setMinAgeWorks() {
        PublicEvent event = new PublicEvent(name, date, loc, description, 0, 0, owner);
        event.setMinAge(0);
        assertEquals(event.getMinAge(), 0);
    }

    @Test
    public void addTagWorks() {
        PublicEvent event = new PublicEvent(name, date, loc, description, 0, 0, owner);
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        event.addTag(tag);
        assertTrue(event.getTags().contains(tag));
    }

    @Test
    public void addTagRejectsOnDuplicateTag() {
        PublicEvent event = new PublicEvent(name, date, loc, description, 0, 0, owner);
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        event.addTag(tag);
        assertThrows(IllegalArgumentException.class, () -> event.addTag(tag));
    }

    @Test
    public void setTagWorks() {
        List<Tag> list = new ArrayList<Tag>();
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        Tag tag2 = new Tag("\uD83C\uDFB8", "Folk Music");
        list.add(tag);
        list.add(tag2);
        PublicEvent event = new PublicEvent(name, date, loc, description, 0, 0, owner);
        event.setTags(list);

        List<Tag> result = event.getTags();
        assertEquals(result.size(), list.size());
        for(int i = 0; i < result.size(); ++i) {
            assertEquals(list.get(i), result.get(i));
        }

    }

    @Test
    public void basicSettersWork() {
        PublicEvent event = new PublicEvent(name, date, loc, description, 0, 0, owner);
        List<String> attendees = new ArrayList<>();
        Date newDate = new Date(2021, 3, 12);
        Location newLocation = new Location(0, 0, "North Pole");

        attendees.add("Mary");
        event.setName("Christmas Party");
        event.setPrice(4);
        event.setAttendees(attendees);
        event.setDate(newDate);
        event.setNewOwner("EPFL-IC");
        event.setLocation(newLocation);
        event.setDescription("Another description");

        assertEquals(event.getDate(), newDate);
        assertEquals(event.getName(), "Christmas Party");
        assertEquals(event.getLocation(), newLocation);
        assertEquals(event.getDescription(), "Another description");
        assertEquals(event.getOwner(), "EPFL-IC");
        assertEquals(event.getNumOfAttendees(), 1);
        assertTrue(event.getAttendees().size() == attendees.size());
        for(int i = 0; i < attendees.size(); ++i) {
            assertEquals(attendees.get(i), event.getAttendees().get(i));
        }
    }


}
