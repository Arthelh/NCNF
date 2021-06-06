package com.ncnf.views.activities.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ncnf.R;
import com.ncnf.views.fragments.login.SignInFragment;
import com.ncnf.views.fragments.login.SignUpFragment;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.NEXT_ACTIVITY_EXTRA_KEY;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private Class<?> nextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        // Get extras from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            this.nextActivity = (Class<?>)extras.get(NEXT_ACTIVITY_EXTRA_KEY);
        }

        // Setup fragment selector
        LoginActivity.FragmentSelector fragmentSelector = new LoginActivity.FragmentSelector(this, nextActivity);
        ViewPager2 viewPager = findViewById(R.id.loginViewPager);
        viewPager.setAdapter(fragmentSelector);
        
        TabLayout tabs = findViewById(R.id.loginTabs);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(fragmentSelector.getPageTitle(position))
        ).attach();
    }

    private static class FragmentSelector extends FragmentStateAdapter {

        private final String[] TAB_TITLES = new String[]{"Log In", "Register"};
        private final int itemCount = TAB_TITLES.length;
        private final Class<?> nextActivity;

        public FragmentSelector(FragmentActivity activity, Class<?> nextActivity) {
            super(activity);
            this.nextActivity = nextActivity;
        }

        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position) {
                case 0: return new SignInFragment(nextActivity);
                case 1:
                default: return new SignUpFragment(nextActivity);
            }
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public boolean containsItem(long itemId) {
            return super.containsItem(itemId);
        }
    }

}