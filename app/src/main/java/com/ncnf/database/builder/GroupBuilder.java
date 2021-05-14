package com.ncnf.database.builder;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.socialObject.Group;
import com.ncnf.socialObject.SocialObject;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
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
        Date d = ((Timestamp) data.getOrDefault(DATE_KEY, new Date())).toDate();
        LocalDateTime date = LocalDateTime.of(d.getYear(), d.getMonth(), d.getDay(), d.getHours(), d.getMinutes());
        GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
        String address = data.get(ADDRESS_KEY).toString();
        String typeStr = data.get(TYPE_KEY).toString();
        SocialObject.Type type = SocialObject.Type.valueOf(typeStr);
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
        //TODO FIX THIS
        Date d = new Date(group.getDate().getYear(), group.getDate().getMonth().getValue(), group.getDate().getDayOfYear(), group.getDate().getHour(), group.getDate().getMinute());
        map.put(DATE_KEY, new Timestamp(d));
        map.put(TYPE_KEY, group.getType().toString());
        map.put(ATTENDEES_KEY, group.getAttendees());
        map.put(DESCRIPTION_KEY, group.getDescription());
        map.put(LOCATION_KEY, group.getLocation());
        map.put(ADDRESS_KEY, group.getAddress());

        return map;
    }
}
