package com.ncnf;

import org.junit.Test;

import static com.ncnf.Utils.isValidEmail;
import static com.ncnf.Utils.isValidPassword;
import static com.ncnf.Utils.stringIsEmpty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
