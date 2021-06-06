package com.ncnf.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.utilities.InputValidator;

import static com.ncnf.utilities.InputValidator.checkCompleteList;
import static com.ncnf.utilities.InputValidator.isInvalidString;
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

    /**
     * Public constructor to create a new organization
     * @param name Name of the organization
     * @param location Location of the organization
     * @param address Address of the location
     * @param email Contact of the organization
     * @param phoneNumber Phone Number of the organization
     * @param originalOwner Owner/first administrator of the organization
     */
    public Organization(String name, GeoPoint location, String address, String email, String phoneNumber, String originalOwner) {
        this(UUID.randomUUID(), name, location, address, email, phoneNumber, new ArrayList<String>() {{ add(originalOwner); }}, new ArrayList<>());
    }

    /**
     * Public constructor used to create an organization from an already existing organization
     * @param uuid Unique identifier of the organization
     * @param name Name of the organization
     * @param location Location of the organization
     * @param address Address of the location
     * @param email Contact of the organization
     * @param phoneNumber Phone Number of the organization
     * @param adminIds List of the organization administrators' identifiers
     * @param eventIds List of the organization events' identifiers
     */
    public Organization(UUID uuid, String name, GeoPoint location, String address, String email, String phoneNumber, List<String> adminIds, List<String> eventIds) {
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.adminIds = adminIds;
        this.eventIds = eventIds;
    }


    /**
     * Store the organization on FirebaseFirestore
     * @param db Database service that will be use to store the organization
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    public CompletableFuture<Boolean> saveToDB(FirebaseDatabase db){
        if(uuid == null || checkCompleteList(adminIds)){
            return CompletableFuture.completedFuture(false);
        }

        return db.setDocument(ORGANIZATIONS_COLLECTION_KEY + uuid.toString(), this);
    }

    /**
     * Getters for attributes
     */
    public UUID getUuid() {
        return uuid;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return this.email;
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

    /**
     * Setters for attributes
     */
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
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setAdminIds(List<String> adminIds) {
        this.adminIds = new ArrayList<>(adminIds);
    }
    public void setEventIds(List<String> eventIds) {
        this.eventIds = new ArrayList<>(eventIds);
    }

    /**
     * Add a new administrator to the organization's administrator list
     * @param adminId Identifier of the administrator to add
     * @return True if added, false otherwise
     */
    public boolean addAdmin(String adminId){
        if(isInvalidString(adminId)){
            return false;
        }
        return this.adminIds.add(adminId);
    }

    /**
     * Remove an administrator from the organization's administrator list
     * @param adminId Identifier of the administrator to remove
     * @return True if removed, false otherwise
     */
    public boolean deleteAdmin(String adminId){
        if(this.adminIds.size() == 1 && this.adminIds.contains(adminId)){
            throw new IllegalStateException("Organization should have at least one admin");
        }
        if(isInvalidString(adminId)){
            return false;
        }
        return this.adminIds.remove(adminId);
    }

    /**
     * Add an event from the organization's event list
     * @param eventId Identifier of the event to add
     * @return True if added, false otherwise
     */
    public boolean addEvent(String eventId){
        if(isInvalidString(eventId)){
            return false;
        }
        return this.eventIds.add(eventId);
    }

    /**
     * Remove an event from the organization's event list
     * @param eventId Identifier of the event to remove
     * @return True if removed, false otherwise
     */
    public boolean removeEvent(String eventId){
        if(isInvalidString(eventId)){
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