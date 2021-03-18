package com.ncnf.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.user.PrivateUser;
import com.ncnf.user.UserProfileActivity;

import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.*;

public class SignInActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        intent = new Intent(this, UserProfileActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setProgressBar(View.INVISIBLE);
        ((EditText)findViewById(R.id.signInEmail)).getText().clear();
        ((EditText)findViewById(R.id.signInPassword)).getText().clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void signIn(View view) {
        EditText emailField = findViewById(R.id.signInEmail);
        String email = emailField.getText().toString();

        EditText passwordField = findViewById(R.id.signInPassword);
        String password = passwordField.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            setException(EMPTY_FIELD_STRING);
            return;
        } else if (!isValidEmail(email)){
            setException(BADLY_FORMATTED_EMAIL_STRING);
            return;
        }

        logIn(email, password);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void logIn(String email, String password){
        AuthenticationService auth = new AuthenticationService();

        setProgressBar(View.VISIBLE);

        CompletableFuture<AuthenticationResponse> futureResponse = auth.logIn(email, password);

        futureResponse.thenAccept(response -> {
            if(response.isSuccessful()){
                Log.d(DEBUG_TAG,"Successful login for " + email);
                startActivity(intent);
                finish();
            } else {
                Log.d(DEBUG_TAG,"Unsuccessful login for " + email + " : " + response.getException().getMessage());
                setException(response.getException().getMessage());
            }
            setProgressBar(View.INVISIBLE);
        });
    }

    private void setProgressBar(int visibility){
        ProgressBar bar = findViewById(R.id.progressBar2);
        bar.setVisibility(visibility);
    }

    private void setException(String s){
        TextView exception = findViewById(R.id.exceptionSignIn);
        exception.setText(s);
    }
}