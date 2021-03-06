package com.ncnf.models;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrganizationTests {

    FirebaseDatabase db = mock(FirebaseDatabase.class);

    @Test
    public void constructorEqualsHashTest(){
        Organization org1 = new Organization("Test1", new GeoPoint(1,1), "address", "foo@bar.com", "phone", "first_owner");
        Organization org2 = new Organization("Test1", new GeoPoint(1,1), "address", "foo@bar.com", "phone", "first_owner");
        UUID uuid = UUID.randomUUID();
        Organization o1 = new Organization(uuid,"Test1", new GeoPoint(1,1), "address", "foo@bar.com", "phone", Arrays.asList("originalOwner"), new ArrayList<>());
        Organization o2 = new Organization(uuid,"Test 2", new GeoPoint(1,1), "address", "foo@bar.com", "phone", Arrays.asList("originalOwner"), new ArrayList<>());

        assertEquals(org1, org1);
        assertNotEquals(org1, org2);
        assertNotEquals(null, org1);
        assertNotEquals(org1, new ArrayList<>());
        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), Objects.hash(uuid));
    }

    @Test
    public void getterSetterTest(){
        UUID uuid = UUID.randomUUID();
        String name = "Test1";
        GeoPoint location = new GeoPoint(1,1);
        String address = "address";
        String email = "email";
        String phoneNumber = "phone_number";
        List<String> adminIds =  Arrays.asList("first_owner");

        Organization org = new Organization(name, new GeoPoint(1,1), address, email, phoneNumber, "first_owner");
        assertEquals(org.getName(), name);
        String name2 = "name2";
        org.setName(name2);
        assertEquals(org.getName(), name2);

        assertEquals(org.getLocation(), location);
        GeoPoint geo2 = new GeoPoint(2,2);
        org.setLocation(geo2);
        assertEquals(org.getLocation(), geo2);

        assertEquals(org.getAddress(), address);
        String address2 = "address2";
        org.setAddress(address2);
        assertEquals(org.getAddress(), address2);

        assertEquals(org.getEmail(), email);
        String email2 = "foo2@bar.com";
        org.setEmail(email2);
        assertEquals(org.getEmail(), email2);

        assertEquals(org.getPhoneNumber(), phoneNumber);
        String phoneNumber2 = "phone2";
        org.setPhoneNumber(phoneNumber2);
        assertEquals(org.getPhoneNumber(), phoneNumber2);

        assertEquals(org.getAdminIds(), adminIds);
        List<String> adminIds2 =  Arrays.asList("original");
        org.setAdminIds(adminIds2);
        assertEquals(org.getAdminIds(), adminIds2);

        assertTrue(org.getEventIds().isEmpty());
        List<String> events = new ArrayList<>();
        events.add("firstEvent");
        org.setEventIds(events);
        assertEquals(org.getEventIds(), events);
        assertEquals(events.get(0), org.getEventIds().get(0));


    }

    @Test
    public void addDeleteAdminTest(){
        String admin1 = "first_admin";
        String admin2 = "second_admin";
        String admin3 = "third_admin";

        Organization org = new Organization("Test1", new GeoPoint(1,1), "address", "foo@bar.com", "phone", admin1);
        assertTrue(org.getAdminIds().size() == 1 && org.getAdminIds().contains(admin1));

        assertTrue(org.addAdmin(admin2) && org.getAdminIds().size() == 2 && org.getAdminIds().contains(admin2));

        assertTrue(!org.removeEvent(admin3) && org.getAdminIds().size() == 2 && !org.getAdminIds().contains(admin3));

        assertTrue(!org.deleteAdmin(null) && org.getAdminIds().size() == 2 && org.getAdminIds().contains(admin2));

        assertTrue(org.deleteAdmin(admin2) && org.getAdminIds().size() == 1 && !org.getAdminIds().contains(admin2));

        assertTrue(!org.addAdmin(null) && org.getAdminIds().size() == 1 && !org.getAdminIds().contains(null));

        assertThrows(IllegalStateException.class, () -> {
            org.deleteAdmin(admin1);
        });
    }

    @Test
    public void addDeleteEvents(){
        String admin1 = "first_admin";

        String event1 = "first_event";
        String event2 = "second_event2";
        String event3 = "third_event3";

        Organization org = new Organization("Test1", new GeoPoint(1,1), "address", "foo@bar.com","phone", admin1);
        assertEquals(0, org.getEventIds().size());

        assertTrue(org.addEvent(event2) && org.getEventIds().size() == 1 && org.getEventIds().contains(event2));

        assertTrue(!org.removeEvent(event3) && org.getEventIds().size() == 1 && !org.getEventIds().contains(event3));

        assertTrue(!org.removeEvent(null) && org.getEventIds().size() == 1 && org.getEventIds().contains(event2));

        assertTrue(org.removeEvent(event2) && org.getEventIds().size() == 0 && !org.getEventIds().contains(event2));

        assertTrue(!org.addEvent(null) && org.getEventIds().size() == 0 && !org.getEventIds().contains(null));
    }

    @Test
    public void saveToDBWorks(){
        when(db.setDocument(anyString(), any())).thenReturn(CompletableFuture.completedFuture(true));
        Organization org1 = new Organization(null, "name", new GeoPoint(0,0), "address", "email", "phoneNumber", Arrays.asList("originalOwner"), new ArrayList<>());
        CompletableFuture<Boolean> future = org1.saveToDB(db);
        try {
            assertFalse(future.get());
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }

        Organization org2 = new Organization(UUID.randomUUID(), "name", new GeoPoint(0,0), "address", "email", "phoneNumber", Arrays.asList("originalOwner"), new ArrayList<>());
        org2.setAdminIds(new ArrayList<>());
        future = org2.saveToDB(db);
        try {
            assertFalse(future.get());
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }

        Organization org3 = new Organization(UUID.randomUUID(), "name", new GeoPoint(0,0), "address", "email", "phoneNumber", Arrays.asList("originalOwner"), new ArrayList<>());
        future = org3.saveToDB(db);
        try {
            assertTrue(future.get());
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }
}
