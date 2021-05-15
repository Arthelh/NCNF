package com.ncnf.socialObject;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.GROUPS_COLLECTION_KEY;

public class Group extends SocialObject {

    public DatabaseService databaseService;

    private final List<String> invited;

    public Group(String ownerId, String name, LocalDateTime date, GeoPoint location, String address, String description, Type type) {
        super(ownerId, name, date, location, address, type, description);
        invited = new ArrayList<>();
    }

    public Group(String ownerId, UUID id, String name, LocalDateTime date, GeoPoint location, String address, Type type, List<String> attendees, String description, List<String> invited) {
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
    public int compareTo(SocialObject o) {
        Group otherGroup = (Group) o;
        return getDate().compareTo(otherGroup.getDate());
    }

    public CompletableFuture<Boolean> store(DatabaseService db){
        return db.setDocument(GROUPS_COLLECTION_KEY + this.getUuid(), this);
    }

}
