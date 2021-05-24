package com.ncnf.repositories;

import com.ncnf.database.firebase.DatabaseService;
import com.ncnf.models.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.Helpers.combine;
import static com.ncnf.utilities.StringCodes.AWAITING_REQUESTS_KEY;
import static com.ncnf.utilities.StringCodes.FULL_NAME_KEY;
import static com.ncnf.utilities.StringCodes.FRIENDS_KEY;
import static com.ncnf.utilities.StringCodes.PENDING_REQUESTS_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;


/**
    Interactions with the database to handle friends
    Collection: /users
    Fields:
    - friends (Array<String>): array of friends uuid
    - pending_requests (Array<String>): array of users uuid to which the user sent a request
    - awaiting_requests (Array<String>): array of users uuid requesting the user
 */
public class FriendsRepository {

    /*
        TODO: Some requests needs to be written with a transaction in case of failure.
     */
    private final DatabaseService db;
    private final String path = USERS_COLLECTION_KEY;

    public FriendsRepository() {
        this.db = new DatabaseService();
    }

    @Inject
    public FriendsRepository(DatabaseService db) {
        this.db = db;
    }

    /**
     * Friend request to another user (identified by its ID)
     * @param my_uuid User's identifier
     * @param other_uuid Friend's identifier
     * @return CompletableFuture containing the Firebase's response : true if the request was sent
     */
    public CompletableFuture<Boolean> request(String my_uuid, String other_uuid) {

        // Add a pending request to the user
        CompletableFuture<Boolean> u1 = db.updateArrayField(this.path + my_uuid, PENDING_REQUESTS_KEY, other_uuid);
        // Add a awaiting request to the other user
        CompletableFuture<Boolean> u2 = db.updateArrayField(USERS_COLLECTION_KEY + other_uuid, AWAITING_REQUESTS_KEY, my_uuid);
        return combine(u1, u2);
    }

    /**
     * Friend requests that are waiting for approval from the user
     * @param uuid User's identifier
     * @return CompletableFuture containing a list of users that sent me a friend request
     */
    public CompletableFuture<List<User>> awaitingRequests(String uuid) {
        // Fetch users that have a pending request to this user
        return requests(uuid, PENDING_REQUESTS_KEY);
    }
    /*
        Friend requests sent by the user and still pending
     */

    /**
     * Friend requests sent by the user and still pending
     * @param uuid User's identifier
     * @return CompletableFuture containing a list of users to who the user sent a request
     */
    public CompletableFuture<List<User>> pendingRequests(String uuid) {
        // Fetch users that an awaiting request from this user
        return requests(uuid, AWAITING_REQUESTS_KEY);
    }

    /**
     * Fetch a list of users which have the user's uuid in the given field
     * @param uuid User's identifier
     * @param field Key of the field we want to check
     * @return CompletableFuture containing a list of users
     */
    private CompletableFuture<List<User>> requests(String uuid, String field) {
        return this.db.whereArrayContains(USERS_COLLECTION_KEY, field, uuid, User.class);
    }

    /**
     * Update user's and friend's fields when the user accept or decline a friend request
     * @param accept Boolean describing if we accepted or declined the request
     * @param my_uuid User's identifier
     * @param other_uuid Identifier of the other user that send the current user a friend request
     * @return CompletableFuture containing Firebase's response : true if all operations were correctly executed
     */
    public CompletableFuture<Boolean> updateRequest(boolean accept, String my_uuid, String other_uuid) {
        // remove the awaiting request of the user
        CompletableFuture<Boolean> u1 = db.removeArrayField(this.path + my_uuid, AWAITING_REQUESTS_KEY, other_uuid);
        // remove the pending request of the other user
        CompletableFuture<Boolean> u2 = db.removeArrayField(USERS_COLLECTION_KEY + other_uuid, PENDING_REQUESTS_KEY, my_uuid);
        if (accept) {
            // add the new friend to both users
            CompletableFuture<Boolean> u3 = db.updateArrayField(this.path + my_uuid, FRIENDS_KEY, other_uuid);
            CompletableFuture<Boolean> u4 = db.updateArrayField(USERS_COLLECTION_KEY + other_uuid, FRIENDS_KEY, my_uuid);
            return combine(combine(combine(u1, u2), u3), u4);
        }

        return combine(u1, u2);
    }

    /*
        Get list of user friends
    */

    /**
     * Get the User's friends
     * @param uuid User's identifier
     * @return CompletableFuture containing a list of users corresponding the friends
     */
    public CompletableFuture<List<User>> getFriends(String uuid) {
        return db.whereArrayContains(USERS_COLLECTION_KEY, FRIENDS_KEY, uuid, User.class);
    }

    /**
     * Remove a user from a list of friend
     * @param my_uuid User's identifier
     * @param other_uuid Friend's identifier we look like to remove
     * @return CompletableFuture containing Firebase's response : true if all operations were correctly executed
     */
    public CompletableFuture<Boolean> removeFriend(String my_uuid, String other_uuid) {
        CompletableFuture<Boolean> u1 = db.removeArrayField(this.path + my_uuid, FRIENDS_KEY, other_uuid);
        CompletableFuture<Boolean> u2 = db.removeArrayField(USERS_COLLECTION_KEY + other_uuid, FRIENDS_KEY, my_uuid);
        return combine(u1, u2);
    }

    /**
     * Get list of users based on username
     * @param username Username we want to match
     * @return CompletableFuture containing a list of users that fulfill the condition
     */
    public CompletableFuture<List<User>> searchFriends(String username){
        return db.withFieldContaining(USERS_COLLECTION_KEY, FULL_NAME_KEY, username, User.class); // TODO : change FULL_NAME_KEY to USERNAME_KEY
    }
}
