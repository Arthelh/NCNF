package com.ncnf.authentication;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.Utils.DEBUG_TAG;

public class AuthenticationService implements AuthenticationServiceInterface {

    private FirebaseAuth auth;

    @Inject
    public AuthenticationService(){
        this(true);
    }

    protected AuthenticationService(boolean getAuth){
        if(getAuth){
            this.auth = FirebaseAuth.getInstance();
        }
    }

    @Override
    public CompletableFuture<Boolean> register(String email, String password){
        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> onTaskComplete(task, futureResponse));

        return futureResponse;
    }

    @Override
    public CompletableFuture<Boolean> logIn(String email, String password) {
        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> onTaskComplete(task, futureResponse));

        return futureResponse;
    }

    @Override
    public void logOut() {
        this.auth.signOut();
    }

    /**
     * TODO : should we monitor fail cases or what ?
     * @return
     */
    @Override
    public CompletableFuture<Boolean> delete() {
        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();

        this.auth.getCurrentUser().delete().addOnCompleteListener(task -> onTaskComplete(task, futureResponse));

        return futureResponse;
    }

    private void onTaskComplete(Task<?> task, CompletableFuture<Boolean> futureResponse){
        if(task.isSuccessful()){
            futureResponse.complete(true);
        } else {
            futureResponse.completeExceptionally(task.getException());
        }
    }

    protected void setAuth(FirebaseAuth auth){
        this.auth = auth;
    }

}
