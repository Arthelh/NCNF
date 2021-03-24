package com.ncnf.authentication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.user.PrivateUser;
import com.ncnf.user.UserProfileActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EMPTY_FIELD_STRING;
import static com.ncnf.Utils.EMPTY_STRING;
import static com.ncnf.Utils.INVALID_PASSWORD_STRING;
import static com.ncnf.Utils.PASSWORDS_DO_NOT_MATCH_STRING;
import static com.ncnf.Utils.USERs_COLLECTION_KEY;
import static com.ncnf.Utils.isValidEmail;
import static com.ncnf.Utils.isValidPassword;

@AndroidEntryPoint
public class SignUpActivity extends AppCompatActivity {
    @Inject
    AuthenticationService auth;

    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.intent = new Intent(this, UserProfileActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setProgressBar(View.INVISIBLE);
        setFieldsEmpty();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        register(email, password);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void register(String email, String password){
        setProgressBar(View.VISIBLE);

        CompletableFuture<AuthenticationResponse> futureResponse= auth.register(email, password);

        futureResponse.thenAccept(response -> {
            if(response.isSuccessful()){
                Log.d(DEBUG_TAG, "Successful register for " + email);
                FirebaseUser fb = response.getResult().getUser();
                PrivateUser user = new PrivateUser(fb, USERs_COLLECTION_KEY + fb.getUid());
                user.saveUserToDB().thenAccept(dbResponse -> {
                    if (dbResponse.isSuccessful()) {
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(DEBUG_TAG, "Deleting user.");
                        fb.delete();
                        setException("Couldn't create a new user : please try again");
                    }
                });
            } else {
                Log.d(DEBUG_TAG,"Unsuccessful register for " + email + " : " + response.getException().getMessage());
                setException(response.getException().getMessage());
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