package com.ncnf.organization;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.database.builder.OrganizationBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.ADDRESS_KEY;
import static com.ncnf.Utils.ADMIN_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static com.ncnf.Utils.ORGANIZED_EVENTS_KEY;
import static com.ncnf.Utils.PHONE_NB_KEY;
import static com.ncnf.Utils.UUID_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class OrganizationBuilderTests {

    private final DatabaseService db = Mockito.mock(DatabaseService.class);

    private final OrganizationBuilder builder = new OrganizationBuilder();

    private final Map<String, Object> data = new HashMap<>();
    private UUID uuid = UUID.randomUUID();
    private String name = "name";
    private String address = "address";
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
        data.put(PHONE_NB_KEY, phoneNb);
        data.put(ORGANIZED_EVENTS_KEY, events);

        assertEquals(builder.toObject(uuid.toString(), data), null);
        data.put(ADMIN_KEY, admins);
        Organization org = builder.toObject(uuid.toString(), data);
        Organization org2 = new Organization(uuid, name, location, address, phoneNb, admins, events);
        assertTrue(org != null);
        assertTrue(org.getEventIds().contains(event1));
        assertTrue(org.getName().equals(name));

        assertEquals(org, org2);
    }

    @Test
    public void buildOnErrorTest() {
        Organization org = new Organization(uuid, name, location, address, phoneNb, admins, events);
        Map<String, Object> map = builder.toMap(org);
        Organization org2 = builder.toObject(uuid.toString(), map);

        assertEquals(org, org2);
    }
}
