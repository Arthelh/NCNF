package com.ncnf.views.activities.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.ncnf.R;
import com.ncnf.views.fragments.feed.FeedFragment;
import com.ncnf.views.fragments.group.FriendSelectionGroupFragment;
import com.ncnf.views.fragments.group.GroupEditingFragment;
import com.ncnf.views.fragments.home.HomeFragment;
import com.ncnf.views.fragments.map.MapFragment;

public class GroupCreationActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment groupEditionFragment;
    private Fragment friendSelectorFragment;

    private MaterialButton nextStepButton;
    private boolean isFriendFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);


        if(savedInstanceState == null){
            this.groupEditionFragment = new GroupEditingFragment();
            this.friendSelectorFragment = new FriendSelectionGroupFragment();
        }

        fragmentManager.beginTransaction().add(R.id.group_creation_fragment_container, this.groupEditionFragment).hide(this.groupEditionFragment).commit();
        fragmentManager.beginTransaction().add(R.id.group_creation_fragment_container, this.friendSelectorFragment).hide(this.friendSelectorFragment).commit();

        fragmentManager.beginTransaction().show(this.groupEditionFragment).commit();

        nextStepButton = findViewById(R.id.next_step_group_creation_button);
        nextStepButton.setOnClickListener(this::switchViews);
        isFriendFragment = false;
    }

    private void switchViews(View view) {
        if(!isFriendFragment){
            fragmentManager.beginTransaction().hide(this.friendSelectorFragment).commit();
            fragmentManager.beginTransaction().show(this.groupEditionFragment).commit();
            nextStepButton.setIcon(getDrawable(R.drawable.ic_baseline_arrow_back_24));
            isFriendFragment = true;
        } else {
            fragmentManager.beginTransaction().hide(this.groupEditionFragment).commit();
            fragmentManager.beginTransaction().show(this.friendSelectorFragment).commit();
            nextStepButton.setIcon(getDrawable(R.drawable.ic_baseline_arrow_forward_24));
            isFriendFragment = false;
        }
    }

}