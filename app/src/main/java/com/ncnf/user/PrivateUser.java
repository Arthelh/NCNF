package com.ncnf.user;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.BIRTH_YEAR_KEY;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EMAIL_KEY;
import static com.ncnf.Utils.EMPTY_STRING;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.FRIENDS_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static com.ncnf.Utils.OWNED_EVENTS_KEY;
import static com.ncnf.Utils.SAVED_EVENTS_KEY;
import static com.ncnf.Utils.USERs_COLLECTION_KEY;

public class PrivateUser {

    private FirebaseUser user;
    private String path;

    public PrivateUser(FirebaseUser user, String path) {
        this.user = user;
        this.path = path;
    }

    public String getID(){
        if(this.user != null) {
            return this.user.getUid();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse> saveUserToDB(){
        verifyCredential();

        Log.d(DEBUG_TAG, "Integrating new user to DB");

        Map<String, Object> initial_data = new HashMap<>();
        initial_data.put(EMAIL_KEY, this.user.getEmail());
        initial_data.put(LAST_NAME_KEY, EMPTY_STRING);
        initial_data.put(FIRST_NAME_KEY, EMPTY_STRING);
        initial_data.put(BIRTH_YEAR_KEY, 0);
        initial_data.put(FRIENDS_KEY, new ArrayList<String>());
        initial_data.put(OWNED_EVENTS_KEY, new ArrayList<String>());
        initial_data.put(SAVED_EVENTS_KEY, new ArrayList<String>());

        return DatabaseService.getInstance().setDocument(this.path, initial_data);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private CompletableFuture<DatabaseResponse> update(String key, Object value){
        verifyCredential();
        return DatabaseService.getInstance().updateField(path, key, value);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse> getField(String field){
        verifyCredential();
        return DatabaseService.getInstance().getField(path, field);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse>  updateLastName(String name){
        return this.update(LAST_NAME_KEY, name);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse>  updateFirstName(String name){
        return this.update(FIRST_NAME_KEY, name);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse>  updateBirth(int year){
        return this.update(BIRTH_YEAR_KEY, year);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse> loadUserFromBD(){
        verifyCredential();
        return DatabaseService.getInstance().getData(this.path);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse> saveEvent(Event event){
        return this.addEvent(event, SAVED_EVENTS_KEY);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse> ownEvent(Event event){
        return this.addEvent(event, OWNED_EVENTS_KEY);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private CompletableFuture<DatabaseResponse> addEvent(Event event, String array){
        verifyCredential();
        return this.update(array, FieldValue.arrayUnion(event.getUID().toString()));
    }

    public void delete(){
        user = null;
        path = null;
    }

    public void verifyCredential(){
        if(user == null || path == null){
            throw new IllegalStateException("User doesn't have the right credentials to perform current operation");
        }
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        user.delete();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        PrivateUser that = (PrivateUser) o;
        if (o == null || getClass() != o.getClass() ||this.user == null || that.user == null) return false;
        return user.getUid().equals(that.user.getUid()) && this.path == that.path;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}