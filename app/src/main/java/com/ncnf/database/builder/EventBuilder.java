package com.ncnf.database.builder;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.event.Event;
import com.ncnf.event.PrivateEvent;
import com.ncnf.event.PublicEvent;
import com.ncnf.event.Tag;
import com.ncnf.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ncnf.Utils.ADDRESS_KEY;
import static com.ncnf.Utils.ATTENDEES_KEY;
import static com.ncnf.Utils.DATE_KEY;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.DESCRIPTION_KEY;
import static com.ncnf.Utils.EMAIL_KEY;
import static com.ncnf.Utils.INVITED_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.MIN_AGE_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static com.ncnf.Utils.OWNER_KEY;
import static com.ncnf.Utils.PRICE_KEY;
import static com.ncnf.Utils.TAGS_LIST_KEY;
import static com.ncnf.Utils.TYPE_KEY;
import static com.ncnf.Utils.UUID_KEY;
import static com.ncnf.Utils.VISIBILITY_KEY;

public class EventBuilder extends DatabaseObjectBuilder<Event> {

    @Override
    public Event toObject(String uuid, Map<String, Object> data) {
        Log.d(DEBUG_TAG, uuid);

        String ownerId = data.get(OWNER_KEY).toString();
        String uuidStr = data.get(UUID_KEY).toString();
        String name = (String) data.get(NAME_KEY);
        Date date = ((Timestamp) data.get(DATE_KEY)).toDate();
        GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
        String address = data.get(ADDRESS_KEY).toString();
        String visibilityStr = data.get(VISIBILITY_KEY).toString();
        Event.Visibility visibility = Event.Visibility.valueOf(visibilityStr);
        String typeStr = data.get(TYPE_KEY).toString();
        Event.Type type = Event.Type.valueOf(typeStr);
        List<String> attendees = (List<String>) data.get(ATTENDEES_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);

        if(visibility.equals(Event.Visibility.PUBLIC)){
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
            return new PublicEvent(ownerId, UUID.fromString(uuidStr), name, date, location, address, description, type, attendees, minAge, price, tags, email);

        } else {
            List<String> invited = (ArrayList) data.get(INVITED_KEY);
            return new PrivateEvent(ownerId, UUID.fromString(uuidStr), name, date, location, address, type, attendees, description, invited);
        }
    }

    @Override
    public Map<String, Object> toMap(Event event) {
        Map<String, Object> map = new HashMap<>();
        map.put(UUID_KEY, event.getUuid().toString());
        map.put(NAME_KEY, event.getName());
        map.put(DATE_KEY, new Timestamp(event.getDate()));
        map.put(LOCATION_KEY, event.getLocation());
        map.put(ADDRESS_KEY, event.getAddress());
        map.put(VISIBILITY_KEY, event.getVisibility().toString());
        map.put(TYPE_KEY, event.getType().toString());
        map.put(ATTENDEES_KEY, event.getAttendees());
        map.put(DESCRIPTION_KEY, event.getDescription());
        map.put(OWNER_KEY, event.getOwnerId());

        if (event.getVisibility() == Event.Visibility.PRIVATE) {
            PrivateEvent privateEvent = (PrivateEvent) event;
            map.put(INVITED_KEY, privateEvent.getInvited());
        } else {
            PublicEvent publicEvent = (PublicEvent) event;
            map.put(MIN_AGE_KEY, publicEvent.getMinAge());
            map.put(PRICE_KEY, publicEvent.getPrice());
            map.put(TAGS_LIST_KEY, publicEvent.getTags());
            map.put(EMAIL_KEY, publicEvent.getEmail());
        }

        return map;
    }
}
