package com.ncnf.friends.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.ncnf.R;
import com.ncnf.user.User;
import com.ncnf.views.fragments.PublicProfileFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendsActivity extends AppCompatActivity {

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment;
    private Fragment friendsFragment;
    private Fragment addFriendFragment;
    private Fragment userProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        this.friendsFragment = new FriendsFragment();
        this.addFriendFragment = new AddFriendFragment();
        this.activeFragment = this.friendsFragment;

        fragmentManager.beginTransaction().add(R.id.friends_fragment_container, friendsFragment).hide(friendsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.friends_fragment_container, addFriendFragment).hide(addFriendFragment).commit();

        findViewById(R.id.friends_switch_button).setOnClickListener(this::onButtonClicked);

        showActiveFragment();
    }

    private void onButtonClicked(View view) {
        MaterialButton addButton = (MaterialButton) view;

        if(activeFragment instanceof FriendsFragment){
            addButton.setIcon(getDrawable(R.drawable.ic_baseline_format_list_bulleted_24));
            fragmentManager.beginTransaction().hide(activeFragment).show(addFriendFragment).commit();
            this.activeFragment = addFriendFragment;
        } else if(activeFragment instanceof AddFriendFragment){
            addButton.setIcon(getDrawable(R.drawable.ic_baseline_add_24));
            fragmentManager.beginTransaction().hide(activeFragment).show(friendsFragment).commit();
            this.activeFragment = friendsFragment;
        }
    }

    protected void showPublicProfileFragment(User user){
        Toast.makeText(this, "TEST_PROFILE_DISPLAY", Toast.LENGTH_LONG).show();
        this.userProfileFragment = new PublicProfileFragment(user);
        fragmentManager.beginTransaction().replace(R.id.friends_fragment_container, userProfileFragment).commit();
    }

    protected void showActiveFragment(){
        fragmentManager.beginTransaction().show(activeFragment).addToBackStack("active").commit();
    }

}