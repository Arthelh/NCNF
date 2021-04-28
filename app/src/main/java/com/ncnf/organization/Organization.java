package com.ncnf.organization;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.utilities.InputValidator;

import static com.ncnf.utilities.InputValidator.checkCompleteList;
import static com.ncnf.utilities.StringCodes.ADDRESS_KEY;
import static com.ncnf.utilities.StringCodes.ADMIN_KEY;
import static com.ncnf.utilities.StringCodes.LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.NAME_KEY;
import static com.ncnf.utilities.StringCodes.ORGANIZATIONS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.ORGANIZED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.PHONE_NB_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;
import static com.ncnf.utilities.InputValidator.isInvalidArray;
import static com.ncnf.utilities.InputValidator.isInvalidString;

public class Organization {

    private final UUID uuid;
    private String name;
    private GeoPoint location;
    private String address;
    private String phoneNumber;
    private List<String> adminIds;
    private List<String>  events;

    public Organization(String name, GeoPoint location, String address, String phoneNumber, @NonNull String originalOwner) {
        this(UUID.randomUUID(), name, location, address, phoneNumber, new ArrayList<>(Collections.singletonList(originalOwner)) , new ArrayList<>());
    }

    public Organization(UUID uuid, String name, GeoPoint location, String address, String phoneNumber, List<String> adminIds, List<String> events) {
        if (InputValidator.checkCompleteList(adminIds) || adminIds.isEmpty()){
            throw new IllegalArgumentException("Organization should have at least one admin created when created");
        }
        if (events == null){
            throw new IllegalArgumentException("List of events cannot be null");
        }
        if(uuid == null){
            throw new IllegalArgumentException("The UUID cannot be null");
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
        //TODO check with db values?
        return true;
        //return l.stream().noneMatch(InputValidator::isInvalidString);
    }

    public CompletableFuture<DatabaseResponse> saveToDB(DatabaseService db){

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
        return Collections.unmodifiableList(adminIds);
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
        if(checkCompleteList(adminIds)){
            throw new IllegalArgumentException("List of new admins cannot be null or empty");
        }
        this.adminIds = new ArrayList<>(adminIds);
    }

    public void replaceEvents(List<String> events) {
        if(InputValidator.checkCompleteList(events)){
            throw new IllegalArgumentException("List of new events cannot be null or empty");
        }
        this.events = new ArrayList<>(events);
    }

    public void addListEvents(List<String> events){
        if(InputValidator.checkCompleteList(events)){
            throw new IllegalArgumentException("List of new events cannot be null or empty");
        }
        this.events.addAll(events);
        //remove duplicates
        this.events = this.events.stream().distinct().collect(Collectors.toList());
    }

    public void clearEvents() {this.events.clear();}

    public boolean addAdmin(String adminId){
        if(isInvalidString(adminId)){
            return false;
        }
        return this.adminIds.add(adminId);
    }

    public boolean deleteAdmin(String adminId){
        if(this.adminIds.size() == 1 && this.adminIds.contains(adminId)){
            throw new IllegalStateException("Organization should have at least one admin");
        }
        if(isInvalidString(adminId)){
            return false;
        }
        return this.adminIds.remove(adminId);
    }

    public boolean addEvent(String eventId){
        if(isInvalidString(eventId)){
            return false;
        }
        return this.events.add(eventId);
    }

    public boolean removeEvent(String eventId){
        if(isInvalidString(eventId)){
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
