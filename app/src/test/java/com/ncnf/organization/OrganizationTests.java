package com.ncnf.organization;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class OrganizationTests {

    @Test
    public void constructorEqualsHashTest(){
        Organization org1 = new Organization("Test1", new GeoPoint(1,1), "address", "phone", "first_owner");
        Organization org2 = new Organization("Test1", new GeoPoint(1,1), "address", "phone", "first_owner");
        UUID uuid = UUID.randomUUID();
        Organization o1 = new Organization(uuid,"Test1", new GeoPoint(1,1), "address", "phone", Arrays.asList(new String[]{"originalOwner"}), new ArrayList<>());
        Organization o2 = new Organization(uuid,"Test 2", new GeoPoint(1,1), "address", "phone", Arrays.asList(new String[]{"originalOwner"}), new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> {
            Organization o = new Organization(uuid,"Test1", new GeoPoint(1,1), "address", "phone", new ArrayList<>(), new ArrayList<>());
        });


        assertTrue(org1.equals(org1));
        assertFalse(org1.equals(org2));
        assertFalse(org1.equals(null));
        assertFalse(org1.equals(new ArrayList<>()));
        assertTrue(o1.equals(o2));
        assertTrue(o1.hashCode() == Objects.hash(uuid));

    }

    @Test
    public void getterSetterTest(){
        UUID uuid = UUID.randomUUID();
        String name = "Test1";
        GeoPoint location = new GeoPoint(1,1);
        String address = "address";
        String phoneNumber = "phone_number";
        List<String> adminIds =  Arrays.asList(new String[]{"first_owner"});
        List<String>  events = new ArrayList<>();

        Organization org = new Organization(name, new GeoPoint(1,1), address, phoneNumber, "first_owner");
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

        assertEquals(org.getPhoneNumber(), phoneNumber);
        String phoneNumber2 = "phone2";
        org.setPhoneNumber(phoneNumber2);
        assertEquals(org.getPhoneNumber(), phoneNumber2);

        assertEquals(org.getAdminIds(), adminIds);
        List<String> adminIds2 =  Arrays.asList(new String[]{"original"});
        org.setAdminIds(adminIds2);
        assertEquals(org.getAdminIds(), adminIds2);

    }

    @Test
    public void addDeleteAdminTest(){
        String admin1 = "first_admin";
        String admin2 = "second_admin";
        String admin3 = "third_admin";

        Organization org = new Organization("Test1", new GeoPoint(1,1), "address", "phone", admin1);
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

        Organization org = new Organization("Test1", new GeoPoint(1,1), "address", "phone", admin1);
        assertTrue(org.getEventIds().size() == 0);

        assertTrue(org.addEvent(event2) && org.getEventIds().size() == 1 && org.getEventIds().contains(event2));

        assertTrue(!org.removeEvent(event3) && org.getEventIds().size() == 1 && !org.getEventIds().contains(event3));

        assertTrue(!org.removeEvent(null) && org.getEventIds().size() == 1 && org.getEventIds().contains(event2));

        assertTrue(org.removeEvent(event2) && org.getEventIds().size() == 0 && !org.getEventIds().contains(event2));

        assertTrue(!org.addEvent(null) && org.getEventIds().size() == 0 && !org.getEventIds().contains(null));
    }
}
