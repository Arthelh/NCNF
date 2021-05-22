package com.ncnf.authentication;

import java.util.concurrent.CompletableFuture;

public interface Authentication {

    /**
     * Register a new User with an email and a password
     * @param email the email of the new User
     * @param password the password of the new User
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    CompletableFuture<Boolean> register(String email, String password);

    /**
     * Login an existing User with its email and password
     * @param email the email of the existing User
     * @param password the password of the existing User
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    CompletableFuture<Boolean> logIn(String email, String password);

    /**
     * Logout the currently logged User
     */
    void logOut();

    /**
     * Deletes the currently logged User from the Authentication system
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    CompletableFuture<Boolean> delete();

    /**
     * Changes the email of the currently logged User
     * @param email the new email to use
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    CompletableFuture<Boolean> changeEmail(String email);
}
