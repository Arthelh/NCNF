package com.ncnf.models;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.utilities.StringCodes.GROUPS_COLLECTION_KEY;

public class Group extends SocialObject {

    private final List<String> invited;

    /**
     * Public constructor used to create a new group
     * @param ownerId Identifier of the owner of this group
     * @param name Name of the group
     * @param date Date during which the group will take place
     * @param location Meeting point of the group (GPS coordinates)
     * @param address Address corresponding the location of the group
     * @param description Description of the group
     */
    public Group(String ownerId, String name, LocalDateTime date, GeoPoint location, String address, String description) {
        super(ownerId, name, date, location, address, description);
        invited = new ArrayList<>();
    }

    /**
     * Public constructor used to create an group from an already existing group
     * @param ownerId Identifier of the owner of this group
     * @param id Unique identifier of the group
     * @param name Name of the group
     * @param date Date during which the group will take place
     * @param location Meeting point of the group (GPS coordinates)
     * @param address Address corresponding the location of the group
     * @param attendees List of IDs of the people attending the event
     * @param description Description of the group
     * @param invited List of IDs of the people
     */
    public Group(String ownerId, UUID id, String name, LocalDateTime date, GeoPoint location, String address, List<String> attendees, String description, List<String> invited) {
        super(id, ownerId, name, date, location, address, attendees, description);
        if(invited == null) {
            this.invited = new ArrayList<>();
        }
        else {
            this.invited = invited;
        }
    }

    /**
     * Add a user to the invited list
     * @param userId ID of the user to add
     */
    public void invite(String userId) {
        invited.add(userId);
    }

    /**
     * Getter for the attribute
     */
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

    /**
     * Store the group in the given database service
     * @param db Database service used to store the event
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    public CompletableFuture<Boolean> store(FirebaseDatabase db){
        return db.setDocument(GROUPS_COLLECTION_KEY + this.getUuid(), this);
    }
}