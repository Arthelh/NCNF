package com.ncnf.user;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import static com.ncnf.Utils.*;

public class PrivateUser {

    private static PrivateUser instance = new PrivateUser();
    private static FirebaseUser user;
    private static DocumentReference docRef;

    public static PrivateUser getInstance(){
        if(user == null || docRef == null){
            instance = new PrivateUser();
        }
        return instance;
    }

    private PrivateUser(){
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        if(this.user != null){
            this.docRef = FirebaseFirestore.getInstance().document(USERs_COLLECTION_KEY + user.getUid());
            Log.d(DEBUG_TAG, this.docRef.toString());
        } else {
            this.docRef = null;
        }
    }

    public void createDBUser(OnSuccessListener s, OnFailureListener f){

        Log.d(DEBUG_TAG, "Integrating new user to DB");

        Map<String, Object> initial_data = new HashMap<>();
        initial_data.put(EMAIL_KEY, this.user.getEmail());
        initial_data.put(LAST_NAME_KEY, EMPTY_STRING);
        initial_data.put(FIRST_NAME_KEY, EMPTY_STRING);
        initial_data.put(BIRTH_YEAR_KEY, 0);
        initial_data.put(FRIENDS_KEY, new ArrayList<String>());
        initial_data.put(OWNED_EVENTS_KEY, new ArrayList<String>());
        initial_data.put(SAVED_EVENTS_KEY, new ArrayList<String>());

        this.docRef.set(initial_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(DEBUG_TAG, "Successfully stored new user in DB");
                    s.onSuccess(task.getResult());
                } else {
                    f.onFailure(task.getException());
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

    public void loadUserFromBD(OnSuccessListener s, OnFailureListener f){
        this.docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        s.onSuccess(document.getData());
                    } else {
                        f.onFailure(task.getException());
                        Log.d(DEBUG_TAG, "No such document");
                    }
                } else {
                    Log.d(DEBUG_TAG, "Failing retrieving data : ", task.getException());
                }
            }
        });
    }

    public void delete(){
        this.user = null;
        this.docRef = null;
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