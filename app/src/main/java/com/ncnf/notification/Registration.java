package com.ncnf.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ncnf.user.PrivateUser;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class Registration {

    private final FirebaseMessaging service;
    private final PrivateUser user;

    @Inject
    public Registration(PrivateUser user) {
        this.service = FirebaseMessaging.getInstance();
        this.user = user;
    }

    public Registration(FirebaseMessaging messaging, PrivateUser user) {
        this.service = messaging;
        this.user = user;
    }

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

    public CompletableFuture<Boolean> unregister() {
        CompletableFuture<Boolean> res = new CompletableFuture<>();

        user.updateNotifications(false).whenComplete((r, e) -> {
            res.complete(e == null);
        });

        return res;
    }

}
