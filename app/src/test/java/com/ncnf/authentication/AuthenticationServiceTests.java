package com.ncnf.authentication;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTests {
    private AuthResult authResult;
    @Mock
    private AuthenticationService mockedAuth;
    private FirebaseAuth mockedFirebaseAuth;

    MutableLiveData<Exception> nullException = null;

    @Before
    public void setup(){
        mockedAuth = mock(AuthenticationService.class);
        mockedFirebaseAuth = mock(FirebaseAuth.class);
    }

    @Test
    public void noExceptionOnRegisterTest(){
        when(mockedAuth.register(anyString(), anyString())).thenReturn(nullException);
    }
}
