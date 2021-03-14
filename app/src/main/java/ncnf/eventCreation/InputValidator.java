package ncnf.eventCreation;


import android.util.Patterns;
import android.widget.EditText;

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
                || (!Patterns.PHONE.matcher(phoneNumberInputString).matches())){
            phoneNumberInput.setError("Please enter right phone number");
        }


    }
}
