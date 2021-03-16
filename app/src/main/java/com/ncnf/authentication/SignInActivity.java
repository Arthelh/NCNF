package com.ncnf.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.ncnf.user.UserProfileActivity;

import static com.ncnf.Utils.*;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        auth = FirebaseAuth.getInstance();
        intent = new Intent(this, UserProfileActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setProgressBar(View.INVISIBLE);
        ((EditText)findViewById(R.id.signInEmail)).getText().clear();
        ((EditText)findViewById(R.id.signInPassword)).getText().clear();
    }

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

        setProgressBar(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(DEBUG_TAG,"User successfully connected");
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(DEBUG_TAG,"Error connecting user " + task.getException().toString());
                    /*
                    TODO: Match exception to check if user exists or not
                     */
                }

            }
        });
        setProgressBar(View.INVISIBLE);
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