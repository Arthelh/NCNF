package com.ncnf.event;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.*;

public abstract class Event implements Comparable {

    public enum Visibility {
        PUBLIC, PRIVATE
    }

    public enum Type {
        NOTHING, Movie, Museum, Conference, Opera, OTHER
    }


    private final String path;

    private UUID uuid;
    private String ownerId;
    private Visibility visibility;
    private String name;
    private Date date;
    private Type type;
    private List<String> attendees;
    private int numOfAttendees;
    private String description;
    private GeoPoint location;
    private String address;

    public Event(String ownerId, String name, Date date, GeoPoint location, String address, Type type, Visibility visibility, String description) {
        this(ownerId, UUID.randomUUID(), name, date, location, address, type, visibility, new ArrayList<>(), description);
    }

    public Event(String ownerId, UUID id, String name, Date date, GeoPoint location, String address, Type type, Visibility visibility, List<String> attendees, String description) {
        this.uuid = id;
        this.path = EVENTs_COLLECTION_KEY + uuid;
        this.ownerId = ownerId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.address = address;
        this.type = type;
        this.visibility = visibility;
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

    public Date getDate() {
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

    public Visibility getVisibility() {
        return visibility;
    }

    public List<String> getAttendees() {
        return attendees;
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

    public void setDate(Date date) {
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

    protected CompletableFuture<DatabaseResponse> store(DatabaseService db, String[] fields, Object[] objects) {
        Map<String, Object> map = new HashMap<>();
        map.put(UUID_KEY, this.uuid.toString());
        map.put(NAME_KEY, this.name);
        map.put(DATE_KEY, new Timestamp(this.date));
        map.put(LOCATION_KEY, this.location);
        map.put(ADDRESS_KEY, this.address);
        map.put(VISIBILITY_KEY, this.visibility.toString());
        map.put(TYPE_KEY, this.type.toString());
        map.put(ATTENDEES_KEY, this.attendees);
        map.put(DESCRIPTION_KEY, this.description);
        map.put(OWNER_KEY, this.ownerId);
        for (int i = 0; i < fields.length; ++i) {
            map.put(fields[i], objects[i]);
        }
        return db.setDocument(path, map);
    }

    public static CompletableFuture<DatabaseResponse> addNews(DatabaseService db, String uuid, String value) {
       return db.updateArrayField(EVENTs_COLLECTION_KEY + uuid, NEWS_KEY, value);
    }
}