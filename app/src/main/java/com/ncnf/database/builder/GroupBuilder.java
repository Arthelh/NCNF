package com.ncnf.database.builder;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.event.Group;
import com.ncnf.event.Social;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ncnf.Utils.ADDRESS_KEY;
import static com.ncnf.Utils.ATTENDEES_KEY;
import static com.ncnf.Utils.DATE_KEY;
import static com.ncnf.Utils.DESCRIPTION_KEY;
import static com.ncnf.Utils.INVITED_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static com.ncnf.Utils.OWNER_KEY;
import static com.ncnf.Utils.TYPE_KEY;
import static com.ncnf.Utils.UUID_KEY;

public class GroupBuilder extends DatabaseObjectBuilder<Group>{
    @Override
    public Group toObject(String uuid, Map data) {
        String ownerId = data.get(OWNER_KEY).toString();
        String uuidStr = data.get(UUID_KEY).toString();
        String name = (String) data.get(NAME_KEY);
        Date date = ((Timestamp) data.get(DATE_KEY)).toDate();
        GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
        String address = data.get(ADDRESS_KEY).toString();
        String typeStr = data.get(TYPE_KEY).toString();
        Social.Type type = Social.Type.valueOf(typeStr);
        List<String> attendees = (List<String>) data.get(ATTENDEES_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        List<String> invited = (List<String>) data.get(INVITED_KEY);

        return new Group(ownerId, UUID.fromString(uuidStr), name, date, location, address, type, attendees, description, invited);
    }

    @Override
    public Map<String, Object> toMap(Group group) {
        Map<String, Object> map = new HashMap<>();
        map.put(UUID_KEY, group.getUuid().toString());
        map.put(OWNER_KEY, group.getOwnerId());
        map.put(NAME_KEY, group.getName());
        map.put(DATE_KEY, new Timestamp(group.getDate()));
        map.put(TYPE_KEY, group.getType().toString());
        map.put(ATTENDEES_KEY, group.getAttendees());
        map.put(DESCRIPTION_KEY, group.getDescription());
        map.put(LOCATION_KEY, group.getLocation());
        map.put(ADDRESS_KEY, group.getAddress());

        return map;
    }
}
