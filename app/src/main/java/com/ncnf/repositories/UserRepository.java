package com.ncnf.repositories;

import com.ncnf.database.firebase.DatabaseService;
import com.ncnf.models.User;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;


public class UserRepository {

    public DatabaseService db;

    public UserRepository() {
        this.db = new DatabaseService();
    }

    @Inject
    public UserRepository(DatabaseService db) {
        this.db = db;
    }

    public CompletableFuture<User> loadUser(String uuid){
        return this.db.getDocument(USERS_COLLECTION_KEY + uuid, User.class);
    }
}
