package com.example.bootcamp;

import java.util.ArrayList;
import java.util.List;

public class PublicEvent extends Event {

    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 125;

    private List<Tag> tags;

    private int price;
    private int minAge;

    public PublicEvent(String name, Date date, Location location, String description, int minAge, int price, String owner) {
        super(name, date, location, Type.PUBLIC, description, owner);

        if(!(minAge >= MIN_AGE && minAge <= MAX_AGE)) {
            throw new IllegalArgumentException();
        }

        tags = new ArrayList<>();
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
        this.tags = new ArrayList<Tag>();
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


}
