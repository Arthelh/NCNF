package com.ncnf.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ncnf.R;
import com.ncnf.feed.ui.FeedFragment;
import com.ncnf.home.ui.HomeFragment;
import com.ncnf.map.ui.MapFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationBar;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment mapFragment = new MapFragment();
    private final Fragment feedFragment = new FeedFragment();

    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fragmentManager.beginTransaction().add(R.id.mainFragmentContainerView, feedFragment, "feed").hide(feedFragment).commit();
        fragmentManager.beginTransaction().add(R.id.mainFragmentContainerView, mapFragment, "map").hide(mapFragment).commit();
        fragmentManager.beginTransaction().add(R.id.mainFragmentContainerView, homeFragment, "home").commit();

        navigationBar = findViewById(R.id.mainNavigationBar);

        navigationBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public final  BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        switch (item.getItemId()){
            case R.id.navigation_home:
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;

            case R.id.navigation_map:
                fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit();
                activeFragment = mapFragment;
                return true;

            case R.id.navigation_feed:
                fragmentManager.beginTransaction().hide(activeFragment).show(feedFragment).commit();
                activeFragment = feedFragment;
                return true;
        }

        return false;
    };
}