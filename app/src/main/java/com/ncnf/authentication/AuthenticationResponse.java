package com.ncnf.authentication;

import com.google.firebase.auth.AuthResult;

import javax.annotation.Nullable;

public class AuthenticationResponse {

    private final boolean isSuccessful;
    private final AuthResult result;
    private final Exception exception;

    public AuthenticationResponse(boolean isSuccessful, @Nullable AuthResult result, Exception exception){
        this.isSuccessful = isSuccessful;
        this.result = result;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public AuthResult getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
