package com.example.bootcamp;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static com.example.bootcamp.Utils.*;

public class PrivateUser {

    private static final PrivateUser instance = new PrivateUser();
    private final FirebaseUser user;
    private final DocumentReference docRef;

    public static PrivateUser getInstance(){
        return instance;
    }

    private PrivateUser(){
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.docRef = FirebaseFirestore.getInstance().document(USERs_COLLECTION + user.getUid());
    }

    public void createDBUser(DatabaseLambda l){

        Log.d(DEBUG_TAG, "Integrating new user to DB");

        Map<String, Object> initial_data = new HashMap<>();
        initial_data.put(EMAIL_KEY, this.user.getEmail());
        initial_data.put(LAST_NAME_KEY, EMPTY_STRING);
        initial_data.put(FIRST_NAME_KEY, EMPTY_STRING);
        initial_data.put(BIRTH_YEAR_KEY, null);
        initial_data.put(FRIENDS_KEY, new ArrayList<String>());
        initial_data.put(OWNED_EVENTS, new ArrayList<String>());
        initial_data.put(SAVED_EVENTS, new ArrayList<String>());

        this.docRef.set(initial_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(DEBUG_TAG, "Successfully stored new user in DB");
                    l.applyAfterStoreSuccess();
                } else {
                    l.applyAfterStoreFailure();
                    Log.d(DEBUG_TAG, "Error adding new user to DB : " + task.getException().getMessage());
                }
            }
        });
    }

    private void update(String key, Object value){

        this.docRef.update(key, value).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(DEBUG_TAG, "Successfully updated the DB");
                } else {
                    Log.d(DEBUG_TAG, "Error updating DB : " + task.getException().getMessage());
                }
            }
        });
    }

    public void updateLastName(String name){
        this.update(LAST_NAME_KEY, name);
    }

    public void updateFirstName(String name){
        this.update(FIRST_NAME_KEY, name);
    }

    public void updateBirth(int year){
        this.update(BIRTH_YEAR_KEY, year);
    }

    public void loadBD(DatabaseLambda l){
        this.docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        l.applyAfterLoadSuccess(document.getData());
                    } else {
                        l.applyAfterLoadFailure();
                        Log.d(DEBUG_TAG, "No such document");
                    }
                } else {
                    Log.d(DEBUG_TAG, "Failing retrieving data : ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateUser that = (PrivateUser) o;
        return this.user.getUid().equals(that.user.getUid()) && this.docRef == that.docRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
