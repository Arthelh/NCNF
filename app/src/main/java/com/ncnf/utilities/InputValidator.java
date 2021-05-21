package com.ncnf.utilities;


import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.util.PatternsCompat;

import java.util.List;
import java.util.Objects;

public class InputValidator {

    static public boolean verifyEmailInput(String emailInputString) {
        return  emailInputString != null &&
                !emailInputString.isEmpty() &&
                PatternsCompat.EMAIL_ADDRESS.matcher(emailInputString).matches();
    }

    static public boolean verifyPhoneNumberInput(String phoneNumberInputString) {
        String phonePattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
        return  phoneNumberInputString != null &&
                !phoneNumberInputString.equals("") &&
                phoneNumberInputString.matches(phonePattern);
    }

    static public boolean verifyWebAddress(String webAddressInputString) {
        return  webAddressInputString != null &&
                !webAddressInputString.equals("") &&
                PatternsCompat.WEB_URL.matcher(webAddressInputString).matches();
    }

    static public boolean verifyPostalCode(String postalCodeInputString) {
        if (postalCodeInputString == null) return false;
        try {
            int code = Integer.parseInt(postalCodeInputString);
            return code > 0 && code < 10000;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static public boolean verifyGenericInput(EditText textField){
        if(textField == null) {
            throw new IllegalArgumentException("EditText passed as argument cannot be null");
        }
        if(textField.getText() == null) {
            return false;
        }

        String inputText = textField.getText().toString();
        int inputType = textField.getInputType();

        if(textField.length() == 0) {
            textField.setError("This field cannot be empty");
            return false;
        }

        switch(inputType) {
            case (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS):
                if (!verifyEmailInput(inputText)) textField.setError("Please enter a correct email address");
                break;
            case InputType.TYPE_CLASS_PHONE:
                if (!verifyPhoneNumberInput(inputText)) textField.setError("Please enter a correct phone number");
                break;
            case InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS: // Postal Address
                if (!verifyPostalCode(inputText)) textField.setError("Please enter a valid postal code");
                break;
            case (InputType.TYPE_TEXT_VARIATION_URI):
                if (!verifyWebAddress(inputText)) textField.setError("Please enter a valid url");
                break;
        }

        return textField.getError() == null;
    }

    static public void setErrorMsg(View v, String error){
        if(v instanceof Button){
            ((Button) v).setError(error);
        } else if(v instanceof EditText){
            ((EditText) v).setError(error);
        }
    }

    public static boolean isValidPassword(String password){
        return password.length() >= StringCodes.PASSWORD_MINIMUM_LENGTH;
    }

    public static boolean isInvalidString(String s){
        return s == null || s.length() == 0;
    }

    public static boolean isInvalidArray(List l){
        return l == null || l.isEmpty();
    }

    public static boolean checkCompleteList(List<String> l){
        return (l == null) || l.isEmpty() || l.stream().anyMatch(Objects::isNull);
    }
}
