package com.ncnf.authentication;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import static com.ncnf.Utils.DEBUG_TAG;

public class AuthenticationService implements AuthenticationServiceInterface {

    private final FirebaseAuth auth;
    private final MutableLiveData<Exception> exceptionMutableLiveData;

    @Inject
    public AuthenticationService(){
        this.auth = FirebaseAuth.getInstance();
        this.exceptionMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Exception> register(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(DEBUG_TAG,"New user successfully authenticated");
                exceptionMutableLiveData.postValue(null);
            } else {
                Log.d(DEBUG_TAG,"Error authenticating new user " + task.getException().toString());
                exceptionMutableLiveData.postValue(task.getException());
            }
        });

        return exceptionMutableLiveData;
    }

    public MutableLiveData<Exception> signIn(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(DEBUG_TAG,"User successfully logged in");
                    exceptionMutableLiveData.postValue(null);
                } else {
                    Log.d(DEBUG_TAG,"Error connecting user " + task.getException().toString());
                    exceptionMutableLiveData.postValue(task.getException());
                }

            }
        });

        return exceptionMutableLiveData;
    }

}
