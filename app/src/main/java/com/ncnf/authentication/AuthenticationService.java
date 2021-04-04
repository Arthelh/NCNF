package com.ncnf.authentication;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CompletableFuture<AuthenticationResponse> register(String email, String password){
        CompletableFuture<AuthenticationResponse> futureResponse = new CompletableFuture<>();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> onTaskComplete(task, futureResponse));

        return futureResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CompletableFuture<AuthenticationResponse> logIn(String email, String password) {
        CompletableFuture<AuthenticationResponse> futureResponse = new CompletableFuture<>();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> onTaskComplete(task, futureResponse));

        return futureResponse;
    }

    @Override
    public CompletableFuture<AuthenticationResponse> logOut(FirebaseUser user) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onTaskComplete(Task<AuthResult> task, CompletableFuture<AuthenticationResponse> futureResponse){
        try {
            futureResponse.complete(new AuthenticationResponse(task.isSuccessful(), task.getResult(), task.getException()));
        } catch (Exception e){
            futureResponse.complete(new AuthenticationResponse(task.isSuccessful(), null, task.getException()));
                /*
                    TODO: Match exception to check if user exists or not
                */
        }
    }

    protected void setAuth(FirebaseAuth auth){
        this.auth = auth;
    }

}
