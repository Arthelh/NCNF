package com.ncnf.authentication;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTests {

    @Mock
    FirebaseAuth mockedAuth;

    Task<AuthResult> failureTask;
    Task<AuthResult> successTask;
    AuthenticationService service;

    String email = "test@test.com";
    String password = "test1234";


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
//        mockedAuth = mock(FirebaseAuth.class);

        service = new AuthenticationService(false);
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
                return successTask;                       }
        };
    }

    @Test
    public void registerSuccessTest() throws ExecutionException, InterruptedException {
        when(mockedAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask);

        AuthenticationResponse res = service.register(email, password).get();
        assertTrue(res.isSuccessful());
        assertTrue(res.getResult()==null);
        assertTrue(res.getException()==null);
    }

    @Test
    public void registerFailureTest() throws ExecutionException, InterruptedException {
        when(mockedAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(failureTask);

        AuthenticationResponse res = service.register(email, password).get();
        assertFalse(res.isSuccessful());
        assertTrue(res.getResult()==null);
        assertTrue(res.getException()==null);
    }

    @Test
    public void loginSuccessTest() throws ExecutionException, InterruptedException {
        when(mockedAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask);

        AuthenticationResponse res = service.logIn(email, password).get();
        assertTrue(res.isSuccessful());
        assertTrue(res.getResult()==null);
        assertTrue(res.getException()==null);
    }

    @Test
    public void loginFailureTest() throws ExecutionException, InterruptedException {
        when(mockedAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(failureTask);

        AuthenticationResponse res = service.logIn(email, password).get();
        assertFalse(res.isSuccessful());
        assertTrue(res.getResult()==null);
        assertTrue(res.getException()==null);
    }
}
