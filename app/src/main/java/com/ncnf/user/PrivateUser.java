package com.ncnf.user;

import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.event.EventBuilder;
import com.ncnf.event.PrivateEvent;
import com.ncnf.event.PublicEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.ncnf.Utils.BIRTH_YEAR_KEY;
import static com.ncnf.Utils.EMAIL_KEY;
import static com.ncnf.Utils.EMPTY_STRING;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.FRIENDS_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_TOKEN_KEY;
import static com.ncnf.Utils.OWNED_EVENTS_KEY;
import static com.ncnf.Utils.SAVED_EVENTS_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;

public class PrivateUser {

    private DatabaseService db;
    private String email;
    private final String UUID;
    private final String path;

    public PrivateUser(){
        this.db = new DatabaseService();
        this.UUID = FirebaseAuth.getInstance().getUid();
        this.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        this.path = USERS_COLLECTION_KEY + UUID;

    }

    public PrivateUser(String UUID, String email) {
        if(UUID == null || email == null || UUID.isEmpty() || email.isEmpty()) {
            throw new IllegalStateException("User doesn't have the right credentials to perform current operation");
        }

        this.db = new DatabaseService();
        this.path = USERS_COLLECTION_KEY + UUID;
        this.UUID = UUID;
        this.email = email;
    }

    public PrivateUser(String UUID, DatabaseService db){
        this.path = USERS_COLLECTION_KEY + UUID;
        this.UUID = UUID;
        this.db = db;
    }

    protected PrivateUser(DatabaseService db, String UUID, String email) {
        if(UUID == null || email == null || UUID.isEmpty() || email.isEmpty()) {
            throw new IllegalStateException("User doesn't have the right credentials to perform current operation");
        }

        this.db = db;
        this.path = USERS_COLLECTION_KEY + UUID;
        this.UUID = UUID;
        this.email = email;
    }

    public String getID(){
        return UUID;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public CompletableFuture<DatabaseResponse> saveUserToDB(){
        if(this.email == null || this.email.isEmpty()){
            return CompletableFuture.completedFuture(new DatabaseResponse(false, null, new IllegalStateException("User's email can't be null or empty")));
        }

        Map<String, Object> initial_data = new HashMap<>();
        initial_data.put(EMAIL_KEY, this.email);
        initial_data.put(LAST_NAME_KEY, EMPTY_STRING);
        initial_data.put(FIRST_NAME_KEY, EMPTY_STRING);
        initial_data.put(BIRTH_YEAR_KEY, 0);
        initial_data.put(FRIENDS_KEY, new ArrayList<String>());
        initial_data.put(OWNED_EVENTS_KEY, new ArrayList<String>());
        initial_data.put(SAVED_EVENTS_KEY, new ArrayList<String>());
        initial_data.put(NOTIFICATIONS_KEY, false);

        return this.db.setDocument(this.path, initial_data);
    }

    private CompletableFuture<DatabaseResponse> update(String key, Object value){
        return this.db.updateField(path, key, value);
    }

    public CompletableFuture<DatabaseResponse> getField(String field){
        return this.db.getField(path, field);
    }

    public CompletableFuture<DatabaseResponse>  updateLastName(String name){
        return this.update(LAST_NAME_KEY, name);
    }

    public CompletableFuture<DatabaseResponse>  updateFirstName(String name){
        return this.update(FIRST_NAME_KEY, name);
    }

    public CompletableFuture<DatabaseResponse>  updateBirth(int year){
        return this.update(BIRTH_YEAR_KEY, year);
    }

    public CompletableFuture<DatabaseResponse>  updateNotifications(boolean isEnabled) {
        return this.update(NOTIFICATIONS_KEY, isEnabled);
    }

    public CompletableFuture<DatabaseResponse>  updateNotificationsToken(String token) {
        return this.update(NOTIFICATIONS_TOKEN_KEY, token);
    }

    public CompletableFuture<DatabaseResponse> loadUserFromDB(){
        return this.db.getData(this.path);
    }

    public CompletableFuture<DatabaseResponse> saveEvent(Event event){
        return this.addEvent(event, SAVED_EVENTS_KEY);
    }

    private CompletableFuture<DatabaseResponse> addEvent(Event event, String array){
        return db.updateArrayField(path, array, event.getUuid().toString());
    }


    public CompletableFuture<CompletableFuture<List<Event>>> getAllEvents(String eventCollection){
        return this.getField(eventCollection).thenApply(task -> {
            if(task.isSuccessful()){

                List<String> eventIds = (List<String>) task.getResult();
                List<CompletableFuture<Event>> eventsFuture = new ArrayList<>();
                EventBuilder builder = new EventBuilder(this.db);
                for(String s : eventIds){
                    eventsFuture.add(builder.build(s));
                }

                //Create a future of list from a list of future
                CompletableFuture<List<Event>> listEvent = CompletableFuture.allOf(eventsFuture.toArray(new CompletableFuture<?>[0]))
                        .thenApply(v -> eventsFuture.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList())
                        );
                return listEvent;
            } else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    //TODO : for now it can store both type of events but won't be the case in the future
    public CompletableFuture<CompletableFuture<DatabaseResponse>> createEvent(Event event){
        if(event.getOwnerId() != this.UUID){
            return CompletableFuture.completedFuture(CompletableFuture.completedFuture(new DatabaseResponse(false, null, new IllegalStateException("Current user isn't the user associated with this event"))));
        }

        CompletableFuture<DatabaseResponse> storing;

       if(event instanceof PublicEvent){
           PublicEvent publicEvent = (PublicEvent) event;
           storing = publicEvent.store(this.db);
       } else {
           PrivateEvent privateEvent = (PrivateEvent) event;
           storing = privateEvent.store(this.db);
       }

        return storing.thenApply(task -> {
            if(task.isSuccessful()){
                return this.addEvent(event, OWNED_EVENTS_KEY);
            }
            return CompletableFuture.completedFuture(new DatabaseResponse(false, null, task.getException()));
        });
    }


    public void signOut() {
        CurrentUserModule.signOut();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateUser that = (PrivateUser) o;
        return this.UUID.equals(that.UUID) && this.path.equals(that.path) && this.email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UUID);
    }
}