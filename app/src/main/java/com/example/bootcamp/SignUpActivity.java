package com.example.bootcamp;

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

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Intent intent;
    private PrivateUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.auth = FirebaseAuth.getInstance();
        this.intent = new Intent(this, UserProfileActivity.class);
        setProgressBar(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setProgressBar(View.INVISIBLE);
        ((EditText)findViewById(R.id.signUpEmail)).setText("");
        ((EditText)findViewById(R.id.signUpPassword)).setText("");
        ((EditText)findViewById(R.id.signUpConfirmPassword)).setText("");
    }

    public void signUp(View view) {

        EditText emailField = findViewById(R.id.signUpEmail);
        String email = emailField.getText().toString();
        emailField.setText("");

        EditText passwordField = findViewById(R.id.signUpPassword);
        String password = passwordField.getText().toString();
        passwordField.setText("");

        EditText confirmField = findViewById(R.id.signUpConfirmPassword);
        String confirmed = confirmField.getText().toString();
        confirmField.setText("");

        if(email.isEmpty() || password.isEmpty() || confirmed.isEmpty()){
            setException("One of the fields is empty");
            return;
        } else if(!password.equals(confirmed)){
            setException("Password aren't equal");
            return;
        }

        setProgressBar(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(MainActivity.TAG,"User successfully created");
                    user = PrivateUser.getInstance();
                    user.createDBUser();
                    setProgressBar(View.INVISIBLE);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(MainActivity.TAG,"Error creating user " + task.getException().toString());
                    setException(task.getException().getMessage());
                    setProgressBar(View.INVISIBLE);
                }

            }
        });
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