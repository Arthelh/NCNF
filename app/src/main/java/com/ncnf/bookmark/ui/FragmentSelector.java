package com.ncnf.bookmark.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ncnf.bookmark.EventDisplayFragment;
import com.ncnf.user.PrivateUser;

import static com.ncnf.Utils.*;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class FragmentSelector extends FragmentStateAdapter {

    private static final String[] TAB_TITLES = new String[]{"Saved Events", "Organized Events"};
    private static final int itemCount = 2;

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
            return new EventDisplayFragment(OWNED_EVENTS_KEY);
        }
        return new EventDisplayFragment(SAVED_EVENTS_KEY);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }
}
