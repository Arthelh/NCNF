package com.ncnf.event;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import static com.ncnf.utilities.StringCodes.*;


public class PrivateEvent extends Event {

    private final List<String> invited;

    public PrivateEvent(String ownerId, String name, Date date, GeoPoint location, String address, String description, Type type) {
        super(ownerId, name, date, location, address, type, Event.Visibility.PRIVATE, description);
        invited = new ArrayList<>();
    }

    public PrivateEvent(String ownerId, UUID id, String name, Date date, GeoPoint location, String address, Type type, List<String> attendees, String description, List<String> invited) {
        super(ownerId, id, name, date, location, address, type, Event.Visibility.PRIVATE,attendees, description);
        this.invited = invited;
    }

    public void invite(String user) {
        invited.add(user);
    }

    public List<String> getInvited() {
        return invited;
    }

    @Override
    public boolean equals(Object o) {
        PrivateEvent p = (PrivateEvent) o;
        return p.getUuid().equals(getUuid());
    }

    @Override
    public int compareTo(Object o) {
        PrivateEvent otherEvent = (PrivateEvent) o;
        return getDate().compareTo(otherEvent.getDate());
    }
    public CompletableFuture<DatabaseResponse> store(DatabaseService db){
        String[] fields = {INVITED_KEY};
        Object[] objects = {this.invited};
        return super.store(db, fields, objects);
    }

}
