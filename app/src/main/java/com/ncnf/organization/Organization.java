package com.ncnf.organization;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;
import com.ncnf.utilities.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.ncnf.utilities.StringCodes.ORGANIZATIONS_COLLECTION_KEY;

public class Organization {

    private final UUID uuid;
    private String name;
    private GeoPoint location;
    private String address;
    private String phoneNumber;
    private List<String> adminIds;
    private List<String> eventIds;

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
        this.eventIds = events;
    }

    public boolean checkAdminIds(List<String> l){
        //TODO check with db values?
        return true;
        //return l.stream().noneMatch(InputValidator::isInvalidString);
    }

    public CompletableFuture<Boolean> saveToDB(DatabaseService db){

        return db.setDocument(ORGANIZATIONS_COLLECTION_KEY + uuid.toString(), this);
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

    public List<String> getEventIds() {
        return Collections.unmodifiableList(eventIds);
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
        if(InputValidator.checkCompleteList(adminIds)){
            throw new IllegalArgumentException("List of new admins cannot be null or empty");
        }
        this.adminIds = new ArrayList<>(adminIds);
    }


    public void replaceEvents(List<String> events) {
        if(InputValidator.checkCompleteList(events)){
            throw new IllegalArgumentException("List of new events cannot be null or empty");
        }
        this.eventIds = new ArrayList<>(events);
    }

    public void addListEvents(List<String> events){
        if(InputValidator.checkCompleteList(events)){
            throw new IllegalArgumentException("List of new events cannot be null or empty");
        }
        this.eventIds.addAll(events);
        //remove duplicates
        this.eventIds = this.eventIds.stream().distinct().collect(Collectors.toList());
    }

    public void clearEvents() {this.eventIds.clear();}

    public boolean addAdmin(String adminId){
        if(InputValidator.isInvalidString(adminId)){
            return false;
        }
        return this.adminIds.add(adminId);
    }

    public boolean deleteAdmin(String adminId){
        if(this.adminIds.size() == 1 && this.adminIds.contains(adminId)){
            throw new IllegalStateException("Organization should have at least one admin");
        }
        if(InputValidator.isInvalidString(adminId)){
            return false;
        }
        return this.adminIds.remove(adminId);
    }

    public boolean addEvent(String eventId){
        if(InputValidator.isInvalidString(eventId)){
            return false;
        }
        return this.eventIds.add(eventId);
    }

    public boolean removeEvent(String eventId){
        if(InputValidator.isInvalidString(eventId)){
            return false;
        }
        return this.eventIds.remove(eventId);
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
