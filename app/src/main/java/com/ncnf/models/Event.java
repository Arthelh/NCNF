package com.ncnf.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;

public class Event extends SocialObject {

    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 125;

    /**
     * Possible type for a social object
     */
    public enum Type {
        NOTHING, Movie, Museum, Conference, Opera, OTHER
    }

    private Type type;
    private double price;
    private int minAge;
    private String email;
    private List<String> attendees;

    /**
     * Public constructor used to create a new event
     *
     * @param ownerId     Identifier of the owner of this event
     * @param name        Name of the event
     * @param date        Date during which the event will take place
     * @param location    Location where the event will take place (GPS coordinates)
     * @param address     Address corresponding the location of the event
     * @param description Description of the event
     * @param type        Type of event : Concert, Movie, Museum, etc.
     * @param minAge      Minimum age to attend the event
     * @param price       Price to pay to attend the event
     * @param email       Contact email of the event
     */
    public Event(String ownerId, String name, LocalDateTime date, GeoPoint location, String address, String description, Type type, int minAge, double price, String email) {
        super(ownerId, name, date, location, address, description);

        checkConstraints(minAge, price);

        this.type = type;
        this.minAge = minAge;
        this.price = price;
        this.email = email;
        this.attendees = new ArrayList<>();
    }

    /**
     * Public constructor used to create an event from an already existing event
     *
     * @param ownerId     Identifier of the owner of this event
     * @param uuid        Unique identifier of the event
     * @param name        Name of the event
     * @param date        Date during which the event will take place
     * @param location    Location where the event will take place (GPS coordinates)
     * @param address     Address corresponding the location of the event
     * @param description Description of the event
     * @param type        Type of event : Concert, Movie, Museum, etc.
     * @param attendees   List of IDs of the people attending the event
     * @param minAge      Minimum age to attend the event
     * @param price       Price to pay to attend the event
     * @param email       Contact email of the event
     */
    public Event(String ownerId, UUID uuid, String name, LocalDateTime date, GeoPoint location, String address, String description, Type type, List<String> attendees, int minAge, double price, String email) {
        super(uuid, ownerId, name, date, location, address, description);

        checkConstraints(minAge, price);

        this.type = type;
        this.minAge = minAge;
        this.price = price;
        this.email = email;
        this.attendees = attendees;
    }

    /**
     * Check if the minimum age and price respect the constraint :
     * age and price can't be negative and age can't have a value to high as well
     */
    private void checkConstraints(int minAge, double price) {
        if (!(minAge >= MIN_AGE && minAge <= MAX_AGE) || price < 0) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Getters for the event's attributes
     */
    public int getMinAge() {
        return minAge;
    }

    public double getPrice() {
        return price;
    }

    public String getEmail() {
        return email;
    }

    public Type getType() {
        return type;
    }

    public List<String> getAttendees() {
        return Collections.unmodifiableList(attendees);
    }

    public int getNumOfAttendees() {
        return this.attendees.size();
    }

    /**
     * Setters for the event's attributes
     */
    public void setMinAge(int minAge) {
        checkConstraints(minAge, this.price);
        this.minAge = minAge;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = new ArrayList<>(attendees);
    }

    @Override
    public int compareTo(SocialObject o) {
        Event otherEvent = (Event) o;
        return getDate().compareTo(otherEvent.getDate());
    }

    @Override
    public boolean equals(Object o) {
        Event p = (Event) o;
        return p.getUuid().equals(getUuid());
    }

    /**
     * Store the event in the given database service
     *
     * @param db Database service used to store the event
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    public CompletableFuture<Boolean> store(@NonNull FirebaseDatabase db) {
        return db.setDocument(EVENTS_COLLECTION_KEY + this.getUuid(), this);
    }
}
