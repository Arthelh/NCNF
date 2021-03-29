package com.ncnf.notification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Tag;

public class Registration {

    private FirebaseMessaging service;
    private DatabaseService db;

    public Registration(DatabaseService db) {
        this.service = FirebaseMessaging.getInstance();
        this.db = db;
    }

    public void register() {
        service.getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d("NOTIFICATION", "REGISTRATION WITH TOKEN" + token);
                updateUserToken(token);
            } else {
                // return a failure
            }
        });
    }

    public void unregister() {
        Log.d("NOTIFICATION", "UNREGISTRATION");
    }

    private void updateUserToken(String token) {

    }

}
