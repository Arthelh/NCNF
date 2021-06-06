package com.ncnf.models;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class SocialObject implements Comparable<SocialObject> {

    public static final String IMAGE_PATH = "/events/images";
    public static final String IMAGE_NAME = "banner_%s";

    private UUID uuid;
    private String ownerId;
    private String name;
    private LocalDateTime date;
    private String description;
    private GeoPoint location;
    private String address;

    /**
     * Public constructor used to create a new socialObject
     * @param ownerId Identifier of the owner of this socialObject
     * @param name Name of the socialObject
     * @param date Date during which the socialObject will take place
     * @param location Location where the socialObject will take place (GPS coordinates)
     * @param address Address corresponding the location of the socialObject
     * @param description Description of the socialObject
     */
    public SocialObject(String ownerId, String name, LocalDateTime date, GeoPoint location, String address, String description) {
        this(UUID.randomUUID(), ownerId, name, date, location, address, description);
    }

    /**
     * Public constructor used to create an event from an already existing socialObject
     * @param uuid Unique identifier of the socialObject
     * @param ownerId Identifier of the owner of this socialObject
     * @param name Name of the socialObject
     * @param date Date during which the socialObject will take place
     * @param location Location where the socialObject will take place (GPS coordinates)
     * @param address Address corresponding the location of the socialObject
     * @param description Description of the socialObject
     */
    public SocialObject(UUID uuid, String ownerId, String name, LocalDateTime date, GeoPoint location, String address, String description) {
        this.uuid = uuid;
        this.ownerId = ownerId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.address = address;
        this.description = description;
    }

    /**
     * Getters for the attributes
     */
    public UUID getUuid() {
        return uuid;
    }
    public String getName() {
        return name;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public GeoPoint getLocation() {
        return location;
    }
    public java.lang.String getAddress() {
        return address;
    }
    public String getDescription() {
        return description;
    }
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Setters for the attributes
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public void setLocation(GeoPoint location) {
        this.location = location;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Store the object in the given database service
     * @param db Database service used to store the event
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    abstract public CompletableFuture<Boolean> store(FirebaseDatabase db);
}