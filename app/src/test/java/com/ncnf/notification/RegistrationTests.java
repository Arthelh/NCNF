package com.ncnf.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ncnf.mocks.MockTask;
import com.ncnf.models.User;
import com.ncnf.utilities.registration.Registration;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationTests {

    private final FirebaseMessaging messaging = Mockito.mock(FirebaseMessaging.class);
    private final User user = Mockito.mock(User.class);
    Registration registration = new Registration(messaging, user);

    @Test
    public void registerIsSuccessful() {

        MockTask<String> task = new MockTask("My token", null);
        CompletableFuture<Boolean> notificationFuture = CompletableFuture.completedFuture(true);
        CompletableFuture<Boolean> tokenFuture = CompletableFuture.completedFuture(true);

        when(messaging.getToken()).thenReturn(task);
        when(user.updateNotifications(anyBoolean())).thenReturn(notificationFuture);
        when(user.updateNotificationsToken(anyString())).thenReturn(tokenFuture);

        CompletableFuture<Boolean> res = registration.register();

        verify(user).updateNotifications(true);
        verify(user).updateNotificationsToken("My token");

        try {
            assertEquals(true, res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void registerFails() {
        MockTask<String> task = new MockTask("My token", null, false);

        when(messaging.getToken()).thenReturn(task);

        CompletableFuture<Boolean> res = registration.register();
        try {
            assertEquals(false, res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void unregisterIsSuccessful() {
        CompletableFuture<Boolean> notificationFuture = CompletableFuture.completedFuture(true);

        when(user.updateNotifications(anyBoolean())).thenReturn(notificationFuture);

        CompletableFuture<Boolean> res = registration.unregister();
        try {
            assertEquals(true, res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void unregisterFails() {
        CompletableFuture<Boolean> notificationFuture = new CompletableFuture<>();

        when(user.updateNotifications(anyBoolean())).thenReturn(notificationFuture);

        notificationFuture.completeExceptionally(new Exception());
        CompletableFuture<Boolean> res = registration.unregister();

        try {
            assertEquals(false, res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

}
