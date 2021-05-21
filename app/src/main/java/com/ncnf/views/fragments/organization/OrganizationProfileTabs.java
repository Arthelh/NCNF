package com.ncnf.views.fragments.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ncnf.R;


public class OrganizationProfileTabs extends Fragment {

    public static ViewPager2 viewPager;
    private final String[] tabNames = new String[]{"Profile", "Events"};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Get the uuid and pass it to the children
        String uuid = getArguments().getString("organization_id");

        viewPager = requireView().findViewById(R.id.user_view_pager);
        FragmentStateAdapter pagerAdapter = new OrganizationProfileTabs.ViewPagerFragmentAdapter(requireActivity(), uuid);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = requireView().findViewById(R.id.user_tabs);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabNames[position])).attach();
        return inflater.inflate(R.layout.fragment_organization_tabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        String uuid;

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity, String uuid) {
            super(fragmentActivity);
            this.uuid=uuid;

        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    return createFragmentWithExtra(this.uuid, 0);
                case 1:
                    return createFragmentWithExtra(this.uuid, 1);
                default:
                    //TODO default case
                    throw new IllegalStateException();
            }
        }

        private Fragment createFragmentWithExtra(String uuid, int fragId) {
            Fragment f;
            switch (fragId) {
                case 0:
                    f = new OrganizationViewFragment();
                    break;
                case 1:
                    f = new OrganizationEventsFragment();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid class type Id");
            }
            Bundle bundle = new Bundle();
            bundle.putString("organization_id", uuid);
            f.setArguments(bundle);
            return f;
        }

        @Override
        public int getItemCount() {
            return tabNames.length;
        }
    }
}
