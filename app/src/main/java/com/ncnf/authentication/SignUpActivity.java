package com.ncnf.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.user.PrivateUser;
import com.ncnf.user.UserProfileActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.Utils.EMPTY_FIELD_STRING;
import static com.ncnf.Utils.EMPTY_STRING;
import static com.ncnf.Utils.INVALID_PASSWORD_STRING;
import static com.ncnf.Utils.PASSWORDS_DO_NOT_MATCH_STRING;
import static com.ncnf.Utils.isValidEmail;
import static com.ncnf.Utils.isValidPassword;

@AndroidEntryPoint
public class SignUpActivity extends AppCompatActivity {

    private Intent intent;

    @Inject
    AuthenticationService auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.intent = new Intent(this, UserProfileActivity.class);
        this.auth = new AuthenticationService();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setProgressBar(View.INVISIBLE);
        setFieldsEmpty();
    }


    public void signUp(View view) {
        String email = getFieldText(R.id.signUpEmail);
        String password = getFieldText(R.id.signUpPassword);
        String confirmed = getFieldText(R.id.signUpConfirmPassword);

        setFieldsEmpty();

        if(email.isEmpty() || password.isEmpty() || confirmed.isEmpty()){
            setException(EMPTY_FIELD_STRING);
            return;
        } else if(!password.equals(confirmed)){
            setException(PASSWORDS_DO_NOT_MATCH_STRING);
            return;
        } else if (!isValidEmail(email)){
            setException(BADLY_FORMATTED_EMAIL_STRING);
            return;
        } else if (!isValidPassword(password)){
            setException(INVALID_PASSWORD_STRING);
            return;
        }

        setProgressBar(View.VISIBLE);

        auth.register(email, password).observe(this, exception-> {
            if(exception == null){
                PrivateUser user = PrivateUser.getInstance();
                user.createDBUser(o -> {
                    startActivity(intent);
                    finish();
                }, e -> {
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                    setException("Couldn't create a new user : please try again");
                });
            } else {
                setException(exception.getMessage());
                /*
                    TODO: Match exception to check if user exists or not
                */
            }
            setProgressBar(View.INVISIBLE);
        });
    }

    private void setFieldsEmpty(){
        ((EditText)findViewById(R.id.signUpEmail)).setText(EMPTY_STRING);
        ((EditText)findViewById(R.id.signUpPassword)).setText(EMPTY_STRING);
        ((EditText)findViewById(R.id.signUpConfirmPassword)).setText(EMPTY_STRING);
    }

    private String getFieldText(int id){
        return ((EditText)findViewById(id)).getText().toString();
    }

    private void setProgressBar(int visibility){
        ProgressBar bar = findViewById(R.id.progressBar);
        bar.setVisibility(visibility);
    }

    private void setException(String s){
        TextView exception = findViewById(R.id.exceptionSignUp);
        exception.setText(s);
    }
}