package com.ncnf.event;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.organizer.PublicOrganizer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import static com.ncnf.Utils.*;

public class PublicEvent extends Event {

    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 125;

    private List<Tag> tags;

    private int price;
    private int minAge;

    public PublicEvent(String ownerId, String name, Date date, GeoPoint location, String address, String description, Type type, int minAge, int price) {
        super(ownerId, name, date, location, address, type, Event.Visibility.PUBLIC, description);

        if(!(minAge >= MIN_AGE && minAge <= MAX_AGE) || price < 0) {
            throw new IllegalArgumentException();
        }

        tags = new ArrayList<>();
        this.minAge = minAge;
        this.price = price;
    }

    public PublicEvent(String ownerId, UUID uuid, String name, Date date, GeoPoint location, String address, String description, Event.Type type, List<String> attendees, int minAge, int price, List<Tag> tags) {
        super(ownerId, uuid, name, date, location, address, type, Visibility.PUBLIC, attendees, description);

        if(!(minAge >= MIN_AGE && minAge <= MAX_AGE) || price <= 0) {
            throw new IllegalArgumentException();
        }

        this.tags = tags;
        this.minAge = minAge;
        this.price = price;
    }



    public int getMinAge() { return minAge; }
    public int getPrice() { return price; }
    public List<Tag> getTags() { return tags; }

    public void setMinAge(int minAge) {
        if(!(minAge >= MIN_AGE && minAge <= MAX_AGE)) {
            throw new IllegalArgumentException();
        }
        this.minAge = minAge;
    }

    public void setPrice(int price) { this.price = price; }

    public void setTags(List<Tag> tags) {
        this.tags = new ArrayList<>();
        for(int i = 0; i < tags.size(); ++i) {
            this.tags.add(tags.get(i));
        }
    }

    public void addTag(Tag newTag) {

        for(int i = 0; i < tags.size(); ++i) {
            if(tags.get(i).equals(newTag)) {
                throw new IllegalArgumentException();
            }
        }
        tags.add(newTag);
    }

    public CompletableFuture<CompletableFuture<DatabaseResponse>> store(){
        String[] fields = {MIN_AGE_KEY, PRICE_KEY, TAGS_LIST_KEY};
        Object[] objects = {this.minAge, this.price, this.tags};
        return super.store(fields, objects);
    }
}
