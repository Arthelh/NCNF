package com.ncnf.event;

import com.ncnf.organizer.Organizer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class Event {

    public enum PubPriv {
        PUBLIC, PRIVATE
    }

    private final UUID uuid;

    private String name;
    private Date date;
    private Location location;
    private final EventType type;
    private final PubPriv pubPriv;
    private List<String> attendees;
    private int numOfAttendees;
    private String description;
    private Organizer organizer;

    public Event(String name, Date date, Location location, EventType type, PubPriv pubPriv, String description, Organizer organizer) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.type = type;
        this.pubPriv = pubPriv;
        attendees = new ArrayList<String>();
        numOfAttendees = 0;
        this.description = description;
        this.uuid = UUID.randomUUID();
        this.organizer = organizer;
    }

    public String getName() { return name; }
    public Date getDate() { return date; }
    public Location getLocation() { return location; }
    public EventType getType() { return type; }
    public PubPriv getPubPriv() { return pubPriv; }
    public List<String> getAttendees() { return attendees; }
    public int getNumOfAttendees() { return numOfAttendees; }
    public String getDescription() { return description; }
    public UUID getUuid() { return uuid; }
    public Organizer getOrganizer() { return organizer; }

    public void setName(String name) { this.name = name; }
    public void setDate(Date date) { this.date = date; }
    public void setLocation(Location location) { this.location = location; }
    public void setAttendees(List<String> attendees) {
        this.attendees = new ArrayList<>(attendees);
        numOfAttendees = attendees.size();
    }
    public void setDescription(String description)  { this.description = description; }

    public void setNewOrganizer(Organizer newOwner) { this.organizer = newOwner; }

}
