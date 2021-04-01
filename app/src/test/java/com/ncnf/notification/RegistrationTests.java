package com.ncnf.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.mocks.MockTask;
import com.ncnf.user.PrivateUser;

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
    private final PrivateUser user = Mockito.mock(PrivateUser.class);
    Registration registration = new Registration(messaging, user);

    @Test
    public void registerIsSuccessful() {

        MockTask<String> task = new MockTask("My token", null);
        CompletableFuture<DatabaseResponse> notificationFuture = CompletableFuture.completedFuture(
                new DatabaseResponse(true, null, null));
        CompletableFuture<DatabaseResponse> tokenFuture = CompletableFuture.completedFuture(
                new DatabaseResponse(true, null, null));

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
        CompletableFuture<DatabaseResponse> notificationFuture = CompletableFuture.completedFuture(
                new DatabaseResponse(true, null, null));
        CompletableFuture<DatabaseResponse> tokenFuture = CompletableFuture.completedFuture(
                new DatabaseResponse(true, null, null));

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
        CompletableFuture<DatabaseResponse> notificationFuture = CompletableFuture.completedFuture(
                new DatabaseResponse(true, null, null));

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
        CompletableFuture<DatabaseResponse> notificationFuture = new CompletableFuture<>();

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
