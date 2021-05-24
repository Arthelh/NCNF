package com.ncnf.database.builders;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.models.Event;
import com.ncnf.models.Group;
import com.ncnf.models.SocialObject;
import com.ncnf.models.Tag;

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
import static com.ncnf.utilities.StringCodes.ATTENDEES_KEY;
import static com.ncnf.utilities.StringCodes.DATE_KEY;
import static com.ncnf.utilities.StringCodes.DESCRIPTION_KEY;
import static com.ncnf.utilities.StringCodes.EMAIL_KEY;
import static com.ncnf.utilities.StringCodes.INVITED_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.MIN_AGE_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.OWNER_KEY;
import static com.ncnf.utilities.StringCodes.PRICE_KEY;
import static com.ncnf.utilities.StringCodes.TAGS_LIST_KEY;
import static com.ncnf.utilities.StringCodes.TYPE_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;
import static com.ncnf.utilities.StringCodes.VISIBILITY_KEY;
import static org.junit.Assert.assertEquals;

public class EventBuilderTest {

    private EventBuilder eventBuilder = new EventBuilder();
    private GroupBuilder groupbuilder = new GroupBuilder();

    Map<String, Object> publicEvent;
    Map<String, Object> privateEvent;
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
    List<Tag> tags = new ArrayList<>(Collections.singleton(new Tag("\uD83C\uDFB8", "Rock Music")));

    List<String> invited = new ArrayList<>(Collections.singleton("invited"));

    @Before
    public void setup() {
        publicEvent = new HashMap<>();
        privateEvent = new HashMap<>();
    }

    @Test
    public void loadPublic() {
        publicEvent.put(OWNER_KEY, ownerId);
        publicEvent.put(UUID_KEY, this.uuid);
        publicEvent.put(NAME_KEY, this.name);
        publicEvent.put(DATE_KEY, timestamp);
        publicEvent.put(LOCATION_KEY, this.location);
        publicEvent.put(ADDRESS_KEY, this.address);
        publicEvent.put(VISIBILITY_KEY, "PUBLIC");
        publicEvent.put(TYPE_KEY, this.type);
        publicEvent.put(ATTENDEES_KEY, this.attendees);
        publicEvent.put(DESCRIPTION_KEY, this.description);
        publicEvent.put(OWNER_KEY, this.ownerId);
        publicEvent.put(EMAIL_KEY, email);

        publicEvent.put(MIN_AGE_KEY, minAge);
        publicEvent.put(PRICE_KEY, price);
        publicEvent.put(TAGS_LIST_KEY, tags);

        Event event = eventBuilder.toObject(uuid, publicEvent);

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
    public void loadPrivate() {
        privateEvent.put(OWNER_KEY, ownerId);
        privateEvent.put(UUID_KEY, this.uuid);
        privateEvent.put(NAME_KEY, this.name);
        privateEvent.put(DATE_KEY, timestamp);
        privateEvent.put(LOCATION_KEY, this.location);
        privateEvent.put(ADDRESS_KEY, this.address);
        privateEvent.put(VISIBILITY_KEY, "PRIVATE");
        privateEvent.put(TYPE_KEY, this.type);
        privateEvent.put(ATTENDEES_KEY, this.attendees);
        privateEvent.put(DESCRIPTION_KEY, this.description);
        privateEvent.put(OWNER_KEY, this.ownerId);

        privateEvent.put(INVITED_KEY, invited);

        Group event = (Group) groupbuilder.toObject(uuid, privateEvent);

        assertEquals(event.getUuid().toString(), uuid);
        assertEquals(event.getName(), name);

        assertDateEquals(event.getDate(), date);
        assertEquals(event.getLocation(), location);
        assertEquals(event.getAddress(), address);
        assertEquals(event.getType().toString(), type);
        assertEquals(event.getAttendees().get(0), attendees.get(0));
        assertEquals(event.getDescription(), description);
        assertEquals(event.getOwnerId(), ownerId);
        assertEquals(event.getInvited().get(0), "invited");

    }

    @Test
    public void privateToMapWorks() {
        SocialObject.Type type = SocialObject.Type.Movie;
        UUID uuid = UUID.randomUUID();

        Group group = new Group(uuid, ownerId, name, date, location, address, type, attendees, description, invited);
        Map<String, Object> map = groupbuilder.toMap(group);
        assertEquals(groupbuilder.toObject(uuid.toString(), map), group);

    }

    @Test
    public void publicToMapWorks() {
        SocialObject.Type type = SocialObject.Type.Movie;
        UUID uuid = UUID.randomUUID();

        Event event = new Event(uuid, ownerId, name, date, location, address, description, type, attendees, minAge, price, tags, email);
        Map<String, Object> map = eventBuilder.toMap(event);
        assertEquals(eventBuilder.toObject(uuid.toString(), map), event);

    }

    private void assertDateEquals(LocalDateTime d1, LocalDateTime d2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd HH mm");
        assertEquals(d1.format(formatter), d2.format(formatter));
    }
}
