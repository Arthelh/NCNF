package com.example.bootcamp;

import java.util.ArrayList;
import java.util.List;

public class PrivateEvent extends Event {

    private List<String> invited;

    public PrivateEvent(String name, Date date, Location location, String description, String owner) {
        super(name, date, location, Type.PRIVATE, description, owner);
        invited = new ArrayList<>();
    }

    public void invite(String user) {
        invited.add(user);
    }

    public List<String> getInvited() {
        return invited;
    }

}
