package com.ncnf.user;

import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.AWAITING_REQUESTS_KEY;
import static com.ncnf.Utils.PENDING_REQUESTS_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;

public class Friends {

    private final DatabaseService db;
    private final String uuid;
    private final String path;

    Friends(DatabaseService db, String uuid) {
        this.db = db;
        this.uuid = uuid;
        this.path = USERS_COLLECTION_KEY + uuid;
    }

    public CompletableFuture<DatabaseResponse> request(String other_uuid) {
        // Add a pending request to the user
        CompletableFuture<DatabaseResponse> u1 = db.updateArrayField(this.path, PENDING_REQUESTS_KEY, other_uuid);
        // Add a awaiting request to the other user
        CompletableFuture<DatabaseResponse> u2 = db.updateArrayField(USERS_COLLECTION_KEY + other_uuid, AWAITING_REQUESTS_KEY, this.uuid);
        return u1.thenCombine(u2, (v1, v2) -> {
            Exception exception = v1.getException();
            if (v2.getException() != null) exception = v2.getException();
            return new DatabaseResponse(
                    v1.isSuccessful() && v2.isSuccessful(),
                    Arrays.asList(v1.getResult(), v2.getResult()),
                    exception
            );
        });
    }

    public CompletableFuture<DatabaseResponse> awaiting_requests() {
        CompletableFuture<DatabaseResponse> futureResponse = new CompletableFuture<>();

        db.getField(this.path, AWAITING_REQUESTS_KEY).thenAccept(res -> {
           if (res.isSuccessful()) {
               List<String> ids = (List<String>) res.getResult();
               db.whereEqualTo(USERS_COLLECTION_KEY, "uuid", ids).thenAccept(futureResponse::complete);
           } else {
               futureResponse.complete(new DatabaseResponse(false, null, null));
           }
        });

        return futureResponse;
    }
    
}
