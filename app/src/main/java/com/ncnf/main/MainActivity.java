package com.ncnf.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private Fragment homeFragment;
    private Fragment mapFragment;
    private Fragment feedFragment;

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            this.homeFragment = new HomeFragment();
            this.mapFragment = new MapFragment();
            this.feedFragment = new FeedFragment();
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

        fragmentManager.putFragment(outState, "home_fragment", homeFragment);
        fragmentManager.putFragment(outState, "map_fragment", mapFragment);
        fragmentManager.putFragment(outState, "feed_fragment", feedFragment);
        fragmentManager.putFragment(outState, "active_fragment", activeFragment);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.homeFragment = fragmentManager.getFragment(savedInstanceState, "home_fragment");
        this.mapFragment = fragmentManager.getFragment(savedInstanceState, "map_fragment");
        this.feedFragment = fragmentManager.getFragment(savedInstanceState, "feed_fragment");
        this.activeFragment = fragmentManager.getFragment(savedInstanceState, "active_fragment");
    }
}