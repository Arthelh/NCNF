package com.ncnf.views.activities.user;

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
import com.ncnf.views.fragments.organization.OrganizationTabFragment;
import com.ncnf.views.fragments.user.UserProfileTabFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserTabActivity extends AppCompatActivity {

    public static ViewPager2 viewPager;
    private final String[] tabNames = new String[]{"User Profile", "Organization"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        viewPager = findViewById(R.id.user_view_pager);
        FragmentStateAdapter pagerAdapter = new ViewPagerFragmentAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.user_tabs);

        new TabLayoutMediator(tabLayout, viewPager,(tab, position) -> tab.setText(tabNames[position])).attach();
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new UserProfileTabFragment();
                case 1:
                    return new OrganizationTabFragment();
                default:
                    //TODO default case
                    throw new IllegalStateException();
            }
        }

        @Override
        public int getItemCount() {
            return tabNames.length;
        }
    }
}