package com.ncnf.views.activities.bookmark;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ncnf.R;
import com.ncnf.views.fragments.bookmark.FragmentSelector;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookMarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        FragmentSelector fragmentSelector = new FragmentSelector(this);
        ViewPager2 viewPager = findViewById(R.id.bookmark_view_pager);
        viewPager.setAdapter(fragmentSelector);

        TabLayout tabs = findViewById(R.id.bookmark_tabs);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(fragmentSelector.getPageTitle(position))
        ).attach();

    }
}