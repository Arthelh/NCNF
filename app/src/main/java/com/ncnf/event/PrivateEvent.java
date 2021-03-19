package com.ncnf.event;

import com.ncnf.organizer.Organizer;
import com.ncnf.organizer.PublicOrganizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class PrivateEvent extends Event {

    private List<String> invited;

    public PrivateEvent(String name, Date date, Location location, String description, EventType type, Organizer owner) {
        super(name, date, location, type, PubPriv.PRIVATE, description, owner);
        invited = new ArrayList<>();
    }

    public void invite(String user) {
        invited.add(user);
    }

    public List<String> getInvited() {
        return invited;
    }

}
