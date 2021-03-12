package com.example.bootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private PrivateUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(MainActivity.TAG, "current user : " + this.auth.getCurrentUser().getUid().toString());
        user = new PrivateUser(this.auth.getCurrentUser());
        fillEmailAddress();
    }

    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        auth.signOut();

        startActivity(intent);
    }

    private void fillEmailAddress(){
        TextView text = findViewById(R.id.userProfileEmail);
        text.setEnabled(false);
        text.setText(this.auth.getCurrentUser().getEmail());
    }

//    private void fillFirstName(){
//        if(user.)
//        TextView text = findViewById(R.id.userProfileEmail);
//        text.setEnabled(false);
//        text.setText(this.auth.getCurrentUser().getEmail());
//    }
}