//package com.ncnf.user;
//
//import com.ncnf.database.DatabaseResponse;
//import com.ncnf.database.DatabaseService;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//import static com.ncnf.Utils.AWAITING_REQUESTS_KEY;
//import static com.ncnf.Utils.FRIENDS_KEY;
//import static com.ncnf.Utils.PENDING_REQUESTS_KEY;
//import static com.ncnf.Utils.USERS_COLLECTION_KEY;
//
///*
//    Interactions with the database to handle friends
//
//    collection: /users
//    fields:
//    - friends => Array<String> uuid: array of friends uuid
//    - pending_requests => Array<String> uuid: array of users uuid to which the user sent a request
//    - awaiting_requests => Array<String> uuid: array of users uuid requesting the user
// */
//public class Friends {
//
//    /*
//        TODO: Some requests needs to be written with a transaction in case of failure.
//     */
//
//    private final DatabaseService db;
//    private final String uuid;
//    private final String path;
//
//    Friends(DatabaseService db, String uuid) {
//        this.db = db;
//        this.uuid = uuid;
//        this.path = USERS_COLLECTION_KEY + uuid;
//    }
//
//    /*
//        Friend request to another user (identified by its ID)
//     */
//    public CompletableFuture<Boolean> request(String other_uuid) {
//        // Add a pending request to the user
//        CompletableFuture<Boolean> u1 = db.updateArrayField(this.path, PENDING_REQUESTS_KEY, other_uuid);
//        // Add a awaiting request to the other user
//        CompletableFuture<Boolean> u2 = db.updateArrayField(USERS_COLLECTION_KEY + other_uuid, AWAITING_REQUESTS_KEY, this.uuid);
//        return combine(u1, u2);
//    }
//
//    /*
//        Friend requests that are waiting for approval from the user
//     */
//    public CompletableFuture<List<User>> awaitingRequests() {
//        return requests(AWAITING_REQUESTS_KEY);
//    }
//    /*
//        Friend requests sent by the user and still pending
//     */
//    public CompletableFuture<List<User>> pendingRequests() {
//        return requests(User.g);
//    }
//
//    /*
//        Fetch a list of users from a list of uuid in the given user field (key)
//     */
//    private CompletableFuture<List<User>> requests(List<String> ids) {
//        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();
//
//        this.db.whereIn()
//
//        db.getField(this.path, field, List.class).thenAccept(res -> {
//            if (res.isSuccessful()) {
//                List<String> ids = (List<String>) res.getResult();
//                db.whereEqualTo(USERS_COLLECTION_KEY, "uuid", ids, User.class).thenAccept(futureResponse::complete);
//            } else {
//                futureResponse.completeExceptionally();
//            }
//        });
//
//        return futureResponse;
//    }
//
//    /*
//        Accept or decline a friend request
//     */
//    public <T> CompletableFuture<Boolean> updateRequest(boolean accept, String other_uuid) {
//        // remove the awaiting request of the user
//        CompletableFuture<Boolean> u1 = db.removeArrayField(this.path, AWAITING_REQUESTS_KEY, other_uuid);
//        // remove the pending request of the other user
//        CompletableFuture<Boolean> u2 = db.removeArrayField(USERS_COLLECTION_KEY + other_uuid, PENDING_REQUESTS_KEY, this.uuid);
//        // add the new friend to both users
//        CompletableFuture<Boolean> u3 = db.updateArrayField(this.path, FRIENDS_KEY, other_uuid);
//        CompletableFuture<Boolean> u4 = db.updateArrayField(USERS_COLLECTION_KEY + other_uuid, FRIENDS_KEY, uuid);
//
//        return combine(combine(combine(u1, u2), u3), u4);
//    }
//
//    private CompletableFuture<Boolean> combine(CompletableFuture<Boolean> u1, CompletableFuture<Boolean> u2) {
//        return u1.thenCombine(u2, (v1, v2) -> v1 && v2)
//                .exceptionally(exception -> false);
//    }
//
//}
