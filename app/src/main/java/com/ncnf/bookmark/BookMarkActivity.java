package com.ncnf.bookmark;

import android.os.Bundle;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.ncnf.R;
import com.ncnf.bookmark.ui.FragmentSelector;
import com.ncnf.user.PrivateUser;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookMarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        FragmentSelector fragmentSelector = new FragmentSelector(this);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(fragmentSelector);

        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(fragmentSelector.getPageTitle(position))
        ).attach();

    }
}