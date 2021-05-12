package com.ncnf.database.builders;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.builder.DatabaseObjectBuilder;
import com.ncnf.user.organization.Organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ncnf.utilities.StringCodes.*;

public class OrganizationBuilder extends DatabaseObjectBuilder<Organization> {

    @Override
    public Organization toObject(String uuid, Map<String, Object> data) {
        try {
            String uuidStr = data.get(UUID_KEY).toString();
            String name = data.get(NAME_KEY).toString();
            String address = data.get(ADDRESS_KEY).toString();
            GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
            String phoneNb = data.get(PHONE_NB_KEY).toString();
            List<String> admin = (List<String>) data.get(ADMIN_KEY);
            List<String> events = (List<String>) data.get(ORGANIZED_EVENTS_KEY);
            String email = data.get(ORGANIZATION_EMAIL).toString();

            return new Organization(UUID.fromString(uuidStr), name, location, address, email, phoneNb, admin, events);
        } catch (Exception e){
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
        data.put(PHONE_NB_KEY, org.getPhoneNumber());
        data.put(ORGANIZATION_EMAIL, org.getEmail());
        data.put(ADMIN_KEY, org.getAdminIds());
        data.put(ORGANIZED_EVENTS_KEY, org.getEventIds());

        return data;
    }
}
