package com.ncnf.views.activities.organization;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.ncnf.views.fragments.organization.OrganizationEventsFragment;
import com.ncnf.views.fragments.organization.OrganizationTabFragment;
import com.ncnf.views.fragments.organization.OrganizationViewFragment;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrganizationProfileActivity extends AppCompatActivity {

    public static ViewPager2 viewPager;
    private final String[] tabNames = new String[]{"Profile", "Events"};
    private String uuid;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        this.uuid = intent.getStringExtra(OrganizationTabFragment.ORGANIZATION_UUID_KEY);

        setContentView(R.layout.fragment_organization_tabs);
        viewPager = findViewById(R.id.organization_view_pager);
        FragmentStateAdapter pagerAdapter = new ViewPagerFragmentAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.organization_tabs);

        new TabLayoutMediator(tabLayout, viewPager,(tab, position) -> tab.setText(tabNames[position])).attach();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
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
                    return createFragmentWithExtra( 0);
                case 1:
                    return createFragmentWithExtra(1);
                default:
                    //TODO default case
                    throw new IllegalStateException();
            }
        }

        private Fragment createFragmentWithExtra(int fragId) {
            Bundle bundle = getDataToTransfer();
            Fragment f;
            switch (fragId) {
                case 0:
                    f = new OrganizationViewFragment();
                    f.setArguments(bundle);
                    break;
                case 1:
                    f = new OrganizationEventsFragment();
                    f.setArguments(bundle);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid class type Id");
            }
            return f;
        }

        private Bundle getDataToTransfer(){
            Bundle bundle = new Bundle();
            bundle.putString("organization_id", uuid);
            return bundle;
        }

        @Override
        public int getItemCount() {
            return tabNames.length;
        }
    }
}
