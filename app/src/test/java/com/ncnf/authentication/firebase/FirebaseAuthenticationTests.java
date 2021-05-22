package com.ncnf.authentication.firebase;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FirebaseAuthenticationTests {

    FirebaseAuth mockedAuth;

    Task<AuthResult> failureTask;
    Task<AuthResult> successTask;
    FirebaseAuthentication service;

    String email = "test@test.com";
    String password = "test1234";


    @Before
    public void setup(){
        mockedAuth = mock(FirebaseAuth.class);

        service = new FirebaseAuthentication(false);
        service.setAuth(mockedAuth);

        failureTask = new Task<AuthResult>() {
            @Override
            public boolean isComplete() {
                return false;
            }

            @Override
            public boolean isSuccessful() {
                return false;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public AuthResult getResult() {
                return null;
            }

            @Nullable
            @Override
            public <X extends Throwable> AuthResult getResult(@NonNull Class<X> aClass) throws X {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                return new IllegalArgumentException();
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnCompleteListener(@NonNull OnCompleteListener<AuthResult> onCompleteListener) {
                onCompleteListener.onComplete(failureTask);
                return failureTask;
            }
        };

        successTask = new Task<AuthResult>() {

            @Override
            public boolean isComplete() {
                return false;
            }

            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public AuthResult getResult() {
                return null;
            }

            @Nullable
            @Override
            public <X extends Throwable> AuthResult getResult(@NonNull Class<X> aClass) throws X {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnCompleteListener(@NonNull OnCompleteListener<AuthResult> onCompleteListener) {
                onCompleteListener.onComplete(successTask);
                return successTask;
            }
        };
    }

    @Test
    public void registerSuccessTest() throws ExecutionException, InterruptedException {
        when(mockedAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask);

        boolean registered = service.register(email, password).get();

        verify(mockedAuth).createUserWithEmailAndPassword(anyString(), anyString());

        assertTrue(registered);
    }

    @Test
    public void registerFailureTest() throws ExecutionException, InterruptedException {
        when(mockedAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(failureTask);

        CompletableFuture<Boolean> registered = service.register(email, password);
        try{
            assertTrue(registered.isCompletedExceptionally());
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }

        verify(mockedAuth).createUserWithEmailAndPassword(anyString(), anyString());
    }

    @Test
    public void loginSuccessTest() throws ExecutionException, InterruptedException {
        when(mockedAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask);

        boolean logged = service.logIn(email, password).get();

        verify(mockedAuth).signInWithEmailAndPassword(anyString(), anyString());

        assertTrue(logged);
    }

    @Test
    public void loginFailureTest() throws ExecutionException, InterruptedException {
        when(mockedAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(failureTask);

        CompletableFuture<Boolean> logged = service.logIn(email, password);
        try{
            assertTrue(logged.isCompletedExceptionally());
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }

        verify(mockedAuth).signInWithEmailAndPassword(anyString(), anyString());

    }
}
