package com.ncnf.repositories;

import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Group;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.GROUPS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;

public class GroupRepository {
    private final FirebaseDatabase db;

    public GroupRepository() {
        this.db = new FirebaseDatabase();
    }

    @Inject
    public GroupRepository(FirebaseDatabase db)  {
        this.db = db;
    }

    /**
     * Loads the Group from Database
     * @param uuid the unique identifier of the Group
     * @return A CompletableFuture wrapping the loaded Group
     */
    public CompletableFuture<Group> loadGroup(String uuid){
        return this.db.getDocument(GROUPS_COLLECTION_KEY + uuid, Group.class);
    }

    /**
     * Stores the Group to Database
     * @param group the Group object to store
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> storeGroup(Group group){
        return this.db.setDocument(GROUPS_COLLECTION_KEY + group.getUuid(), group);
    }

    /**
     * Loads multiple Group objects from Database
     * @param uuidList the list of Group unique identifiers to load
     * @return A CompletableFuture wrapping a list containing the loaded Group objects
     */
    public CompletableFuture<List<Group>> loadMultipleGroups(List<String> uuidList){
        return this.db.whereIn(GROUPS_COLLECTION_KEY, UUID_KEY, uuidList, Group.class);
    }

    /**
     * Deletes the Group from the Database
     * @param group the Group object to delete
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> deleteGroup(Group group){
        return this.db.deleteDocument(GROUPS_COLLECTION_KEY + group.getUuid());
    }

}
