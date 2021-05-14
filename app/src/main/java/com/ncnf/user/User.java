package com.ncnf.user;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;
import com.ncnf.socialObject.Event;
import com.ncnf.socialObject.Group;
import com.ncnf.user.helpers.CurrentUserModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.utilities.InputValidator.isInvalidString;
import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.FIRST_NAME_KEY;
import static com.ncnf.utilities.StringCodes.GROUPS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.utilities.StringCodes.OWNED_GROUPS_KEY;
import static com.ncnf.utilities.StringCodes.PARTICIPATING_GROUPS_KEY;
import static com.ncnf.utilities.StringCodes.SAVED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;

public class User {

    private DatabaseService db;

    private final String uuid;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> friendsIds;
    private List<String> ownedGroupsIds;
    private List<String> participatingGroupsIds;
    private List<String> savedEventsIds;
    private Date birthDate;
    private boolean notifications;
    private GeoPoint loc;

    
    private final IllegalStateException wrongCredentials = new IllegalStateException("User doesn't have the right credentials to perform current operation");

    public User(DatabaseService db, String uuid, String username, String email, String firstName, String lastName, List<String> friendsIds, List<String> ownedGroupsIds, List<String> participatingGroups, List<String> savedEventsIds, boolean notifications, Date birthDate, GeoPoint loc) {
        if(isInvalidString(uuid) || isInvalidString(email)){
            throw new IllegalArgumentException();
        }
        this.db = db;
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendsIds = friendsIds;
        this.ownedGroupsIds = ownedGroupsIds;
        this.savedEventsIds = savedEventsIds;
        this.birthDate = birthDate;
        this.notifications = notifications;
        this.participatingGroupsIds = participatingGroups;
        this.loc = loc;
    }

    public User(){
        this(new DatabaseService(), FirebaseAuth.getInstance().getUid(), "",FirebaseAuth.getInstance().getCurrentUser().getEmail(),"",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
    }

    public User(String username, String email, String firstName, String lastName, List<String> friendsIds, List<String> ownedGroupsIds, List<String> savedEventsIds, Date birthDate, boolean notifications, GeoPoint loc) {
        this(new DatabaseService(), FirebaseAuth.getInstance().getUid(), username, email, firstName, lastName, friendsIds, ownedGroupsIds, new ArrayList<>(), savedEventsIds, notifications, birthDate, loc);
    }

    public String getUuid(){
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail(){
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getFriendsIds() {
        return Collections.unmodifiableList(friendsIds);
    }

    public List<String> getOwnedGroupsIds() {
        return Collections.unmodifiableList(ownedGroupsIds);
    }

    public List<String> getParticipatingGroupsIds() {
        return Collections.unmodifiableList(participatingGroupsIds);
    }

    public List<String> getSavedEventsIds() {
        return Collections.unmodifiableList(savedEventsIds);
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public GeoPoint getLoc() { return loc; }

    public boolean getNotifications() {
        return notifications;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFriendsIds(List<String> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public void setLoc(GeoPoint loc) { this.loc = loc; }

    
    public void setParticipatingGroupsIds(List<String> participatingGroupsIds) {
        this.participatingGroupsIds = new ArrayList<>(participatingGroupsIds);
    }

    public void setOwnedGroupsIds(List<String> ownedGroupsIds) {
        this.ownedGroupsIds = new ArrayList<>(ownedGroupsIds);
    }

    public void setSavedEventsIds(List<String> savedEventsIds) {
        this.savedEventsIds = new ArrayList<>(savedEventsIds);
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public CompletableFuture<Boolean> updateNotifications(boolean isEnabled) {
        setNotifications(isEnabled);
        return this.db.updateField(USERS_COLLECTION_KEY+uuid, NOTIFICATIONS_KEY, isEnabled);
    }

    public CompletableFuture<Boolean> updateNotificationsToken(String token) {
        return this.db.updateField(USERS_COLLECTION_KEY+uuid, NOTIFICATIONS_TOKEN_KEY, token);
    }

    public CompletableFuture<Boolean> saveUserToDB(){
        return this.db.setDocument(USERS_COLLECTION_KEY + uuid, this);
    }

    public CompletableFuture<User> loadUserFromDB(){
        CompletableFuture<User> futureUser = this.db.getDocument(USERS_COLLECTION_KEY + uuid, User.class);

        return futureUser.thenApply(response -> {
            this.username = response.getUsername();
            this.email = response.getEmail();
            this.firstName = response.getFirstName();
            this.lastName = response.getLastName();
            this.friendsIds = response.getFriendsIds();
            this.ownedGroupsIds = response.getOwnedGroupsIds();
            this.participatingGroupsIds = response.getParticipatingGroupsIds();
            this.savedEventsIds = response.getSavedEventsIds();
            this.birthDate = response.getBirthDate();
            this.notifications = response.getNotifications();
            this.loc = response.getLoc();

            return this;
        }).exceptionally(exception -> {
            // TODO : handle exception
            return null;
        });
    }

    public CompletableFuture<List<User>> getAllUsersLike(String username){
        return this.db.withFieldContaining(USERS_COLLECTION_KEY, FIRST_NAME_KEY, username, User.class); // TODO : change to username when possible
    }

    public CompletableFuture<Boolean> addSavedEvent(Event event){
        if(this.savedEventsIds.add(event.getUuid().toString())){
            return this.db.updateArrayField(USERS_COLLECTION_KEY + uuid, SAVED_EVENTS_KEY, event.getUuid().toString());
        }
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<Boolean> addOwnedGroup(Group group){

        if(this.ownedGroupsIds.add(group.getUuid().toString())){
            return this.db.updateArrayField(USERS_COLLECTION_KEY + uuid, OWNED_GROUPS_KEY, group.getUuid().toString())
                    .thenCompose(task -> addParticipatingGroup(group));
        }
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<Boolean> addParticipatingGroup(Group group){
        if(this.participatingGroupsIds.add(group.getUuid().toString())){
            return this.db.updateArrayField(USERS_COLLECTION_KEY + uuid, PARTICIPATING_GROUPS_KEY, group.getUuid().toString());
        }
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<List<Group>> getOwnedGroups(){

        if(this.ownedGroupsIds.isEmpty()){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return this.db.whereIn(GROUPS_COLLECTION_KEY, UUID_KEY, this.ownedGroupsIds, Group.class);
    }

    public CompletableFuture<List<Group>> getParticipatingGroups(){

        if(this.participatingGroupsIds.isEmpty()){

            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return this.db.whereIn(GROUPS_COLLECTION_KEY, UUID_KEY, this.ownedGroupsIds, Group.class);
    }

    public CompletableFuture<List<Event>> getSavedEvents(){

        if(this.savedEventsIds.isEmpty()){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return this.db.whereIn(EVENTS_COLLECTION_KEY, UUID_KEY, savedEventsIds, Event.class);
    }

    //TODO : for now it can store both type of events but won't be the case in the future
    public CompletableFuture<Boolean> createGroup(Group group){
        if(!group.getOwnerId().equals(this.uuid)){
            return CompletableFuture.completedFuture(false);
        }

        return group.store(this.db)
                .thenCompose(task -> {
                    return this.addOwnedGroup(group);

                })
                .exceptionally(exception -> false); // TODO: handle exception
    }

    public void signOut() {
        CurrentUserModule.signOut();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uuid, user.uuid) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
