package com.ncnf.repositories;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.FULL_NAME_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.utilities.StringCodes.OWNED_GROUPS_KEY;
import static com.ncnf.utilities.StringCodes.PARTICIPATING_GROUPS_KEY;
import static com.ncnf.utilities.StringCodes.SAVED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.USERNAME_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.USER_LOCATION_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;


public class UserRepository {

    public FirebaseDatabase db;

    public UserRepository() {
        this.db = new FirebaseDatabase();
    }

    @Inject
    public UserRepository(FirebaseDatabase db) {
        this.db = db;
    }

    /**
     * Loads the User from Database
     * @param uuid the unique identifier of the User
     * @return A CompletableFuture wrapping the loaded User
     */
    public CompletableFuture<User> loadUser(String uuid){
        return this.db.getDocument(USERS_COLLECTION_KEY + uuid, User.class);
    }

    /**
     * Stores the User to Database
     * @param user the User object to store
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> storeUser(User user){
        return this.db.setDocument(USERS_COLLECTION_KEY + user.getUuid(), user);
    }

    /**
     * Loads multiple User objects from Database
     * @param uuidList the list of User unique identifiers to load
     * @return A CompletableFuture wrapping a list containing the loaded User objects
     */
    public CompletableFuture<List<User>> loadMultipleUsers(List<String> uuidList){
        return this.db.whereIn(USERS_COLLECTION_KEY, UUID_KEY, uuidList, User.class);
    }

    /**
     * Updates the User's notification field
     * @param uuid the unique identifier of the User
     * @param isEnabled the new value for notifications
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> updateNotifications(String uuid, boolean isEnabled) {
        return this.db.updateField(USERS_COLLECTION_KEY + uuid, NOTIFICATIONS_KEY, isEnabled);
    }

    /**
     * Updates the User's notification token
     * @param uuid the unique identifier of the User
     * @param token the new token
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> updateNotificationsToken(String uuid, String token) {
        return this.db.updateField(USERS_COLLECTION_KEY + uuid, NOTIFICATIONS_TOKEN_KEY, token);
    }

    /**
     * Updates the User's location field
     * @param uuid the unique identifier of the User
     * @param location the new location as a GeoPoint
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> updateUserPosition(String uuid, GeoPoint location){
        return this.db.updateField(USERS_COLLECTION_KEY + uuid, USER_LOCATION_KEY, location);
    }

    /**
     * Gets the User's location field
     * @param uuid the unique identifier of the User
     * @return A CompletableFuture wrapping the user's location
     */
    public CompletableFuture<GeoPoint> getUserPosition(String uuid){
        return this.db.getField(USERS_COLLECTION_KEY + uuid, USER_LOCATION_KEY);
    }

    /**
     * get the User's full name
     * @param uuid the unique identifier of the User
     * @return A CompletableFuture wrapping the user's full name
     */
    public CompletableFuture<String> getUserFullName(String uuid){
        return this.db.getField(USERS_COLLECTION_KEY + uuid, FULL_NAME_KEY);
    }

    /**
     * get the User's username
     * @param uuid the unique identifier of the User
     * @return A CompletableFuture wrapping the user's username
     */
    public CompletableFuture<String> getUserUsername(String uuid){
        return this.db.getField(USERS_COLLECTION_KEY + uuid, USERNAME_KEY);
    }

    /**
     * update the user's participating groups
     * @param uuid the unique identifier of the User
     * @param participatingGroups the new list of participating groups
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> updateParticipatingGroups(String uuid, List<String> participatingGroups) {
        return this.db.updateField(USERS_COLLECTION_KEY + uuid, PARTICIPATING_GROUPS_KEY, participatingGroups);
    }
    
     /* Add an event to the User's bookmark
     * @param uuid the unique identifier of the User
     * @param eventUuid the unique identifier of the Event to add
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> addBookmarkEventToUser(String uuid, String eventUuid){
        return this.db.updateArrayField(USERS_COLLECTION_KEY + uuid, SAVED_EVENTS_KEY, eventUuid);
    }
}
