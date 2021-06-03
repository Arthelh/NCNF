package com.ncnf.utilities;

import android.content.Context;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class InputValidatorTests {

    private final Context ctx = ApplicationProvider.getApplicationContext();

    private EditText editTextWith(String value, int type) {
        EditText input = new EditText(ctx);
        input.setInputType(type);
        input.setText(value, TextView.BufferType.NORMAL);
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

    @Test
    public void verifyThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> InputValidator.verifyGenericInput(null));
    }

    @Test
    public void verifyReturnsFalseOnEmptyString (){
        EditText input = editTextWith(null, InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        input.setText(null);
        assertFalse(InputValidator.verifyGenericInput(input));
    }

    @Test
    public void verifyGenericInputTestEveryInputType(){
        EditText input = editTextWith("test", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        assertTrue(!InputValidator.verifyGenericInput(input) || input.getError().equals("Please enter a correct email address"));

        input = editTextWith("test", InputType.TYPE_CLASS_PHONE);
        assertTrue(!InputValidator.verifyGenericInput(input) || input.getError().equals("Please enter a correct phone number"));

        input = editTextWith("test", InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        assertTrue(!InputValidator.verifyGenericInput(input) || input.getError().equals("Please enter a valid postal code"));

        input = editTextWith("test", InputType.TYPE_TEXT_VARIATION_URI);
        assertTrue(!InputValidator.verifyGenericInput(input) || input.getError().equals("Please enter a valid url"));

    }

    @Test
    public void ArrayIsInvalidWorks(){
        assertTrue(InputValidator.isInvalidArray(null) && InputValidator.isInvalidArray(new ArrayList()));
        List<Integer> list = new ArrayList<>();
        list.add(4);
        assertFalse(InputValidator.isInvalidArray(list));
    }

    @Test
    public void setErrorMsgWorksTest(){
        EditText editText = new EditText(ctx);
        InputValidator.setErrorMsg(editText, "error");
        assertTrue(editText.getError().equals("error"));

        Button button = new Button(ctx);
        InputValidator.setErrorMsg(button, "error");
        assertTrue(button.getError().equals("error"));
    }
}
