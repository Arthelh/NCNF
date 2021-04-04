package com.ncnf.event;

import android.content.Context;
import android.widget.ImageView;

import com.ncnf.organizer.Organizer;
import com.ncnf.utilities.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

public abstract class Event implements Comparable {

    public enum PubPriv {
        PUBLIC, PRIVATE;
    }

    private UUID uuid;

    String imageName;

    private String name;
    private Date date;
    private Location location;
    private EventType type;
    private PubPriv pubPriv;
    private List<String> attendees;
    private int numOfAttendees;
    private String description;
    private Organizer organizer;

    public Event(String name, Date date, Location location, EventType type, PubPriv pubPriv, String description, Organizer organizer, String imageName) {
        this.imageName = imageName;
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
    public String getImageName() { return imageName; }
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
        this.attendees = new ArrayList<String>();
        for(int i = 0; i < attendees.size(); ++i) {
            this.attendees.add(attendees.get(i));
        }
        numOfAttendees = attendees.size();
    }
    public void setUuid(UUID uuid) {this.uuid = uuid; }
    public void setDescription(String description)  { this.description = description; }

    public void setNewOrganizer(Organizer newOwner) { this.organizer = newOwner; }

    public static Event toEvent(String f) {

        String[] s = f.split("@");

        if(s[0].equals("PUBLIC")) {
            return PublicEvent.toEvent(s);
        }
        else if(s[0].equals("PRIVATE")) {
            return PrivateEvent.toEvent(s);
        }

        return null;
    }

    /**
     public void cache(Context context) throws IOException {

     File newFile = File.createTempFile(uuid.toString(), null, context.getCacheDir());
     FileOutputStream fos = new FileOutputStream(newFile);
     fos.write(toString().getBytes());
     fos.close();
     }
     **/


}
