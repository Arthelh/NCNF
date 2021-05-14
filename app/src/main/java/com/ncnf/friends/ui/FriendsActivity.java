package com.ncnf.friends.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
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

        // Setup fragment selector
        FragmentSelector fragmentSelector = new FragmentSelector(this);
        ViewPager2 viewPager = findViewById(R.id.friends_view_pager);
        viewPager.setAdapter(fragmentSelector);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                fragmentSelector.notifyDataSetChanged();
            }
        });


        TabLayout tabs = findViewById(R.id.friends_tabs);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(fragmentSelector.getPageTitle(position))
        ).attach();
    }

    private static class FragmentSelector extends FragmentStateAdapter {

        private final String[] TAB_TITLES = new String[]{"Friends", "My requests"};
        private final int itemCount = TAB_TITLES.length;

        public FragmentSelector(FragmentActivity activity) {
            super(activity);
        }

        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position) {
                case 0: return new FriendsFragment();
                case 1:
                default: return new FriendsRequestsFragment();
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