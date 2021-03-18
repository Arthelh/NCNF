package com.ncnf.authentication;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.Utils.DEBUG_TAG;

public class AuthenticationService implements AuthenticationServiceInterface {

    private final FirebaseAuth auth;
    private AuthenticationResponse response;

    @Inject
    public AuthenticationService(){
        this.auth = FirebaseAuth.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CompletableFuture<AuthenticationResponse> register(String email, String password){
        CompletableFuture<AuthenticationResponse> futureResponse = new CompletableFuture<>();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            try {
                futureResponse.complete(new AuthenticationResponse(task.isSuccessful(), task.getResult(), task.getException()));
            } catch (Exception e){
                futureResponse.complete(new AuthenticationResponse(task.isSuccessful(), null, task.getException()));
                /*
                    TODO: Match exception to check if user exists or not
                */
            }
        });

        return futureResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CompletableFuture<AuthenticationResponse> logIn(String email, String password) {
        CompletableFuture<AuthenticationResponse> futureResponse = new CompletableFuture<>();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            try{
                futureResponse.complete(new AuthenticationResponse(task.isSuccessful(), task.getResult(), task.getException()));
            } catch (Exception e) {
                futureResponse.complete(new AuthenticationResponse(task.isSuccessful(), null, task.getException()));
                /*
                    TODO: Match exception to check if user exists or not
                */
            }
        });

        return futureResponse;
    }

    @Override
    public CompletableFuture<AuthenticationResponse> logOut(FirebaseUser user) {
        return null;
    }

}
