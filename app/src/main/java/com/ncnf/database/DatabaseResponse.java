package com.ncnf.database;

import javax.annotation.Nullable;

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
