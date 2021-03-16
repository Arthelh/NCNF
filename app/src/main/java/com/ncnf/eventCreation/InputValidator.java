package com.ncnf.eventCreation;


import android.util.Patterns;
import android.widget.EditText;

import java.util.regex.Pattern;

import static java.lang.Integer.bitCount;
import static java.lang.Integer.parseInt;

public class InputValidator {

    public void verifyEmailInput(String emailInputString, EditText emailInput) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (emailInputString == null || emailInputString.equals("")
                || !emailInputString.matches(emailPattern)
                || (!Patterns.EMAIL_ADDRESS.matcher(emailInputString).matches())){
            emailInput.setError("Please enter right email Address");
        }
    }

    public void verifyPhoneNumberInput(String phoneNumberInputString, EditText phoneNumberInput, String countryCode){
        String phonePattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";

        if (phoneNumberInputString == null || phoneNumberInputString.equals("")
                || !phoneNumberInputString.matches(phonePattern)
                || (!Patterns.PHONE.matcher(phoneNumberInputString).matches())) {
            phoneNumberInput.setError("Please enter right phone number");
        }
    }

    static public boolean verifyGenericInput(EditText textField){
        if(textField == null){
            throw new IllegalArgumentException("EditText passed as argument cannot be null");
        }

        if(textField.getText() == null){
            return false;
        }
        String inputText = textField.getText().toString();
        if(textField.length() == 0){
            textField.setError("This field cannot be empty");
            return false;
        }

        int inputType = textField.getInputType();
        boolean isCorrect;

        switch(inputType) {
            case 32:
                isCorrect = Patterns.EMAIL_ADDRESS.matcher(inputText).matches();
                break;
            case 3:
                isCorrect = Patterns.PHONE.matcher(inputText).matches();
                break;
            case 112: //Postal Address
                //TODO
                isCorrect = true;
                break;
            case 16:
                isCorrect = Patterns.WEB_URL.matcher(inputText).matches();
                break;
            default:
                isCorrect = true;
        }

        if(!isCorrect){
            textField.setError("Incorrect Format for selected field");
        }
        return isCorrect;
    }
}
