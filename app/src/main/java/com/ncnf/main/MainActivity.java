package com.ncnf.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ncnf.R;
import com.ncnf.feed.ui.FeedFragment;
import com.ncnf.home.ui.HomeFragment;
import com.ncnf.map.ui.MapFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragmentContainerView, new HomeFragment())
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
}