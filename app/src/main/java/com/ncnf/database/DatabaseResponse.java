package com.ncnf.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import javax.annotation.Nullable;
import javax.xml.transform.Result;

public class DatabaseResponse {
    private final boolean isSuccessful;
    private final Object result;
    private final Exception exception;

    public DatabaseResponse(boolean isSuccessful, @Nullable Object result, Exception exception){
        this.isSuccessful = isSuccessful;
        this.result = result;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public Object getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
