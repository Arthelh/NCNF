package com.ncnf.user.organization;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.ncnf.database.DatabaseService;
import com.ncnf.utilities.InputValidator;

import static com.ncnf.utilities.StringCodes.*;


public class Organization {

    private final UUID uuid;
    private String name;
    private GeoPoint location;
    private String address;
    private String phoneNumber;
    private String email;
    private List<String> adminIds;
    private List<String> eventIds;

    public Organization(String name, GeoPoint location, String address, String email, String phoneNumber, String originalOwner) {

        this(UUID.randomUUID(), name, location, address, email, phoneNumber, new ArrayList<String>() {{ add(originalOwner); }}, new ArrayList<>());
    }

    public Organization(UUID uuid, String name, GeoPoint location, String address, String email, String phoneNumber, List<String> adminIds, List<String> eventIds) {
        if(InputValidator.checkCompleteList(adminIds)){
            throw new IllegalArgumentException("Organization should have at least one admin created when created");
        }

        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.adminIds = adminIds;
        this.eventIds = eventIds;
    }



    public CompletableFuture<Boolean> saveToDB(DatabaseService db){
        if(uuid == null || InputValidator.checkCompleteList(adminIds)){
            return CompletableFuture.completedFuture(false);
        }

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

    public void setEmail(String email){
        if(InputValidator.verifyEmailInput(email)){
            this.email = email;
        }
    }

    public String getEmail() {
        return this.email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAdminIds(List<String> adminIds) {
        this.adminIds = new ArrayList<>(adminIds);
    }

    public void setEventIds(List<String> eventIds) {
        this.eventIds = new ArrayList<>(eventIds);
    }

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
