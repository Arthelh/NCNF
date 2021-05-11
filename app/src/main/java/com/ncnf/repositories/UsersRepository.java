package com.ncnf.repositories;

import com.ncnf.database.DatabaseService;
import com.ncnf.user.User;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.Utils.USERS_COLLECTION_KEY;

public class UsersRepository {

    @Inject
    public DatabaseService db;

    @Inject
    public UsersRepository(){}

    public CompletableFuture<User> loadUser(String uuid){
        return this.db.getDocument(USERS_COLLECTION_KEY + uuid, User.class);
    }
}