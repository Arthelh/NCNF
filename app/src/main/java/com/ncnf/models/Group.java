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

    private final List<String> members;

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
        this.members = new ArrayList<>();
        this.members.add(0, ownerId);
    }

    /**
     * Public constructor used to create an group from an already existing group
     * @param ownerId Identifier of the owner of this group
     * @param id Unique identifier of the group
     * @param name Name of the group
     * @param date Date during which the group will take place
     * @param location Meeting point of the group (GPS coordinates)
     * @param address Address corresponding the location of the group
     * @param description Description of the group
     * @param members List of IDs of the people
     */
    public Group(String ownerId, UUID id, String name, LocalDateTime date, GeoPoint location, String address, String description, List<String> members) {
        super(id, ownerId, name, date, location, address, description);
        this.members = members == null ? new ArrayList<>() : members;
        if(!this.members.contains(ownerId)){
            this.members.add(0, ownerId);
        }
    }

    /**
     * Add a user as member of the group
     * @param userId ID of the user to add
     */
    public void addMember(String userId) {
        if(userId != null && !members.contains(userId)){
            members.add(userId);
        }
    }

    /**
     * Remove member of the group
     * @param userId ID of the user to remove
     */
    public void removeMember(String userId){
        if(userId != null){
            members.remove(userId);
        }
    }

    /**
     * Getter for the attribute
     */
    public List<String> getMembers() {
        return members;
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