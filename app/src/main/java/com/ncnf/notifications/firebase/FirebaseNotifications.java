package com.ncnf.notifications.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ncnf.models.User;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class FirebaseNotifications {

    private final FirebaseMessaging service;
    private final User user;

    @Inject
    public FirebaseNotifications(User user) {
        this.service = FirebaseMessaging.getInstance();
        this.user = user;
    }

    public FirebaseNotifications(FirebaseMessaging messaging, User user) {
        this.service = messaging;
        this.user = user;
    }

    public CompletableFuture<Boolean> registerToNotifications() {
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

    public CompletableFuture<Boolean> unregisterFromNotifications() {
        CompletableFuture<Boolean> res = new CompletableFuture<>();

        user.updateNotifications(false).whenComplete((r, e) -> {
            res.complete(e == null);
        });

        return res;
    }

}
