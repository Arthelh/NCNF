package com.ncnf.user;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.ncnf.R;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.database.DatabaseServiceInterface;
import com.ncnf.event.Event;
import com.ncnf.event.EventBuilder;
import com.ncnf.feed.ui.EventAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.ncnf.Utils.BIRTH_YEAR_KEY;
import static com.ncnf.Utils.DEBUG_TAG;
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

    private final DatabaseServiceInterface db;

    private final String email;
    private final String UUID;
    private final String path;

    public PrivateUser(String UUID, String email) {
        if(UUID == null || email == null) {
            throw new IllegalStateException("User doesn't have the right credentials to perform current operation");
        }

        this.db = new DatabaseService();
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

    public CompletableFuture<DatabaseResponse> saveUserToDB(){
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

    public CompletableFuture<DatabaseResponse> ownEvent(Event event){
        return this.addEvent(event, OWNED_EVENTS_KEY);
    }

    private CompletableFuture<DatabaseResponse> addEvent(Event event, String array){
        return this.update(array, FieldValue.arrayUnion(event.getUuid()));
    }


    public CompletableFuture<CompletableFuture<List<Event>>> getAllEvents(String eventCollection){
        return this.getField(eventCollection).thenApply(task -> {
            if(task.isSuccessful()){

                List<String> eventIds = (List<String>) task.getResult();
                List<CompletableFuture<Event>> eventsFuture = new ArrayList<>();
                EventBuilder builder = new EventBuilder();
                for(String s : eventIds){
                    Log.d(DEBUG_TAG, s);
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


    public void signOut() {
        CurrentUserModule.signOut();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateUser that = (PrivateUser) o;
        return this.UUID.equals(that.UUID) && this.path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UUID);
    }
}