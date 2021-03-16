package com.example.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import static com.example.bootcamp.Utils.*;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.bootcamp.EXTRA_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Switch s = findViewById(R.id.connected);
        Button profile = findViewById(R.id.mainProfileButton);
        Button signIn = findViewById(R.id.mainSignInButton);
        Button signUp = findViewById(R.id.mainSignUpButton);

        if(isConnected()){
            profile.setEnabled(true);
            signIn.setEnabled(false);
            signUp.setEnabled(false);
            s.setText(CONNECTED_KEYWORD);
            s.setChecked(true);
        } else {
            profile.setEnabled(false);
            signIn.setEnabled(true);
            signUp.setEnabled(true);
            s.setText(DISCONNECTED_KEYWORD);
            s.setChecked(false);
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void gotToProfile(View view){
        if(!isConnected()){
            return;
        }
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    private boolean isConnected(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }



}