package com.ncnf.views.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ncnf.R;
import com.ncnf.views.fragments.feed.FeedFragment;
import com.ncnf.views.fragments.home.HomeFragment;
import com.ncnf.views.fragments.map.MapFragment;
import com.ncnf.views.activities.settings.SettingsActivity;

import java.util.Objects;

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

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        if(savedInstanceState == null){
            this.homeFragment = new HomeFragment();
            this.feedFragment = new FeedFragment();
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
        Fragment currentFragment = activeFragment;
        switch (item.getItemId()){
            case R.id.navigation_home:
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                break;

            case R.id.navigation_map:
                fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit();
                activeFragment = mapFragment;
                break;

            case R.id.navigation_feed:
                fragmentManager.beginTransaction().hide(activeFragment).show(feedFragment).commit();
                activeFragment = feedFragment;
                break;
            default:
                return false;
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentManager.beginTransaction().hide(activeFragment).show(currentFragment).commit();
                activeFragment = currentFragment;
                this.setEnabled(false);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        return true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}