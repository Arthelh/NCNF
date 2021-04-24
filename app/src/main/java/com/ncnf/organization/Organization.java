package com.ncnf.organization;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private List<String> adminIds;
    private List<String>  events;

    public Organization(String name, GeoPoint location, String address, String phoneNumber, String originalOwner) {

        this(UUID.randomUUID(), name, location, address, phoneNumber, new ArrayList<String>() {{ add(originalOwner); }}, new ArrayList<>());
    }

    public Organization(UUID uuid, String name, GeoPoint location, String address, String phoneNumber, List<String> adminIds, List<String> events) {
        if(isArrayEmpty(adminIds) || !checkAdminIds(adminIds)){
            throw new IllegalArgumentException("Organization should have at least one admin created when created");
        }

        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.adminIds = adminIds;
        this.events = events;
    }

    public boolean checkAdminIds(List<String> l){
        for(String s : l){
            if(isStringEmpty(s)){
                return false;
            }
        }
        return true;
    }

    public CompletableFuture<DatabaseResponse> saveToDB(DatabaseService db){
        if(uuid == null || isArrayEmpty(adminIds)){
            return CompletableFuture.completedFuture(new DatabaseResponse(false, null, new IllegalStateException()));
        }
        Map<String, Object> data = new HashMap<>();
        data.put(UUID_KEY, uuid.toString());
        data.put(NAME_KEY, name);
        data.put(LOCATION_KEY, location);
        data.put(ADDRESS_KEY, address);
        data.put(PHONE_NB_KEY, phoneNumber);
        data.put(ADMIN_KEY, adminIds);
        data.put(ORGANIZED_EVENTS_KEY, events);

        return db.setDocument(ORGANIZATIONS_COLLECTION_KEY + uuid.toString(), data);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getAdminIds() {
        return adminIds;
    }

    public List<String> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAdminIds(List<String> adminIds) {
        this.adminIds = new ArrayList<>(adminIds);
    }

    public void setEvents(List<String> events) {
        this.events = new ArrayList<>(events);
    }

    public boolean addAdmin(String adminId){
        if(isStringEmpty(adminId)){
            return false;
        }
        return this.adminIds.add(adminId);
    }

    public boolean deleteAdmin(String adminId){
        if(this.adminIds.size() == 1 && this.adminIds.contains(adminId)){
            throw new IllegalStateException("Organization should have at least one admin");
        }
        if(isStringEmpty(adminId)){
            return false;
        }
        return this.adminIds.remove(adminId);
    }

    public boolean addEvent(String eventId){
        if(isStringEmpty(eventId)){
            return false;
        }
        return this.events.add(eventId);
    }

    public boolean removeEvent(String eventId){
        if(isStringEmpty(eventId)){
            return false;
        }
        return this.events.remove(eventId);
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
