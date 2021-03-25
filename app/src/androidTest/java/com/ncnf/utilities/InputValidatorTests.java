package com.ncnf.utilities;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputValidatorTests {

    private final Context ctx = ApplicationProvider.getApplicationContext();

    private EditText editTextWith(String value, int type) {
        EditText input = new EditText(ctx);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setText("foo@bar.com", TextView.BufferType.NORMAL);
        return input;
    }

    @Test
    public void verifyGenericInputRejectsEmptyField() {
        EditText input = new EditText(ctx);

        assertFalse(InputValidator.verifyGenericInput(input));
    }

    @Test
    public void verifyGenericInputForEmails() {
        EditText input = editTextWith("foo@bar.com", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        assertTrue(InputValidator.verifyGenericInput(input));
    }

    @Test
    public void verifyGenericInputForPhoneNumbers() {
        EditText input = editTextWith("0771112233", InputType.TYPE_CLASS_PHONE);

        assertTrue(InputValidator.verifyGenericInput(input));
    }

    @Test
    public void verifyGenericInputForWebURL() {
        EditText input = editTextWith("foo.com", InputType.TYPE_TEXT_VARIATION_URI);

        assertTrue(InputValidator.verifyGenericInput(input));
    }

    @Test
    public void verifyGenericInputForPostalCodes() {
        EditText input = editTextWith("1627", InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);

        assertTrue(InputValidator.verifyGenericInput(input));
    }

}
