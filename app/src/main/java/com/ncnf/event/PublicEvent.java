package com.ncnf.event;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.utilities.*;

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

        checkConstraints(minAge, price);

        tags = new ArrayList<>();
        this.minAge = minAge;
        this.price = price;
    }

    public PublicEvent(String ownerId, UUID uuid, String name, Date date, GeoPoint location, String address, String description, Type type, List<String> attendees, int minAge, int price, List<Tag> tags) {
        super(ownerId, uuid, name, date, location, address, type, Visibility.PUBLIC, attendees, description);

        checkConstraints(minAge, price);

        setTags(tags);
        this.minAge = minAge;
        this.price = price;
    }

    private void checkConstraints(int minAge, int price){
        if(!(minAge >= MIN_AGE && minAge <= MAX_AGE) || price < 0) {
            throw new IllegalArgumentException();
        }
    }

    public int getMinAge() { return minAge; }
    public int getPrice() { return price; }
    public List<Tag> getTags() { return new ArrayList<Tag>(tags); }

    public void setMinAge(int minAge) {
        checkConstraints(minAge, this.price);
        this.minAge = minAge;
    }

    public void setPrice(int price) { this.price = price; }

    public void setTags(List<Tag> tags) {
        this.tags = new ArrayList<Tag>(tags);
    }

    public void addTag(Tag newTag) {

        for(int i = 0; i < tags.size(); ++i) {
            if(tags.get(i).equals(newTag)) {
                return;
            }
        }
        tags.add(newTag);
    }

    public boolean filterTags(String s) {

        for(Tag tag : tags) {
            if (tag.getName().equals(s) || tag.getName().toLowerCase().contains(s)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public int compareTo(Object o) {
        PublicEvent otherEvent = (PublicEvent) o;
        return getDate().compareTo(otherEvent.getDate());
    }

    @Override
    public boolean equals(Object o) {
        PublicEvent p = (PublicEvent) o;
        return p.getUuid().equals(getUuid());
    }

    public CompletableFuture<DatabaseResponse> store(DatabaseService db){
        String[] fields = {MIN_AGE_KEY, PRICE_KEY, TAGS_LIST_KEY};
        Object[] objects = {this.minAge, this.price, this.tags};
        return super.store(db, fields, objects);
    }
}
