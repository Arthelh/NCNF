package com.ncnf.event;

import com.ncnf.organizer.PublicOrganizer;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;

public class PublicEvent extends Event {

    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 125;

    private List<Tag> tags;

    private int price;
    private int minAge;

    public PublicEvent(String name, Date date, Location location, String description, EventType type, int minAge, int price, PublicOrganizer owner, String image) {
        super(name, date, location, type, PubPriv.PUBLIC, description, owner, image);

        if(!(minAge >= MIN_AGE && minAge <= MAX_AGE)) {
            throw new IllegalArgumentException();
        }

        tags = new ArrayList<>();
        this.minAge = minAge;
        this.price = price;
    }

    public int getMinAge() { return minAge; }
    public int getPrice() { return price; }
    public List<Tag> getTags() { return new ArrayList<Tag>(tags); }

    public void setMinAge(int minAge) {
        if(!(minAge >= MIN_AGE && minAge <= MAX_AGE)) {
            throw new IllegalArgumentException();
        }
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
    public String toString() {
        String separator = "@";

        StringBuilder builder = new StringBuilder();
        builder.append(getPubPriv().toString() + separator + getUuid().toString() + separator + getName() + separator);

        DateAdapter adapter = new DateAdapter(getDate());
        builder.append(adapter.toString() + separator);

        builder.append(getLocation().getLongitude() + " " + getLocation().getLatitude() + " " + getLocation().getAddress() + separator);
        builder.append(getDescription() + separator);
        builder.append(getType().toString() + separator + getMinAge() + separator + getPrice() + separator + getOrganizer().getName() + separator + getImageName());

        return builder.toString();
    }

    public static PublicEvent toEvent(String[] arr) {

        String name = arr[2];
        Date date = DateAdapter.toDate(arr[3]);

        String[] loc = arr[4].split(" ");
        Location newLoc = new Location(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), loc[2]);
        String descr = arr[5];
        EventType e = EventType.valueOf(arr[6]);
        int age = Integer.parseInt(arr[7]);
        int price = Integer.parseInt(arr[8]);
        PublicOrganizer org = new PublicOrganizer(arr[8]);
        String str = arr[10];

        PublicEvent event = new PublicEvent(name, date, newLoc, descr, e, age, price, org, str);
        event.setUuid(UUID.fromString(arr[1]));
        return event;
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

}
