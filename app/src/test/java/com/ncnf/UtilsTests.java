package com.ncnf;

import android.text.TextUtils;
import android.util.Patterns;

import com.ncnf.authentication.AuthenticationService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import static com.ncnf.Utils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsTests {

    String invalidEmail = "test";
    String validEmail = "test@test.com";
    String invalidPassword = "test";
    String validPassword = "test12";

    @Test
    public void emptyStringTest(){
        assertTrue(stringIsEmpty(""));
    }

    @Test
    public void emptyEmailTest(){
        assertFalse(isValidEmail(""));
    }

    @Test
    public void invalidEmailTest(){
        assertFalse(isValidEmail(invalidEmail));
    }

    @Test
    public void validEmailTest(){
        assertTrue(isValidEmail(validEmail));
    }

    @Test
    public void emptyPasswordTest(){
        assertFalse(isValidPassword(""));
    }

    @Test
    public void invalidPasswordTest(){
        assertFalse(isValidPassword(invalidPassword));
    }

    @Test
    public void validPasswordTest(){
        assertTrue(isValidPassword(validPassword));
    }
}
