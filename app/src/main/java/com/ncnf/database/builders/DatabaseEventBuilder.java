package com.ncnf.database.builders;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.models.Event;
import com.ncnf.models.EventTag;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.ncnf.utilities.StringCodes.ADDRESS_KEY;
import static com.ncnf.utilities.StringCodes.MEMBERS_KEY;
import static com.ncnf.utilities.StringCodes.DATE_KEY;
import static com.ncnf.utilities.StringCodes.DESCRIPTION_KEY;
import static com.ncnf.utilities.StringCodes.EMAIL_KEY;
import static com.ncnf.utilities.StringCodes.GEOHASH_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.MIN_AGE_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.OWNER_KEY;
import static com.ncnf.utilities.StringCodes.PRICE_KEY;
import static com.ncnf.utilities.StringCodes.TAGS_LIST_KEY;
import static com.ncnf.utilities.StringCodes.TYPE_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;


public class DatabaseEventBuilder extends DatabaseObjectBuilder<Event> {

    @Override
    public Event toObject(String uuid, @NotNull Map<String, Object> data){
        Objects.requireNonNull(data);

        String ownerId = data.getOrDefault(OWNER_KEY, "").toString();
        String uuidStr = data.getOrDefault(UUID_KEY, "").toString();
        String name = data.getOrDefault(NAME_KEY, "").toString();
        Date date = ((Timestamp) data.getOrDefault(DATE_KEY, new Date())).toDate();
        LocalDateTime datetime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        GeoPoint location = (GeoPoint) data.getOrDefault(LOCATION_KEY, new GeoPoint(0, 0));
        String address = data.getOrDefault(ADDRESS_KEY, "").toString();
        String typeStr = data.getOrDefault(TYPE_KEY, Event.Type.NOTHING).toString();
        Event.Type type = Event.Type.valueOf(typeStr);
        List<String> attendees = (List<String>) data.getOrDefault(MEMBERS_KEY, new ArrayList<>());
        String description = (String) data.getOrDefault(DESCRIPTION_KEY, "");

        int minAge = 0;
        if(data.get(MIN_AGE_KEY) instanceof Long){
            minAge = ((Long) data.get(MIN_AGE_KEY)).intValue();
        } else {
            minAge = (int) data.get(MIN_AGE_KEY);
        }
        double price = (double) data.getOrDefault(PRICE_KEY, 0);
        List<EventTag> eventTags = (List<EventTag>) data.getOrDefault(TAGS_LIST_KEY, new ArrayList<>());
        String email = data.getOrDefault(EMAIL_KEY, "").toString();
        //TODO : should serialize / deserialize tags before adding them
        return new Event(ownerId, UUID.fromString(uuidStr), name, datetime, location, address, description, type, attendees, minAge, price, eventTags, email);
    }

    @Override
    public Map<String, Object> toMap(Event event) {
        Map<String, Object> map = new HashMap<>();
        map.put(UUID_KEY, event.getUuid().toString());
        map.put(NAME_KEY, event.getName());
        Date date = Date.from(event.getDate().atZone(ZoneId.systemDefault()).toInstant());
        map.put(DATE_KEY, new Timestamp(date));
        map.put(LOCATION_KEY, event.getLocation());
        map.put(ADDRESS_KEY, event.getAddress());
        map.put(TYPE_KEY, event.getType().toString());
        map.put(MEMBERS_KEY, event.getAttendees());
        map.put(DESCRIPTION_KEY, event.getDescription());
        map.put(OWNER_KEY, event.getOwnerId());

        //Store GeoHash in map
        double lat = event.getLocation().getLatitude();
        double lng = event.getLocation().getLongitude();
        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
        map.put(GEOHASH_KEY, hash);

        map.put(MIN_AGE_KEY, event.getMinAge());
        map.put(PRICE_KEY, event.getPrice());
        map.put(TAGS_LIST_KEY, event.getEventTags());
        map.put(EMAIL_KEY, event.getEmail());

        return map;
    }
}
