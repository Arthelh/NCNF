package com.ncnf.database;

import javax.annotation.Nullable;

public class DatabaseResponse<T> {
    private final boolean isSuccessful;
    private final T result;
    private final Exception exception;

    public DatabaseResponse(boolean isSuccessful, @Nullable T result, Exception exception){
        this.isSuccessful = isSuccessful;
        this.result = result;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public T getResult() {
        return result;
    }

    public T getResult(Class type) {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
