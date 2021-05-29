package com.ncnf.notifications.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ncnf.models.User;
import com.ncnf.repositories.UserRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class FirebaseNotifications {

    private final FirebaseMessaging service;
    private final User user;
    private final UserRepository userRepository;

    @Inject
    public FirebaseNotifications(User user, UserRepository userRepository) {
        this(FirebaseMessaging.getInstance(), user, userRepository);
    }

    public FirebaseNotifications(FirebaseMessaging messaging, User user, UserRepository userRepository) {
        this.service = messaging;
        this.user = user;
        this.userRepository = userRepository;
    }

    public CompletableFuture<Boolean> registerToNotifications() {
        CompletableFuture<Boolean> res = new CompletableFuture<>();

        service.getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();

                userRepository.updateNotifications(user.getUuid(), true)
                        .thenAcceptBoth(userRepository.updateNotificationsToken(user.getUuid(), token), (r1, r2) -> res.complete(true));

            } else {
                res.complete(false);
            }
        });
        return res;
    }

    public CompletableFuture<Boolean> unregisterFromNotifications() {
        CompletableFuture<Boolean> res = new CompletableFuture<>();

        userRepository.updateNotifications(user.getUuid(), false).whenComplete((r, e) -> {
            res.complete(e == null);
        });

        return res;
    }

}