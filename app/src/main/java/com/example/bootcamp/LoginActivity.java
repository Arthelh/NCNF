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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        intent = new Intent(this, UserProfileActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setProgressBar(View.INVISIBLE);
        ((EditText)findViewById(R.id.signInEmail)).setText("");
        ((EditText)findViewById(R.id.signInPassword)).setText("");
    }

    public void signIn(View view) {


        EditText emailField = findViewById(R.id.signInEmail);
        String email = emailField.getText().toString();


        EditText passwordField = findViewById(R.id.signInPassword);
        String password = passwordField.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            setException("One of the fields is empty");
            return;
        }

        setProgressBar(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(MainActivity.TAG,"User successfully connected");
                    setProgressBar(View.INVISIBLE);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(MainActivity.TAG,"Error connecting user " + task.getException().toString());
                    setException(task.getException().getMessage());
                    setProgressBar(View.INVISIBLE);
                    return;
                }

            }
        });
    }

    private void setProgressBar(int visibility){
        ProgressBar bar = findViewById(R.id.progressBar2);
        bar.setVisibility(visibility);
    }

    private void setException(String s){
        TextView exception = findViewById(R.id.exceptionSignIn);
        exception.setText(s);
        return;
    }
}