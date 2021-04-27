package com.ncnf.user;

import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.event.PrivateEvent;
import com.ncnf.event.PublicEvent;
import com.ncnf.utilities.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.EVENTS_COLLECTION_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.Utils.OWNED_EVENTS_KEY;
import static com.ncnf.Utils.SAVED_EVENTS_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;
import static com.ncnf.Utils.UUID_KEY;
import static com.ncnf.utilities.InputValidator.isStringEmpty;

public class User {

    private DatabaseService db;

    private final String uuid;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> friendsIds;
    private List<String> ownedEventsIds;
    private List<String> savedEventsIds;
    private Date birthDate;
    private boolean notifications;


    private final IllegalStateException wrongCredentials = new IllegalStateException("User doesn't have the right credentials to perform current operation");

    public User(DatabaseService db, String uuid, String username, String email, String firstName, String lastName, List<String> friendsIds, List<String> ownedEventsIds, List<String> savedEventsIds, Date birthDate, boolean notifications) {
        if(isStringEmpty(uuid) || isStringEmpty(email)){
            throw new IllegalArgumentException();
        }
        this.db = db;
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendsIds = friendsIds;
        this.ownedEventsIds = ownedEventsIds;
        this.savedEventsIds = savedEventsIds;
        this.birthDate = birthDate;
        this.notifications = notifications;
    }

    public User(){
        this(new DatabaseService(), FirebaseAuth.getInstance().getUid(), "",FirebaseAuth.getInstance().getCurrentUser().getEmail(),"",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
    }

    public User(String username, String email, String firstName, String lastName, List<String> friendsIds, List<String> ownedEventsIds, List<String> savedEventsIds, Date birthDate, boolean notifications) {
        this(new DatabaseService(), FirebaseAuth.getInstance().getUid(), username, email, firstName, lastName, friendsIds, ownedEventsIds, savedEventsIds, birthDate, notifications);
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

    public List<String> getOwnedEventsIds() {
        return Collections.unmodifiableList(ownedEventsIds);
    }

    public List<String> getSavedEventsIds() {
        return Collections.unmodifiableList(savedEventsIds);
    }

    public Date getBirthDate() {
        return birthDate;
    }

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

    public void setOwnedEventsIds(List<String> ownedEventsIds) {
        this.ownedEventsIds = ownedEventsIds;
    }

    public void setSavedEventsIds(List<String> savedEventsIds) {
        this.savedEventsIds = savedEventsIds;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public CompletableFuture<Boolean>  updateNotifications(boolean isEnabled) {
        setNotifications(isEnabled);
        return this.db.updateField(USERS_COLLECTION_KEY+uuid, NOTIFICATIONS_KEY, isEnabled);
    }

    public CompletableFuture<Boolean>  updateNotificationsToken(String token) {
        return this.db.updateField(USERS_COLLECTION_KEY+uuid, NOTIFICATIONS_TOKEN_KEY, token);
    }

    public CompletableFuture<Boolean> saveUserToDB(){
        return this.db.setDocument(USERS_COLLECTION_KEY + uuid, this);
    }

    public CompletableFuture<User> loadUserFromDB(){
        CompletableFuture<User> futureUser = this.db.getDocument(USERS_COLLECTION_KEY + uuid, User.class);

        return futureUser.thenApply(response -> {
            User user = response;

            this.username = user.getUsername();
            this.email = user.getEmail();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.friendsIds = user.getFriendsIds();
            this.ownedEventsIds = user.getOwnedEventsIds();
            this.savedEventsIds = user.getSavedEventsIds();
            this.birthDate = user.getBirthDate();
            this.notifications = user.getNotifications();

            return this;
        }).exceptionally(exception -> {
            // TODO : handle exception
            return null;
        });
    }


    public CompletableFuture<List<User>> getFriends(){
        if(this.friendsIds.isEmpty()){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        return this.db.whereIn(USERS_COLLECTION_KEY, UUID_KEY, this.friendsIds, User.class);
    }


    public CompletableFuture<List<User>> getAllUsersLike(String username){
        return this.db.withFieldLike(USERS_COLLECTION_KEY, FIRST_NAME_KEY, username, User.class); // TODO : change to USERNAME_KEY
    }

    public CompletableFuture<Boolean> addSavedEvent(Event event){
        if(this.savedEventsIds.add(event.getUuid().toString())){
            return this.addEvent(event, SAVED_EVENTS_KEY);
        }
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<Boolean> addOwnedEvent(Event event){
        if(this.ownedEventsIds.add(event.getUuid().toString())){
            return this.addEvent(event, OWNED_EVENTS_KEY);
        }
        return CompletableFuture.completedFuture(false);
    }

    private CompletableFuture<Boolean> addEvent(Event event, String array){
        return db.updateArrayField(USERS_COLLECTION_KEY + uuid, array, event.getUuid().toString());
    }


    public CompletableFuture<List<Event>> getOwnedEvents(){

        if(this.ownedEventsIds.isEmpty()){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return this.getEvents(this.ownedEventsIds);
    }

    public CompletableFuture<List<Event>> getSavedEvents(){

        if(this.savedEventsIds.isEmpty()){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return this.getEvents(this.savedEventsIds);
    }

    private CompletableFuture<List<Event>> getEvents(List<String> eventIds){
        return this.db.whereIn(EVENTS_COLLECTION_KEY, UUID_KEY, eventIds, Event.class);
    }

    //TODO : for now it can store both type of events but won't be the case in the future
    public CompletableFuture<Boolean> createEvent(Event event){
        if(!event.getOwnerId().equals(this.uuid)){
            return CompletableFuture.completedFuture(false);
        }

        CompletableFuture<Boolean> storing;

       if(event instanceof PublicEvent){
           PublicEvent publicEvent = (PublicEvent) event;
           storing = publicEvent.store(this.db);
       } else {
           PrivateEvent privateEvent = (PrivateEvent) event;
           storing = privateEvent.store(this.db);
       }

        return storing
                .thenCompose(task -> this.addOwnedEvent(event))
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
