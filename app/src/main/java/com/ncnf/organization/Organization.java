package com.ncnf.organization;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

import static com.ncnf.Utils.ADDRESS_KEY;
import static com.ncnf.Utils.ADMIN_KEY;
import static com.ncnf.Utils.EVENTS_COLLECTION_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static com.ncnf.Utils.ORGANIZATIONS_COLLECTION_KEY;
import static com.ncnf.Utils.ORGANIZED_EVENTS_KEY;
import static com.ncnf.Utils.PHONE_NB_KEY;
import static com.ncnf.Utils.UUID_KEY;
import static com.ncnf.utilities.InputValidator.isArrayEmpty;
import static com.ncnf.utilities.InputValidator.isStringEmpty;

public class Organization {

    private final UUID uuid;
    private String name;
    private GeoPoint location;
    private String address;
    private String phoneNumber;
    private List<String> adminId;
    private List<String>  events;

    public GeoPoint getLocation() {
        return location;
    }

    public Organization(String name, GeoPoint location, String address, String phoneNumber, String originalOwner) {
        this(UUID.randomUUID(), name, location, address, phoneNumber, Arrays.asList(new String[]{originalOwner}), new ArrayList<>());
    }

    public Organization(UUID uuid, String name, GeoPoint location, String address, String phoneNumber, List<String> adminId, List<String> events) {
        if(isArrayEmpty(adminId)){
            throw new IllegalArgumentException("Organization should have at least one admin created when created");
        }

        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.adminId = adminId;
        this.events = events;
    }

    public CompletableFuture<DatabaseResponse> saveToDB(DatabaseService db){
        if(uuid == null || isArrayEmpty(adminId)){
            return CompletableFuture.completedFuture(new DatabaseResponse(false, null, new IllegalStateException()));
        }
        Map<String, Object> data = new HashMap<>();
        data.put(UUID_KEY, uuid.toString());
        data.put(NAME_KEY, name);
        data.put(LOCATION_KEY, location);
        data.put(ADDRESS_KEY, address);
        data.put(PHONE_NB_KEY, phoneNumber);
        data.put(ADMIN_KEY, adminId);
        data.put(ORGANIZED_EVENTS_KEY, events);

        return db.setDocument(ORGANIZATIONS_COLLECTION_KEY + uuid.toString(), data);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getAdminId() {
        return adminId;
    }

    public void setAdminId(List<String> adminId) {
        this.adminId = adminId;
    }

    public boolean addAdmin(String adminId){
        return this.addToList(adminId, this.adminId);
    }

    public boolean deleteAdmin(String adminId){
        return this.removeFromList(adminId, this.adminId);
    }

    public boolean addEvent(String eventId){
        return this.addToList(eventId, this.events);
    }

    public boolean removeEvent(String eventId){
        return this.removeFromList(eventId, this.events);
    }

    private boolean addToList(String id, List l){
        if(isStringEmpty(id)){
            return false;
        }
        return l.add(id);
    }

    private boolean removeFromList(String id, List l){
        if(isStringEmpty(id)){
            return false;
        }
        if(l.contains(id)){
            return l.remove(id);
        }
        return true;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
