package com.ncnf.organization;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

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

import static com.ncnf.utilities.StringCodes.ADDRESS_KEY;
import static com.ncnf.utilities.StringCodes.ADMIN_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.ORGANIZED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.PHONE_NB_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class OrganizationBuilderTests {

    private final DatabaseService db = Mockito.mock(DatabaseService.class);

    private final OrganizationBuilder builder = new OrganizationBuilder(db);

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

        assertEquals(builder.build(data), null);
        data.put(ADMIN_KEY, admins);
        Organization org = builder.build(data);
        Organization org2 = new Organization(uuid, name, location, address, phoneNb, admins, events);
        assertTrue(org != null);
        assertTrue(org.getEvents().contains(event1));
        assertTrue(org.getName().equals(name));

        assertEquals(org, org2);
    }

    @Test
    public void buildOnErrorTest() {
        String errorMessage = "Error Test";
        DatabaseResponse response = new DatabaseResponse(false, null, new Exception(errorMessage));
        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        CompletableFuture<DatabaseResponse> query = builder.loadFromDB(uuid.toString());
        try {
            assertFalse(query.get().isSuccessful());
            assertEquals(errorMessage, query.get().getException().getMessage());
        } catch (Exception e) {
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void buildOnMissingDataTest(){
        //Admins missing
        data.put(UUID_KEY, uuid.toString());
        data.put(NAME_KEY, name);
        data.put(LOCATION_KEY, location);
        data.put(ADDRESS_KEY, address);
        data.put(PHONE_NB_KEY, phoneNb);
        data.put(ORGANIZED_EVENTS_KEY, events);
        DatabaseResponse response = new DatabaseResponse(true, data,null);
        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(response));
        CompletableFuture<DatabaseResponse> query = builder.loadFromDB(uuid.toString());
        try {
            DatabaseResponse databaseResponse = query.get();
            assertEquals(databaseResponse.getResult(), null);
        } catch(Exception e){
           Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void buildWorksTest(){
        data.put(UUID_KEY, uuid.toString());
        data.put(NAME_KEY, name);
        data.put(LOCATION_KEY, location);
        data.put(ADDRESS_KEY, address);
        data.put(PHONE_NB_KEY, phoneNb);
        data.put(ADMIN_KEY, admins);
        data.put(ORGANIZED_EVENTS_KEY, events);
        DatabaseResponse response = new DatabaseResponse(true, data,null);
        when(db.getData(anyString())).thenReturn(CompletableFuture.completedFuture(response));
        CompletableFuture<DatabaseResponse> query = builder.loadFromDB(uuid.toString());
        try {
            Organization org = (Organization) query.get().getResult();
            assertEquals(org.getName(), name);
            assertEquals(org.getUuid(), uuid);
            assertEquals(location, org.getLocation());
            assertEquals(org.getEvents().get(0), event1);
            assertEquals(org.getAdminIds().get(0), admin1);
            assertEquals(phoneNb, org.getPhoneNumber());
            assertEquals(address, org.getAddress());
        } catch(Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }
}
