package com.ncnf.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.authentication.SignInActivity;
import com.ncnf.authentication.SignUpActivity;
import com.ncnf.feed.FeedActivity;
import com.ncnf.map.MapActivity;
import com.ncnf.user.UserProfileActivity;

import static com.NCNF.Utils.CONNECTED_KEYWORD;
import static com.NCNF.Utils.DISCONNECTED_KEYWORD;

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

    public void goToMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void feedView() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
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