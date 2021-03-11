package com.example.bootcamp;

import android.graphics.ImageDecoder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrivateUser {

    private final FirebaseUser user;
    private DocumentReference docRef;
    private String first_name;
    private String family_name;
    private Date birth;

    public PrivateUser(FirebaseUser user){
        this.user = user;
        this.docRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        this.storeDB();
    }

//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }
//
//    public String getFamily_name() {
//        return family_name;
//    }
//
//    public void setFamily_name(String family_name) {
//        this.family_name = family_name;
//    }
//
//    public String getFirst_name() {
//        return first_name;
//    }
//
//    public void setFirst_name(String first_name) {
//        this.first_name = first_name;
//    }

    private void storeDB(){

        this.docRef.update("uid", this.user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
