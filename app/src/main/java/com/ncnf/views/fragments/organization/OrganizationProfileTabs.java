package com.ncnf.views.fragments.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ncnf.R;


public class OrganizationProfileTabs extends Fragment {

    public static ViewPager2 viewPager;
    private final String[] tabNames = new String[]{"Profile", "Events"};
    private String uuid;

    public OrganizationProfileTabs(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Get the uuid and pass it to the children
        requireActivity().getSupportFragmentManager().setFragmentResultListener("request Key", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                uuid = result.getString("organization_id");
            }
        });
        return inflater.inflate(R.layout.fragment_organization_tabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = requireView().findViewById(R.id.organization_view_pager);
        FragmentStateAdapter pagerAdapter = new OrganizationProfileTabs.ViewPagerFragmentAdapter(requireActivity(), uuid);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = requireView().findViewById(R.id.organization_tabs);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabNames[position])).attach();
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {


        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity, String uuid) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    return createFragmentWithExtra(uuid, 0);
                case 1:
                    return createFragmentWithExtra(uuid, 1);
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
            requireActivity().getSupportFragmentManager().setFragmentResult("organization_id",bundle);
            return f;
        }

        @Override
        public int getItemCount() {
            return tabNames.length;
        }
    }
}
