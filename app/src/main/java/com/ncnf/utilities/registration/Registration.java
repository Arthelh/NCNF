package com.ncnf.utilities.registration;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ncnf.models.User;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class Registration {

    private final FirebaseMessaging service;
    private final User user;

    @Inject
    public Registration(User user) {
        this.service = FirebaseMessaging.getInstance();
        this.user = user;
    }

    public Registration(FirebaseMessaging messaging, User user) {
        this.service = messaging;
        this.user = user;
    }

    /**
     * Register the current user in FirebaseMessaging service
     * @return CompletableFuture containing the FirebaseMessaging service's response : true if successful
     */
    public CompletableFuture<Boolean> register() {
        CompletableFuture<Boolean> res = new CompletableFuture<>();

        service.getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();

                user.updateNotifications(true).thenAcceptBoth(user.updateNotificationsToken(token), (r1, r2) -> {
                    res.complete(true);
                });

            } else {
                res.complete(false);
            }
        });
        return res;
    }

    /**
     * Remove the current user from FirebaseMessaging service
     * @return CompletableFuture containing the FirebaseMessaging service's response : true if successful
     */
    public CompletableFuture<Boolean> unregister() {
        CompletableFuture<Boolean> res = new CompletableFuture<>();

        user.updateNotifications(false).whenComplete((r, e) -> {
            res.complete(e == null);
        });

        return res;
    }

}
