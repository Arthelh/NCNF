package com.ncnf.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;

public class Event extends SocialObject {

    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 125;

    private List<EventTag> eventTags;
    private double price;
    private int minAge;
    private String email;

    public Event(String ownerId, String name, LocalDateTime date, GeoPoint location, String address, String description, Type type, int minAge, double price, String email) {
        super(ownerId, name, date, location, address, type, description);

        checkConstraints(minAge, price);

        eventTags = new ArrayList<>();
        this.minAge = minAge;
        this.price = price;
        this.email = email;
    }

    public Event(String ownerId, UUID uuid, String name, LocalDateTime date, GeoPoint location, String address, String description, Type type, List<String> attendees, int minAge, double price, List<EventTag> eventTags, String email) {
        super(ownerId, uuid, name, date, location, address, type, attendees, description);

        checkConstraints(minAge, price);

        setEventTags(eventTags);
        this.minAge = minAge;
        this.price = price;
        this.email = email;
    }

    private void checkConstraints(int minAge, double price){
        if(!(minAge >= MIN_AGE && minAge <= MAX_AGE) || price < 0) {
            throw new IllegalArgumentException();
        }
    }

    public int getMinAge() { return minAge; }
    public double getPrice() { return price; }
    public String getEmail() { return email; }
    public List<EventTag> getEventTags() { return new ArrayList<EventTag>(eventTags); }

    public void setMinAge(int minAge) {
        checkConstraints(minAge, this.price);
        this.minAge = minAge;
    }

    public void setPrice(double price) { this.price = price; }

    public void setEmail(String email) { this.email = email; }

    public void setEventTags(List<EventTag> eventTags) {
        this.eventTags = new ArrayList<EventTag>(eventTags);
    }

    public void addTag(EventTag newEventTag) {

        for(int i = 0; i < eventTags.size(); ++i) {
            if(eventTags.get(i).equals(newEventTag)) {
                return;
            }
        }
        eventTags.add(newEventTag);
    }

    public boolean filterTags(String s) {
        for(EventTag eventTag : eventTags) {
            if (eventTag.getName().equals(s) || eventTag.getName().toLowerCase().contains(s)) {
                return true;
            }
        }
        return false;
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

    public CompletableFuture<Boolean> store(@NonNull FirebaseDatabase db){
        return db.setDocument(EVENTS_COLLECTION_KEY + this.getUuid(), this);
    }
}
