package com.ncnf.database.builders;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.models.Organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ncnf.utilities.StringCodes.ADDRESS_KEY;
import static com.ncnf.utilities.StringCodes.ADMIN_KEY;
import static com.ncnf.utilities.StringCodes.EMAIL_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.ORGANIZED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.PHONE_NB_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;


public class OrganizationBuilder extends DatabaseObjectBuilder<Organization>{

    @Override
    public Organization toObject(String uuid, Map<String, Object> data) {
        try {
            String uuidStr = data.get(UUID_KEY).toString();
            String name = data.get(NAME_KEY).toString();
            String address = data.get(ADDRESS_KEY).toString();
            String email = data.get(EMAIL_KEY).toString();
            GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
            String phoneNb = data.get(PHONE_NB_KEY).toString();
            List<String> admin = (List<String>) data.get(ADMIN_KEY);
            List<String> events = (List<String>) data.get(ORGANIZED_EVENTS_KEY);

            return new Organization(UUID.fromString(uuidStr), name, location, address, email, phoneNb, admin, events);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Object> toMap(Organization org) {

        Map<String, Object> data = new HashMap<>();
        data.put(UUID_KEY, org.getUuid().toString());
        data.put(NAME_KEY, org.getName());
        data.put(LOCATION_KEY, org.getLocation());
        data.put(ADDRESS_KEY, org.getAddress());
        data.put(EMAIL_KEY, org.getEmail());
        data.put(PHONE_NB_KEY, org.getPhoneNumber());
        data.put(ADMIN_KEY, org.getAdminIds());
        data.put(ORGANIZED_EVENTS_KEY, org.getEventIds());

        return data;
    }
}
