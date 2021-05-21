package com.ncnf.database.builders;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.DatabaseService;
import com.ncnf.models.Organization;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ncnf.utilities.StringCodes.ADDRESS_KEY;
import static com.ncnf.utilities.StringCodes.ADMIN_KEY;
import static com.ncnf.utilities.StringCodes.EMAIL_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.ORGANIZED_EVENTS;
import static com.ncnf.utilities.StringCodes.PHONE_NB_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class OrganizationBuilderTests {

    private final DatabaseService db = Mockito.mock(DatabaseService.class);

    private final OrganizationBuilder builder = new OrganizationBuilder();

    private final Map<String, Object> data = new HashMap<>();
    private UUID uuid = UUID.randomUUID();
    private String name = "name";
    private String address = "address";
    private String email = "foo@bar.com";
    private GeoPoint location = new GeoPoint(1,1);
    private String phoneNb = "phoneNb";
    private List<String> admins = new ArrayList<>();
    private String admin1 = "admin1";
    private List<String> events = new ArrayList<>();
    private String event1 = "event1";


    @Before
    public void initMap(){
        admins.add(admin1);
        events.add(event1);
    }

    @Test
    public void buildTest(){
        data.put(UUID_KEY, uuid.toString());
        data.put(NAME_KEY, name);
        data.put(LOCATION_KEY, location);
        data.put(ADDRESS_KEY, address);
        data.put(EMAIL_KEY, email);
        data.put(PHONE_NB_KEY, phoneNb);
        data.put(ORGANIZED_EVENTS, events);

        assertNull(builder.toObject(uuid.toString(), data));
        data.put(ADMIN_KEY, admins);
        Organization org = builder.toObject(uuid.toString(), data);
        Organization org2 = new Organization(uuid, name, location, address, email, phoneNb, admins, events);
        assertNotNull(org);
        assertTrue(org.getEventIds().contains(event1));
        assertEquals(org.getName(), name);

        assertEquals(org, org2);
    }

    @Test
    public void buildOnErrorTest() {
        Organization org = new Organization(uuid, name, location, address, email, phoneNb, admins, events);
        Map<String, Object> map = builder.toMap(org);
        Organization org2 = builder.toObject(uuid.toString(), map);

        assertEquals(org, org2);
    }
}
