package com.ncnf.user;

import com.ncnf.database.DatabaseService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.AWAITING_REQUESTS_KEY;
import static com.ncnf.Utils.FRIENDS_KEY;
import static com.ncnf.Utils.PENDING_REQUESTS_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;

/*
    Interactions with the database to handle friends

    collection: /users
    fields:
    - friends => Array<String> uuid: array of friends uuid
    - pending_requests => Array<String> uuid: array of users uuid to which the user sent a request
    - awaiting_requests => Array<String> uuid: array of users uuid requesting the user
 */
public class FriendsRepository {

    /*
        TODO: Some requests needs to be written with a transaction in case of failure.
     */

    private final DatabaseService db;
    private final String uuid;
    private final String path;

    public FriendsRepository(String uuid) {
        this.db = new DatabaseService();
        this.uuid = uuid;
        this.path = USERS_COLLECTION_KEY + uuid;
    }

    public FriendsRepository(DatabaseService db, String uuid) {
        this.db = db;
        this.uuid = uuid;
        this.path = USERS_COLLECTION_KEY + uuid;
    }

    /*
        Friend request to another user (identified by its ID)
     */
    public CompletableFuture<Boolean> request(String other_uuid) {
        // Add a pending request to the user
        CompletableFuture<Boolean> u1 = db.updateArrayField(this.path, PENDING_REQUESTS_KEY, other_uuid);
        // Add a awaiting request to the other user
        CompletableFuture<Boolean> u2 = db.updateArrayField(USERS_COLLECTION_KEY + other_uuid, AWAITING_REQUESTS_KEY, this.uuid);
        return combine(u1, u2);
    }

    /*
        Friend requests that are waiting for approval from the user
     */
    public CompletableFuture<List<User>> awaitingRequests() {
        // Fetch users that have a pending request to this user
        return requests(PENDING_REQUESTS_KEY);
    }
    /*
        Friend requests sent by the user and still pending
     */
    public CompletableFuture<List<User>> pendingRequests() {
        // Fetch users that an awaiting request from this user
        return requests(AWAITING_REQUESTS_KEY);
    }

    /*
        Fetch a list of users which have the user's uuid in the given field
     */
    private CompletableFuture<List<User>> requests(String field) {
        return this.db.whereArrayContains(USERS_COLLECTION_KEY, field, this.uuid, User.class);
    }

    /*
        Accept or decline a friend request
     */
    public CompletableFuture<Boolean> updateRequest(boolean accept, String other_uuid) {
        // remove the awaiting request of the user
        CompletableFuture<Boolean> u1 = db.removeArrayField(this.path, AWAITING_REQUESTS_KEY, other_uuid);
        // remove the pending request of the other user
        CompletableFuture<Boolean> u2 = db.removeArrayField(USERS_COLLECTION_KEY + other_uuid, PENDING_REQUESTS_KEY, this.uuid);
        if (accept) {
            // add the new friend to both users
            CompletableFuture<Boolean> u3 = db.updateArrayField(this.path, FRIENDS_KEY, other_uuid);
            CompletableFuture<Boolean> u4 = db.updateArrayField(USERS_COLLECTION_KEY + other_uuid, FRIENDS_KEY, uuid);
            return combine(combine(combine(u1, u2), u3), u4);
        }

        return combine(u1, u2);
    }

    /*
        Get list of user friends
    */
    public CompletableFuture<List<User>> getFriends() {
        return db.whereArrayContains(USERS_COLLECTION_KEY, FRIENDS_KEY, this.uuid, User.class);
    }

    private CompletableFuture<Boolean> combine(CompletableFuture<Boolean> u1, CompletableFuture<Boolean> u2) {
        return u1.thenCombine(u2, (v1, v2) -> v1 && v2)
                .exceptionally(exception -> false);
    }

}
