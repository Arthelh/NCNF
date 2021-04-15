package com.ncnf.user;

import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ncnf.Utils.AWAITING_REQUESTS_KEY;
import static com.ncnf.Utils.FRIENDS_KEY;
import static com.ncnf.Utils.PENDING_REQUESTS_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FriendsTests {

    private final static String uuid = "my_uuid";
    private final static String other_uuid = "other_uuid";

    private final DatabaseService mockDatabase = Mockito.mock(DatabaseService.class);
    private final Friends friends = new Friends(mockDatabase, uuid);

    private List<String> ids;
    private HashMap<String, String> users;

    private CompletableFuture<DatabaseResponse> successFuture(Object result) {
        return CompletableFuture.completedFuture(new DatabaseResponse(true, result, null));
    }

    private CompletableFuture<DatabaseResponse> failedFuture(Object result) {
        return CompletableFuture.completedFuture(new DatabaseResponse(false, result, null));
    }

    @Before
    public void setup() {
        ids = Arrays.asList("u1");
        users = new HashMap<>();
        users.put("uuid", "u1");
        users.put("username", "Johnny");
    }

    @Test
    public void requestSuccessfullyUpdateTwoUsers() {
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyObject())).thenReturn(successFuture(null));

        CompletableFuture<DatabaseResponse> res = friends.request(other_uuid);

        verify(mockDatabase).updateArrayField(USERS_COLLECTION_KEY + uuid, PENDING_REQUESTS_KEY, other_uuid);
        verify(mockDatabase).updateArrayField(USERS_COLLECTION_KEY + other_uuid, AWAITING_REQUESTS_KEY, uuid);

        try {
            assertTrue(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void requestFailsToUpdateTwoUsers() {
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyObject())).thenReturn(failedFuture(null));

        CompletableFuture<DatabaseResponse> res = friends.request(other_uuid);

        try {
            assertFalse(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void awaitingRequestsIsSuccessful() {
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(successFuture(ids));
        when(mockDatabase.whereEqualTo(anyString(), anyString(), anyList())).thenReturn(successFuture(users));

        CompletableFuture<DatabaseResponse> res = friends.awaitingRequests();

        verify(mockDatabase).getField(USERS_COLLECTION_KEY + uuid, AWAITING_REQUESTS_KEY);
        verify(mockDatabase).whereEqualTo(USERS_COLLECTION_KEY, "uuid", ids);

        try {
            assertTrue(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void awaitingRequestsFailsWithTheFirstRequest() {
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(failedFuture(ids));

        CompletableFuture<DatabaseResponse> res = friends.awaitingRequests();

        try {
            assertFalse(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void awaitingRequestsFailsWithTheSecondRequest() {
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(successFuture(ids));
        when(mockDatabase.whereEqualTo(anyString(), anyString(), anyList())).thenReturn(failedFuture(users));

        CompletableFuture<DatabaseResponse> res = friends.awaitingRequests();

        try {
            assertFalse(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void pendingRequestsIsSuccessful() {
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(successFuture(ids));
        when(mockDatabase.whereEqualTo(anyString(), anyString(), anyList())).thenReturn(successFuture(users));

        CompletableFuture<DatabaseResponse> res = friends.pendingRequests();

        verify(mockDatabase).getField(USERS_COLLECTION_KEY + uuid, PENDING_REQUESTS_KEY);
        verify(mockDatabase).whereEqualTo(USERS_COLLECTION_KEY, "uuid", ids);

        try {
            assertTrue(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void pendingRequestsFails() {
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(failedFuture(ids));

        CompletableFuture<DatabaseResponse> res = friends.pendingRequests();

        try {
            assertFalse(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getFriendsIsSuccessful() {
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(successFuture(ids));
        when(mockDatabase.whereEqualTo(anyString(), anyString(), anyList())).thenReturn(successFuture(users));

        CompletableFuture<DatabaseResponse> res = friends.getFriends();

        verify(mockDatabase).getField(USERS_COLLECTION_KEY + uuid, FRIENDS_KEY);
        verify(mockDatabase).whereEqualTo(USERS_COLLECTION_KEY, "uuid", ids);

        try {
            assertTrue(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getFriendsFails() {
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(failedFuture(ids));

        CompletableFuture<DatabaseResponse> res = friends.getFriends();

        try {
            assertFalse(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void updateRequestIsSuccessful() {
        when(mockDatabase.removeArrayField(anyString(), anyString(), anyString())).thenReturn(successFuture(null));
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyString())).thenReturn(successFuture(null));

        CompletableFuture<DatabaseResponse> res = friends.updateRequest(true, other_uuid);

        verify(mockDatabase).removeArrayField(USERS_COLLECTION_KEY + uuid, AWAITING_REQUESTS_KEY, other_uuid);
        verify(mockDatabase).removeArrayField(USERS_COLLECTION_KEY + other_uuid, PENDING_REQUESTS_KEY, uuid);
        verify(mockDatabase).updateArrayField(USERS_COLLECTION_KEY + uuid, FRIENDS_KEY, other_uuid);
        verify(mockDatabase).updateArrayField(USERS_COLLECTION_KEY + other_uuid, FRIENDS_KEY, uuid);

        try {
            assertTrue(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void updateRequestFails() {
        when(mockDatabase.removeArrayField(anyString(), anyString(), anyString())).thenReturn(failedFuture(null));
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyString())).thenReturn(failedFuture(null));

        CompletableFuture<DatabaseResponse> res = friends.updateRequest(true, other_uuid);

        try {
            assertFalse(res.get().isSuccessful());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

}
