package com.ncnf.event;

import android.content.Context;
import android.widget.ImageView;

import com.ncnf.organizer.Organizer;
import com.ncnf.utilities.Location;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.user.PrivateUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.*;

public abstract class Event implements Comparable {

    public enum PubPriv {
        PUBLIC, PRIVATE;
    }

    private UUID uuid;

    String imageName;

    private String name;
    private Date date;
    private EventType type;
    private PubPriv pubPriv;

    public enum Type {
        Movie, Museum, Conference, Opera, NOTHING
    }

    private String ownerId;

    private List<String> attendees;
    private int numOfAttendees;
    private String description;
    private GeoPoint location;
    private String address;

    public Event(String ownerId, String name, Date date, GeoPoint location, String address, EventType type, PubPriv visibility, String description) {
        this.uuid = UUID.randomUUID();
        this.ownerId = ownerId;
        this.imageName = imageName;
        this.name = name;
        this.date = date;
        this.location = location;
        this.address = address;
        this.type = type;
        this.pubPriv = pubPriv;
        this.attendees = new ArrayList<>();
        this.numOfAttendees = 0;
        this.description = description;

    }

    public Event(String ownerId, UUID id, String name, Date date, GeoPoint location, String address, EventType type, PubPriv pubPriv, List<String> attendees, String description){

        this.uuid = id;
        this.ownerId = ownerId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.address = address;
        this.type = type;
        this.pubPriv = pubPriv;
        this.attendees = attendees;
        this.numOfAttendees = attendees.size();
        this.description = description;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getImageName() { return imageName; }
    public Date getDate() { return date; }
    public GeoPoint getLocation() { return location; }
    public java.lang.String getAddress() {
        return address;
    }
    public EventType getType() { return type; }
    public PubPriv getPubPriv() { return pubPriv; }
    public List<String> getAttendees() { return attendees; }
    public int getNumOfAttendees() { return numOfAttendees; }
    public String getDescription() { return description; }
    public String getOwnerId() { return ownerId; }

    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }
    public void setType(EventType type){
        this.type = type;
    }
    public void setName(String name) { this.name = name; }
    public void setDate(Date date) { this.date = date; }
    public void setLocation(GeoPoint location) { this.location = location; }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setAttendees(List<String> attendees) {
        this.attendees = new ArrayList<String>();
        for(int i = 0; i < attendees.size(); ++i) {
            this.attendees.add(attendees.get(i));
        }
        numOfAttendees = attendees.size();
    }
    public void setUuid(UUID uuid) {this.uuid = uuid; }
    public void setDescription(String description)  { this.description = description; }

    public void setNewOrganizer(String newOwner) { this.ownerId = newOwner; }

    protected CompletableFuture<DatabaseResponse> store(DatabaseService db, String[] fields, Object[] objects){
        Map<String, Object> map = new HashMap<>();
        map.put(UUID_KEY, this.uuid.toString());
        map.put(NAME_KEY, this.name);
        map.put(DATE_KEY, new Timestamp(this.date));
        map.put(LOCATION_KEY, this.location);
        map.put(ADDRESS_KEY, this.address);
        map.put(VISIBILITY_KEY, this.pubPriv.toString());
        map.put(TYPE_KEY, this.type.toString());
        map.put(ATTENDEES_KEY, this.attendees);
        map.put(DESCRIPTION_KEY, this.description);
        map.put(OWNER_KEY, this.ownerId);
        for(int i = 0; i < fields.length; ++i){
            map.put(fields[i], objects[i]);
        }
        return db.setDocument(EVENTs_COLLECTION_KEY + uuid, map);
    }
}
