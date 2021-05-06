package com.ncnf.user;

import com.ncnf.database.DatabaseService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ncnf.Utils.AWAITING_REQUESTS_KEY;
import static com.ncnf.Utils.FRIENDS_KEY;
import static com.ncnf.Utils.PENDING_REQUESTS_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FriendsRepositoryTests {

    private final static String uuid = "my_uuid";
    private final static String other_uuid = "other_uuid";

    private final DatabaseService mockDatabase = Mockito.mock(DatabaseService.class);
    private final FriendsRepository friends = new FriendsRepository(mockDatabase, uuid);

    private List<User> users;

    @Before
    public void setup() {
        User u1 = new User(mockDatabase, "u1", "John", "john@bar.com","John",  "Smith", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        User u2 = new User(mockDatabase, "u2", "Albert", "albert@bar.com","Albert",  "Dupont", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
        users = Arrays.asList(u1, u2);
    }

    @Test
    public void requestSuccessfullyUpdateTwoUsers() {
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> res = friends.request(other_uuid);

        verify(mockDatabase).updateArrayField(USERS_COLLECTION_KEY + uuid, PENDING_REQUESTS_KEY, other_uuid);
        verify(mockDatabase).updateArrayField(USERS_COLLECTION_KEY + other_uuid, AWAITING_REQUESTS_KEY, uuid);

        try {
            assertTrue(res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void requestFailsToUpdateTwoUsers() {
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(false));

        CompletableFuture<Boolean> res = friends.request(other_uuid);

        try {
            assertFalse(res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void awaitingRequestsIsSuccessful() {
        CompletableFuture<List<User>> future = CompletableFuture.completedFuture(users);
        when(mockDatabase.whereArrayContains(anyString(), anyString(), anyObject(), eq(User.class))).thenReturn(future);

        CompletableFuture<List<User>> res = friends.awaitingRequests();

        verify(mockDatabase).whereArrayContains(USERS_COLLECTION_KEY, PENDING_REQUESTS_KEY, uuid, User.class);

        try {
            assertThat(res.get(), is(users));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }


    @Test
    public void pendingRequestsIsSuccessful() {
        CompletableFuture<List<User>> future = CompletableFuture.completedFuture(users);
        when(mockDatabase.whereArrayContains(anyString(), anyString(), anyObject(), eq(User.class))).thenReturn(future);

        CompletableFuture<List<User>> res = friends.pendingRequests();

        verify(mockDatabase).whereArrayContains(USERS_COLLECTION_KEY, AWAITING_REQUESTS_KEY, uuid, User.class);

        try {
            assertThat(res.get(), is(users));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void acceptRequestIsSuccessful() {
        when(mockDatabase.removeArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> res = friends.updateRequest(true, other_uuid);

        verify(mockDatabase).removeArrayField(USERS_COLLECTION_KEY + uuid, AWAITING_REQUESTS_KEY, other_uuid);
        verify(mockDatabase).removeArrayField(USERS_COLLECTION_KEY + other_uuid, PENDING_REQUESTS_KEY, uuid);
        verify(mockDatabase).updateArrayField(USERS_COLLECTION_KEY + uuid, FRIENDS_KEY, other_uuid);
        verify(mockDatabase).updateArrayField(USERS_COLLECTION_KEY + other_uuid, FRIENDS_KEY, uuid);

        try {
            assertTrue(res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void acceptRequestFails() {
        when(mockDatabase.removeArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(false));
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(false));

        CompletableFuture<Boolean> res = friends.updateRequest(true, other_uuid);

        try {
            assertFalse(res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void declineRequestIsSuccessful() {
        when(mockDatabase.removeArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> res = friends.updateRequest(false, other_uuid);

        try {
            assertTrue(res.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getFriendsIsSuccessful() {
        CompletableFuture<List<User>> future = CompletableFuture.completedFuture(users);
        when(mockDatabase.whereArrayContains(anyString(), anyString(), anyString(), eq(User.class))).thenReturn(future);

        CompletableFuture<List<User>> res = friends.getFriends();
        
        try {
            assertThat(res.get(), is(users));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

}
