package com.example.bootcamp;

import android.graphics.ImageDecoder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrivateUser {

    private static PrivateUser instance = new PrivateUser();
    private final FirebaseUser user;
    private DocumentReference docRef;
    private String first_name;
    private String family_name;
    private Date birth;

    public static PrivateUser getInstance(){
        return instance;
    }

    private PrivateUser(){
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.docRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        this.storeDB();
    }

    private void storeDB(){

        Log.d(MainActivity.TAG, "Trying to store in DB");

        Map<String, Object> initial_data = new HashMap<>();
        initial_data.put("uid", this.user.getUid());
        initial_data.put("email", this.user.getEmail());
        this.docRef.set(initial_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(MainActivity.TAG, "Successfully stored in DB");
                } else {
                    Log.d(MainActivity.TAG, "Error updating DB : " + task.getException().getMessage());
                }
            }
        });
    }

    private void update(String key, Object value){

        this.docRef.update(key, value).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(MainActivity.TAG, "Successfully updated the DB");
                } else {
                    Log.d(MainActivity.TAG, "Error updating DB : " + task.getException().getMessage());
                }
            }
        });
    }

    public void updateLastName(String name){
        this.update("last_name", name);
    }

    public void updateFirstName(String name){
        this.update("first_name", name);
    }

    public void updateBirth(Date birth){
        this.update("year_of_birth", birth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateUser that = (PrivateUser) o;
        return this.user.getUid() == that.user.getUid() && this.docRef == that.docRef;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
