package com.ncnf.database.builders;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.models.Event;
import com.ncnf.models.Group;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ncnf.utilities.StringCodes.ADDRESS_KEY;
import static com.ncnf.utilities.StringCodes.DATE_KEY;
import static com.ncnf.utilities.StringCodes.DESCRIPTION_KEY;
import static com.ncnf.utilities.StringCodes.EMAIL_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.MEMBERS_KEY;
import static com.ncnf.utilities.StringCodes.MIN_AGE_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.OWNER_KEY;
import static com.ncnf.utilities.StringCodes.PRICE_KEY;
import static com.ncnf.utilities.StringCodes.TYPE_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;
import static com.ncnf.utilities.StringCodes.VISIBILITY_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatabaseEventBuilderTest {

    private final DatabaseEventBuilder eventBuilder = new DatabaseEventBuilder();
    private final DatabaseGroupBuilder groupBuilder = new DatabaseGroupBuilder();

    Map<String, Object> event;
    Map<String, Object> group;
    String ownerId = "ownerId";
    String uuid = UUID.randomUUID().toString();
    String name = "name";
    LocalDateTime date = LocalDateTime.now();
    Timestamp timestamp = new Timestamp(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
    GeoPoint location = new GeoPoint(0, 0);
    String address = "address";
    String type = "Conference";
    List<String> attendees = new ArrayList<>(Collections.singleton("attendee1"));
    String description = "description";
    String email = "email";

    int minAge = 0;
    double price = 0;

    List<String> invited = new ArrayList<>(Collections.singleton("invited"));

    @Before
    public void setup() {
        event = new HashMap<>();
        group = new HashMap<>();
    }

    @Test
    public void loadEvent() {
        event.put(OWNER_KEY, ownerId);
        event.put(UUID_KEY, this.uuid);
        event.put(NAME_KEY, this.name);
        event.put(DATE_KEY, timestamp);
        event.put(LOCATION_KEY, this.location);
        event.put(ADDRESS_KEY, this.address);
        event.put(VISIBILITY_KEY, "PUBLIC");
        event.put(TYPE_KEY, this.type);
        event.put(MEMBERS_KEY, this.attendees);
        event.put(DESCRIPTION_KEY, this.description);
        event.put(OWNER_KEY, this.ownerId);
        event.put(EMAIL_KEY, email);

        event.put(MIN_AGE_KEY, minAge);
        event.put(PRICE_KEY, price);

        Event event = eventBuilder.toObject(uuid, this.event);

        assertEquals(event.getUuid().toString(), uuid);
        assertEquals(event.getName(), name);
        assertDateEquals(event.getDate(), date);
        assertEquals(event.getLocation(), location);
        assertEquals(event.getAddress(), address);
        assertEquals(event.getType().toString(), type);
        assertEquals(event.getAttendees().get(0), attendees.get(0));
        assertEquals(event.getDescription(), description);
        assertEquals(event.getOwnerId(), ownerId);
        assertEquals(event.getMinAge(), minAge);
        assertEquals(event.getPrice(), price, 0);
        assertEquals(event.getEmail(), email);
    }


    @Test
    public void loadGroup() {
        group.put(OWNER_KEY, ownerId);
        group.put(UUID_KEY, this.uuid);
        group.put(NAME_KEY, this.name);
        group.put(DATE_KEY, timestamp);
        group.put(LOCATION_KEY, this.location);
        group.put(ADDRESS_KEY, this.address);
        group.put(VISIBILITY_KEY, "PRIVATE");
        group.put(TYPE_KEY, this.type);
        group.put(MEMBERS_KEY, this.attendees);
        group.put(DESCRIPTION_KEY, this.description);
        group.put(OWNER_KEY, this.ownerId);
        group.put(MEMBERS_KEY, invited);

        Group group1 = groupBuilder.toObject(uuid, group);

        assertEquals(group1.getUuid().toString(), uuid);
        assertEquals(group1.getName(), name);

        assertDateEquals(group1.getDate(), date);
        assertEquals(group1.getLocation(), location);
        assertEquals(group1.getAddress(), address);
        assertEquals(group1.getDescription(), description);
        assertEquals(group1.getOwnerId(), ownerId);
        assertTrue(group1.getMembers().contains("invited"));
        assertTrue(group1.getMembers().contains(ownerId));

    }

    @Test
    public void groupToMapWorks() {
        UUID uuid = UUID.randomUUID();

        Group group = new Group(ownerId, uuid, name, date, location, address, description, invited);
        Map<String, Object> map = groupBuilder.toMap(group);
        assertEquals(groupBuilder.toObject(uuid.toString(), map), group);

    }

    @Test
    public void eventToMapWorks() {
        Event.Type type = Event.Type.Movie;
        UUID uuid = UUID.randomUUID();

        Event event = new Event(ownerId, uuid, name, date, location, address, description, type, attendees, minAge, price, email);
        Map<String, Object> map = eventBuilder.toMap(event);
        assertEquals(eventBuilder.toObject(uuid.toString(), map), event);

    }

    private void assertDateEquals(LocalDateTime d1, LocalDateTime d2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd HH mm");
        assertEquals(d1.format(formatter), d2.format(formatter));
    }
}
