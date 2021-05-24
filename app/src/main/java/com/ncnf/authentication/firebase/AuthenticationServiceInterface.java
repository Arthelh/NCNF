package com.ncnf.authentication.firebase;

import java.util.concurrent.CompletableFuture;

public interface AuthenticationServiceInterface {

    /**
     * Register a new user in Firebase Authentication system using the given email and password
     * and logs in the new user.
     * @param email New user's email
     * @param password New user's password
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    CompletableFuture<Boolean> register(String email, String password);

    /**
     * Log in an existing user using the given email and password.
     * @param email User's email
     * @param password User's password
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    CompletableFuture<Boolean> logIn(String email, String password);

    /**
     * Log out the current user.
     */
    void logOut();

    /**
     * Delete the current user.
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    CompletableFuture<Boolean> delete();
}
