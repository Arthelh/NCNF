package com.ncnf.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ncnf.R;
import com.ncnf.event.EventDB;
import com.ncnf.feed.ui.FeedFragment;
import com.ncnf.home.ui.HomeFragment;
import com.ncnf.map.ui.MapFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationBar;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment homeFragment;
    private Fragment mapFragment;
    private Fragment feedFragment;

    private Fragment activeFragment;

    private final String HOME_FRAGMENT = "home_fragment";
    private final String MAP_FRAGMENT = "map_fragment";
    private final String FEED_FRAGMENT = "feed_fragment";
    private final String ACTIVE_FRAGMENT = "active_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        EventDB eventDB = new EventDB();

        if(savedInstanceState == null){
            this.homeFragment = new HomeFragment();
            this.feedFragment = new FeedFragment(eventDB);
            this.mapFragment = new MapFragment();
            this.activeFragment = this.homeFragment;
        }

        fragmentManager.beginTransaction().add(R.id.mainFragmentContainerView, feedFragment).hide(feedFragment).commit();
        fragmentManager.beginTransaction().add(R.id.mainFragmentContainerView, mapFragment).hide(mapFragment).commit();
        fragmentManager.beginTransaction().add(R.id.mainFragmentContainerView, homeFragment).hide(homeFragment).commit();

        fragmentManager.beginTransaction().show(activeFragment).commit();

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
            default:
                return false;
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        fragmentManager.putFragment(outState, HOME_FRAGMENT, homeFragment);
        fragmentManager.putFragment(outState, MAP_FRAGMENT, mapFragment);
        fragmentManager.putFragment(outState, FEED_FRAGMENT, feedFragment);
        fragmentManager.putFragment(outState, ACTIVE_FRAGMENT, activeFragment);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.homeFragment = fragmentManager.getFragment(savedInstanceState, HOME_FRAGMENT);
        this.mapFragment = fragmentManager.getFragment(savedInstanceState, MAP_FRAGMENT);
        this.feedFragment = fragmentManager.getFragment(savedInstanceState, FEED_FRAGMENT);
        this.activeFragment = fragmentManager.getFragment(savedInstanceState, ACTIVE_FRAGMENT);
    }
}