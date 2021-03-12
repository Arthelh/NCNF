package com.example.bootcamp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Event {

    public enum Type {
        PUBLIC, PRIVATE;
    }

    private UUID uuid;

    private String name;
    private Date date;
    private Location location;
    private Type type;
    private List<String> attendees;
    private int numOfAttendees;
    private String description;
    private String owner;

    public Event(String name, Date date, Location location, Type type, String description, String owner) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.type = type;
        attendees = new ArrayList<String>();
        numOfAttendees = 0;
        this.description = description;
        this.uuid = UUID.randomUUID();
        this.owner = owner;
    }

    public String getName() { return name; }
    public Date getDate() { return date; }
    public Location getLocation() { return location; }
    public Type getType() { return type; }
    public List<String> getAttendees() { return attendees; }
    public int getNumOfAttendees() { return numOfAttendees; }
    public String getDescription() { return description; }
    public UUID getUuid() { return uuid; }
    public String getOwner() { return owner; }

    public void setName(String name) { this.name = name; }
    public void setDate(Date date) { this.date = date; }
    public void setLocation(Location location) { this.location = location; }
    public void setAttendees(List<String> attendees) {
        this.attendees = new ArrayList<String>();
        for(int i = 0; i < attendees.size(); ++i) {
            this.attendees.add(attendees.get(i));
        }
        numOfAttendees = attendees.size();
    }
    public void setDescription(String description)  { this.description = description; }

    public void setNewOwner(String newOwner) { this.owner = newOwner; }

}
