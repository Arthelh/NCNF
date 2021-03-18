package com.ncnf.authentication;

import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public interface AuthenticationServiceInterface {

    CompletableFuture<AuthenticationResponse> register(String email, String password);

    CompletableFuture<AuthenticationResponse> logIn(String email, String password);

    CompletableFuture<AuthenticationResponse> logOut(FirebaseUser user);
}
