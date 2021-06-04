package com.ncnf.views.activities.bookmark;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.views.fragments.bookmark.EventDisplayFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookMarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentBookmarkContainer, new EventDisplayFragment()).commit();


    }
}