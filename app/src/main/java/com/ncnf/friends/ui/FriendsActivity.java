package com.ncnf.friends.ui;

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

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        FragmentSelector fragmentSelector = new FragmentSelector(this);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(fragmentSelector);

        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(fragmentSelector.getPageTitle(position))
        ).attach();
    }

    private static class FragmentSelector extends FragmentStateAdapter {

        private final String[] TAB_TITLES = new String[]{"Friends", "Add a friend"};
        private final int itemCount = 2;

        public FragmentSelector(FragmentActivity activity) {
            super(activity);
        }

        //By default, we will return saved events if an error occurs

        public CharSequence getPageTitle(int position) {
            if(position == 1){
                return TAB_TITLES[position];
            }
            return TAB_TITLES[0];
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 1){
                return new AddFriendFragment();
            }
            return new FriendsFragment();
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }
    }

}