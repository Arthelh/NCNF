package com.ncnf.views.activities.group;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;

import com.ncnf.models.User;
import com.ncnf.views.fragments.group.GroupFragment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupActivity extends AppCompatActivity {

    @Inject
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getSupportFragmentManager().beginTransaction().add(R.id.group_fragment_holder, new GroupFragment()).commit();


    }


}