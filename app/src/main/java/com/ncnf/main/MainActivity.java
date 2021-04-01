package com.ncnf.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.authentication.ui.LoginActivity;
import com.ncnf.authentication.ui.SignInFragment;
import com.ncnf.authentication.ui.SignUpFragment;
import com.ncnf.event.EventActivity;
import com.ncnf.event.create.EventCreateActivity;
import com.ncnf.feed.ui.FeedActivity;
import com.ncnf.feed.ui.FeedFragment;
import com.ncnf.home.ui.HomeFragment;
import com.ncnf.map.MapActivity;
import com.ncnf.map.ui.MapFragment;
import com.ncnf.user.UserProfileActivity;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.*;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragmentContainerView, new FeedFragment())
                    .commit();
        }

        navigationBar = findViewById(R.id.mainNavigationBar);

        navigationBar.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.navigation_feed){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragmentContainerView, new FeedFragment())
                        .commit();

            } else if(item.getItemId() == R.id.navigation_map) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragmentContainerView, new MapFragment())
                        .commit();

            } else if(item.getItemId() == R.id.navigation_home){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragmentContainerView, new HomeFragment())
                        .commit();                }

            return true;
        });
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        Switch s = findViewById(R.id.connected);
//
//        if(isConnected()){
//            s.setText(CONNECTED_KEYWORD);
//            s.setChecked(true);
//        } else {
//            s.setText(DISCONNECTED_KEYWORD);
//            s.setChecked(false);
//        }
//
//        //RecyclerView r = (RecyclerView)findViewById(R.id.recycler_view);
//
//        Button launchFeed = (Button) findViewById(R.id.feedViewButton);
//        Button launchEventCreation = (Button) findViewById(R.id.create_event_button);
//
//        launchFeed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                feedView(view);
//            }
//        });
//
//        launchEventCreation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                eventCreationView();
//            }
//        });
//
//    }
//
//    public void goToMap(View view){
//        Intent intent = new Intent(this, MapActivity.class);
//        startActivity(intent);
//    }
//
//    public void feedView(View view) {
//        Intent intent = new Intent(this, FeedActivity.class);
//        startActivity(intent);
//    }
//
//
    public void gotToProfile(){
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
//
//
//    public void seeEvent1(View view) {
//        Intent intent = new Intent(this, EventActivity.class);
//        intent.putExtra("EVENT_NUM", 0);
//        startActivity(intent);
//    }
//
//    public void seeEvent2(View view) {
//        Intent intent = new Intent(this, EventActivity.class);
//        intent.putExtra("EVENT_NUM", 1);
//        startActivity(intent);
//    }
//
//    public void eventCreationView(){
//        Intent intent = new Intent(this, EventCreateActivity.class);
//        startActivity(intent);
//    }

}