package com.ncnf.database.builders;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.models.Group;
import com.ncnf.models.SocialObject;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
import static com.ncnf.utilities.StringCodes.INVITED_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.OWNER_KEY;
import static com.ncnf.utilities.StringCodes.TYPE_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;


public class GroupBuilder extends DatabaseObjectBuilder<Group>{
    @Override
    public Group toObject(String uuid, @NotNull Map data) {
        Objects.requireNonNull(data);

        String ownerId = data.get(OWNER_KEY).toString();
        String uuidStr = data.get(UUID_KEY).toString();
        String name = (String) data.get(NAME_KEY);
        Date date = ((Timestamp) data.getOrDefault(DATE_KEY, new Date())).toDate();
        LocalDateTime datetime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
        String address = data.get(ADDRESS_KEY).toString();
        String typeStr = data.get(TYPE_KEY).toString();
        SocialObject.Type type = SocialObject.Type.valueOf(typeStr);
        List<String> attendees = (List<String>) data.get(ATTENDEES_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        List<String> invited = (List<String>) data.get(INVITED_KEY);

        return new Group(UUID.fromString(uuidStr), ownerId, name, datetime, location, address, type, attendees, description, invited);
    }

    @Override
    public Map<String, Object> toMap(Group group) {
        Map<String, Object> map = new HashMap<>();
        map.put(UUID_KEY, group.getUuid().toString());
        map.put(OWNER_KEY, group.getOwnerId());
        map.put(NAME_KEY, group.getName());
        Date date = Date.from(group.getDate().atZone(ZoneId.systemDefault()).toInstant());
        map.put(DATE_KEY, new Timestamp(date));
        map.put(TYPE_KEY, group.getType().toString());
        map.put(ATTENDEES_KEY, group.getAttendees());
        map.put(DESCRIPTION_KEY, group.getDescription());
        map.put(LOCATION_KEY, group.getLocation());
        map.put(ADDRESS_KEY, group.getAddress());

        return map;
    }
}
