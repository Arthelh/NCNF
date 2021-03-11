package com.example.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.bootcamp.EXTRA_MESSAGE";
    public static final String TAG = "debugging";



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

        if(isConnected()){
            profile.setEnabled(true);
            s.setText("Connected");
            s.setChecked(true);
        } else {
            profile.setEnabled(false);
            s.setText("Disconnected");
            s.setChecked(false);
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void gotToProfile(View view){
        if(!isConnected()){
            return;
        }
        Log.d(MainActivity.TAG, "going to logged screen");
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    private boolean isConnected(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }



}