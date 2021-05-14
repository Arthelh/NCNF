package com.ncnf.database.builder;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.socialObject.Event;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.socialObject.Tag;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.ncnf.utilities.StringCodes.ADDRESS_KEY;
import static com.ncnf.utilities.StringCodes.ATTENDEES_KEY;
import static com.ncnf.utilities.StringCodes.DATE_KEY;
import static com.ncnf.utilities.StringCodes.DESCRIPTION_KEY;
import static com.ncnf.utilities.StringCodes.EMAIL_KEY;
import static com.ncnf.utilities.StringCodes.GEOHASH_KEY;
import static com.ncnf.utilities.StringCodes.LAT_KEY;
import static com.ncnf.utilities.StringCodes.LNG_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.MIN_AGE_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.OWNER_KEY;
import static com.ncnf.utilities.StringCodes.PRICE_KEY;
import static com.ncnf.utilities.StringCodes.TAGS_LIST_KEY;
import static com.ncnf.utilities.StringCodes.TYPE_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;


public class EventBuilder extends DatabaseObjectBuilder<Event> {

    @Override
    public Event toObject(String uuid, @NotNull Map<String, Object> data){
        Objects.requireNonNull(data);

        String ownerId = data.getOrDefault(OWNER_KEY, "").toString();
        String uuidStr = data.getOrDefault(UUID_KEY, "").toString();
        String name = data.getOrDefault(NAME_KEY, "").toString();
        //TODO FIX THIS
        Date d = ((Timestamp) data.getOrDefault(DATE_KEY, new Date())).toDate();
        LocalDateTime date = LocalDateTime.of(d.getYear(), d.getMonth(), d.getDay(), d.getHours(), d.getMinutes());
        GeoPoint location = (GeoPoint) data.getOrDefault(LOCATION_KEY, new GeoPoint(0, 0));
        String address = data.getOrDefault(ADDRESS_KEY, "").toString();
        String typeStr = data.getOrDefault(TYPE_KEY, SocialObject.Type.NOTHING).toString();
        SocialObject.Type type = SocialObject.Type.valueOf(typeStr);
        List<String> attendees = (List<String>) data.getOrDefault(ATTENDEES_KEY, new ArrayList<>());
        String description = (String) data.getOrDefault(DESCRIPTION_KEY, "");

        int minAge = 0;
        if(data.get(MIN_AGE_KEY) instanceof Long){
            minAge = ((Long) data.get(MIN_AGE_KEY)).intValue();
        } else {
            minAge = (int) data.get(MIN_AGE_KEY);
        }
        double price = (double) data.getOrDefault(PRICE_KEY, 0);
        List<Tag> tags = (List<Tag>) data.getOrDefault(TAGS_LIST_KEY, new ArrayList<>());
        String email = data.getOrDefault(EMAIL_KEY, "").toString();
        //TODO : should serialize / deserialize tags before adding them
        return new Event(ownerId, UUID.fromString(uuidStr), name, date, location, address, description, type, attendees, minAge, price, tags, email);
    }

    @Override
    public Map<String, Object> toMap(Event event) {
        Map<String, Object> map = new HashMap<>();
        map.put(UUID_KEY, event.getUuid().toString());
        map.put(NAME_KEY, event.getName());
        //TODO FIX THIS
        Date d = new Date(event.getDate().getYear(), event.getDate().getMonth().getValue(), event.getDate().getDayOfYear(), event.getDate().getHour(), event.getDate().getMinute());
        map.put(DATE_KEY, new Timestamp(d));
        map.put(LOCATION_KEY, event.getLocation());
        map.put(ADDRESS_KEY, event.getAddress());
        map.put(TYPE_KEY, event.getType().toString());
        map.put(ATTENDEES_KEY, event.getAttendees());
        map.put(DESCRIPTION_KEY, event.getDescription());
        map.put(OWNER_KEY, event.getOwnerId());

        //Store GeoHash, Lat and Lng in map
        double lat = event.getLocation().getLatitude();
        double lng = event.getLocation().getLongitude();
        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
        map.put(GEOHASH_KEY, hash);
        map.put(LAT_KEY, lat);
        map.put(LNG_KEY, lng);

        map.put(MIN_AGE_KEY, event.getMinAge());
        map.put(PRICE_KEY, event.getPrice());
        map.put(TAGS_LIST_KEY, event.getTags());
        map.put(EMAIL_KEY, event.getEmail());

        return map;
    }
}
