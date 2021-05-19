package com.ncnf.authentication.firebase;

import java.util.concurrent.CompletableFuture;

public interface AuthenticationServiceInterface {

    CompletableFuture<Boolean> register(String email, String password);

    CompletableFuture<Boolean> logIn(String email, String password);

    void logOut();

    CompletableFuture<Boolean> delete();
}
