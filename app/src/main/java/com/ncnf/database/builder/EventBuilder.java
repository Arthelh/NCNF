package com.ncnf.database.builder;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.socialObject.Event;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.socialObject.Tag;

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
    public Event toObject(String uuid, Map<String, Object> data) {
        Objects.requireNonNull(data);

        String ownerId = data.get(OWNER_KEY).toString();
        String uuidStr = data.get(UUID_KEY).toString();
        String name = (String) data.get(NAME_KEY);
        Date date = ((Timestamp) data.get(DATE_KEY)).toDate();
        GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
        String address = data.get(ADDRESS_KEY).toString();
        String typeStr = data.get(TYPE_KEY).toString();
        SocialObject.Type type = SocialObject.Type.valueOf(typeStr);
        List<String> attendees = (List<String>) data.get(ATTENDEES_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);

        int minAge = 0;
        if(data.get(MIN_AGE_KEY) instanceof Long){
            minAge = ((Long) data.get(MIN_AGE_KEY)).intValue();
        } else {
            minAge = (int) data.get(MIN_AGE_KEY);
        }
        double price = (double) data.get(PRICE_KEY);
        List<Tag> tags = (List<Tag>) data.get(TAGS_LIST_KEY);
        String email = data.get(EMAIL_KEY).toString();
        //TODO : should serialize / deserialize tags before adding them
        return new Event(ownerId, UUID.fromString(uuidStr), name, date, location, address, description, type, attendees, minAge, price, tags, email);
    }

    @Override
    public Map<String, Object> toMap(Event event) {
        Map<String, Object> map = new HashMap<>();
        map.put(UUID_KEY, event.getUuid().toString());
        map.put(NAME_KEY, event.getName());
        map.put(DATE_KEY, new Timestamp(event.getDate()));
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
