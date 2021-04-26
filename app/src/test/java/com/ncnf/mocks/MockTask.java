package com.ncnf.mocks;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class MockTask<D> extends Task<D> {

    private final D result;
    private final Exception exception;
    private final boolean isSuccessful;

    public MockTask(D result, Exception exception) {
        this.isSuccessful = true;
        this.result = result;
        this.exception = exception;
    }

    public MockTask(D result, Exception exception, boolean isSuccessful) {
        this.result = result;
        this.exception = exception;
        this.isSuccessful = isSuccessful;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean isSuccessful() {
        return isSuccessful;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Nullable
    @Override
    public D getResult() {
        return result;
    }

    @Nullable
    @Override
    public Exception getException() {
        return exception;
    }

    @NonNull
    public Task<D> addOnCompleteListener(@NonNull OnCompleteListener<D> var1) {
        var1.onComplete(this);
        return this;
    }

    // UNSUPPORTED OPERATIONS

    @NonNull
    @Override
    public Task addOnSuccessListener(@NonNull OnSuccessListener onSuccessListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener onSuccessListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener onSuccessListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public D getResult(@NonNull Class aClass) throws Throwable {
        throw new UnsupportedOperationException();
    }
}
