package com.ncnf.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.authentication.ui.LoginActivity;
import com.ncnf.feed.FeedActivity;
import com.ncnf.map.MapActivity;
import com.ncnf.user.UserProfileActivity;

import static com.ncnf.Utils.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Switch s = findViewById(R.id.connected);

        if(isConnected()){
            s.setText(CONNECTED_KEYWORD);
            s.setChecked(true);
        } else {
            s.setText(DISCONNECTED_KEYWORD);
            s.setChecked(false);
        }
    }

    public void goToMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void feedView(View view) {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

    public void gotToProfile(View view){
        Intent intent;

        if(!isConnected()){
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, UserProfileActivity.class);
        }

        startActivity(intent);
    }

    private boolean isConnected(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }



}