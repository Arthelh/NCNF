package com.ncnf.socialObject;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.NEWS_KEY;

public abstract class SocialObject implements Comparable<SocialObject> {

    public static final String IMAGE_PATH = "/events/images";
    public static final String IMAGE_NAME = "banner_%s";

    public enum Type {
        NOTHING, Movie, Museum, Conference, Opera, OTHER
    }


    private final String path;

    private UUID uuid;
    private String ownerId;
    private String name;
    private LocalDateTime date;
    private Type type;
    private List<String> attendees;
    private int numOfAttendees;
    private String description;
    private GeoPoint location;
    private String address;

    public SocialObject(String ownerId, String name, LocalDateTime date, GeoPoint location, String address, Type type, String description) {
        this(ownerId, UUID.randomUUID(), name, date, location, address, type, new ArrayList<>(), description);
    }

    public SocialObject(String ownerId, UUID id, String name, LocalDateTime date, GeoPoint location, String address, Type type, List<String> attendees, String description) {
        this.uuid = id;
        this.path = EVENTS_COLLECTION_KEY + uuid;
        this.ownerId = ownerId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.address = address;
        this.type = type;
        this.attendees = attendees;
        this.numOfAttendees = attendees.size();
        this.description = description;
    }

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

    public Type getType() {
        return type;
    }

    public List<String> getAttendees() {
        return Collections.unmodifiableList(attendees);
    }

    public int getNumOfAttendees() {
        return numOfAttendees;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setType(Type type) {
        this.type = type;
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

    public void setAttendees(List<String> attendees) {
        this.attendees = new ArrayList<>(attendees);
        numOfAttendees = attendees.size();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    abstract public CompletableFuture<Boolean> store(DatabaseService db);

    //TODO WTF is this doing here? Why is there a static method, that takes a DBS, in an abstract class, where it only is used for events??????
    public static CompletableFuture<Boolean> addNews(DatabaseService db, String uuid, String value) {
        return db.updateArrayField(EVENTS_COLLECTION_KEY + uuid, NEWS_KEY, value);
    }
}