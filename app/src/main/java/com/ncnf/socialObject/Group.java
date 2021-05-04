package com.ncnf.socialObject;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import static com.ncnf.Utils.*;


public class Group extends SocialObject {

    private final List<String> invited;

    public Group(String ownerId, String name, Date date, GeoPoint location, String address, String description, Type type) {
        super(ownerId, name, date, location, address, type, description);
        invited = new ArrayList<>();
    }

    public Group(String ownerId, UUID id, String name, Date date, GeoPoint location, String address, Type type, List<String> attendees, String description, List<String> invited) {
        super(ownerId, id, name, date, location, address, type, attendees, description);
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
        Group p = (Group) o;
        return p.getUuid().equals(getUuid());
    }

    @Override
    public int compareTo(Object o) {
        Group otherEvent = (Group) o;
        return getDate().compareTo(otherEvent.getDate());
    }

    public CompletableFuture<Boolean> store(@NonNull DatabaseService db){
        return db.setDocument(GROUPS_COLLECTION_KEY + this.getUuid(), this);
    }

}
