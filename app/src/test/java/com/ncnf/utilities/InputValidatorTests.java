package com.ncnf.utilities;

import org.junit.Test;

import static com.ncnf.utilities.InputValidator.isStringEmpty;
import static com.ncnf.utilities.InputValidator.isValidPassword;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputValidatorTests {

    String invalidEmail = "test";
    String validEmail = "test@test.com";
    String invalidPassword = "test";
    String validPassword = "test12";


    @Test
    public void verifyEmailInputRejects() {
        assertFalse(InputValidator.verifyEmailInput(null));
        assertFalse(InputValidator.verifyEmailInput(""));
        assertFalse(InputValidator.verifyEmailInput("foo"));
        assertFalse(InputValidator.verifyEmailInput("foo@bar"));
        assertFalse(InputValidator.verifyEmailInput("@bar.com"));
        assertFalse(InputValidator.verifyEmailInput("bar.com"));
    }

    @Test
    public void verifyEmailInputAccepts() {
        assertTrue(InputValidator.verifyEmailInput("foo@bar.com"));
        assertTrue(InputValidator.verifyEmailInput("foo@bar.co.uk"));
        assertTrue(InputValidator.verifyEmailInput("foo.bar@domain.com"));
        assertTrue(InputValidator.verifyEmailInput("foo-bar@domain.com"));
    }

    @Test
    public void verifyPhoneInputRejects() {
        assertFalse(InputValidator.verifyPhoneNumberInput(null));
        assertFalse(InputValidator.verifyPhoneNumberInput(""));
        assertFalse(InputValidator.verifyPhoneNumberInput("foo"));
        assertFalse(InputValidator.verifyPhoneNumberInput("079"));
    }

    @Test
    public void verifyPhoneInputAccepts() {
        assertTrue(InputValidator.verifyPhoneNumberInput("0791112233"));
        assertTrue(InputValidator.verifyPhoneNumberInput("+41791112233"));
    }

    @Test
    public void verifyWebAddressRejects() {
        assertFalse(InputValidator.verifyWebAddress(null));
        assertFalse(InputValidator.verifyWebAddress(""));
        assertFalse(InputValidator.verifyWebAddress("foo"));
    }

    @Test
    public void verifyWebAddressAccepts() {
        assertTrue(InputValidator.verifyWebAddress("foo.com"));
        assertTrue(InputValidator.verifyWebAddress("www.foo.com"));
        assertTrue(InputValidator.verifyWebAddress("https://foo.com"));
        assertTrue(InputValidator.verifyWebAddress("https://www.foo.com"));
        assertTrue(InputValidator.verifyWebAddress("sub.foo.com"));
        assertTrue(InputValidator.verifyWebAddress("foo.co.uk"));
    }

    @Test
    public void verifyPostalCodeRejects() {
        assertFalse(InputValidator.verifyPostalCode(null));
        assertFalse(InputValidator.verifyPostalCode(""));
        assertFalse(InputValidator.verifyPostalCode("foo"));
        assertFalse(InputValidator.verifyPostalCode("0"));
        assertFalse(InputValidator.verifyPostalCode("10000"));
    }

    @Test
    public void verifyPostalCodeAccepts() {
        assertTrue(InputValidator.verifyPostalCode("1000"));
    }

    @Test
    public void emptyStringTest(){
        assertTrue(isStringEmpty(""));
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
