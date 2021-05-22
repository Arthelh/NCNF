package com.ncnf.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.authentication.firebase.FirebaseAuthentication;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.authentication.firebase.CurrentUserModule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.utilities.InputValidator.isInvalidString;
import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.FULL_NAME_KEY;
import static com.ncnf.utilities.StringCodes.GROUPS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.utilities.StringCodes.OWNED_GROUPS_KEY;
import static com.ncnf.utilities.StringCodes.PARTICIPATING_GROUPS_KEY;
import static com.ncnf.utilities.StringCodes.SAVED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;

public class User {

    private FirebaseDatabase db;

    private final String uuid;
    private String username;
    private String email;
    private String fullName;
    private List<String> friendsIds;
    private List<String> ownedGroupsIds;
    private List<String> participatingGroupsIds;
    private List<String> savedEventsIds;
    private LocalDate birthDate;
    private boolean notifications;
    private GeoPoint loc;

    public User(FirebaseDatabase db, String uuid, String username, String email, String fullName, List<String> friendsIds, List<String> ownedGroupsIds, List<String> participatingGroups, List<String> savedEventsIds, boolean notifications, LocalDate birthDate, GeoPoint loc) {
        if(isInvalidString(uuid) || isInvalidString(email)){
            throw new IllegalArgumentException();
        }
        this.db = db;
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.friendsIds = friendsIds;
        this.ownedGroupsIds = ownedGroupsIds;
        this.savedEventsIds = savedEventsIds;
        this.birthDate = birthDate;
        this.notifications = notifications;
        this.participatingGroupsIds = participatingGroups;
        this.loc = loc;
    }

    public User(){
        this(new FirebaseDatabase(), FirebaseAuth.getInstance().getUid(), "",FirebaseAuth.getInstance().getCurrentUser().getEmail(),"", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
    }

    public User(String username, String email, String fullName, String lastName, List<String> friendsIds, List<String> ownedGroupsIds, List<String> savedEventsIds, LocalDate birthDate, boolean notifications, GeoPoint loc) {
        this(new FirebaseDatabase(), FirebaseAuth.getInstance().getUid(), username, email, fullName, friendsIds, ownedGroupsIds, new ArrayList<>(), savedEventsIds, notifications, birthDate, loc);
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

    public String getFullName() {
        return fullName;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public GeoPoint getLoc() { return loc; }

    public boolean getNotifications() {
        return notifications;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setFriendsIds(List<String> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public void setLoc(GeoPoint loc) { this.loc = loc; }

    public void setEmail(String email){
        this.email = email;
    }

    
    public void setParticipatingGroupsIds(List<String> participatingGroupsIds) {
        this.participatingGroupsIds = new ArrayList<>(participatingGroupsIds);
    }

    public void setOwnedGroupsIds(List<String> ownedGroupsIds) {
        this.ownedGroupsIds = new ArrayList<>(ownedGroupsIds);
    }

    public void setSavedEventsIds(List<String> savedEventsIds) {
        this.savedEventsIds = new ArrayList<>(savedEventsIds);
    }

    public void setBirthDate(LocalDate birthDate) {
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
            this.fullName = response.getFullName();
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
        return this.db.withFieldContaining(USERS_COLLECTION_KEY, FULL_NAME_KEY, username, User.class); // TODO : change to username when possible
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
                .thenCompose(task -> this.addOwnedGroup(group))
                .exceptionally(exception -> false); // TODO: handle exception
    }

    public void signOut() {
        CurrentUserModule.signOut();
    }

    public CompletableFuture<Boolean> changeEmail(FirebaseAuthentication auth, String email){
        return auth.changeEmail(email);
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