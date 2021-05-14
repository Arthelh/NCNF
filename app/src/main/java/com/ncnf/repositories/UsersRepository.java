package com.ncnf.repositories;

import com.ncnf.database.DatabaseService;
import com.ncnf.user.User;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;


public class UsersRepository {

    public DatabaseService db;

    public UsersRepository() {
        this.db = new DatabaseService();
    }

    @Inject
    public UsersRepository(DatabaseService db) {
        this.db = db;
    }

    public CompletableFuture<User> loadUser(String uuid){
        return this.db.getDocument(USERS_COLLECTION_KEY + uuid, User.class);
    }
}
