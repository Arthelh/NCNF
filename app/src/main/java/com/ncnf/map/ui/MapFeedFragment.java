package com.ncnf.map.ui;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ncnf.R;
import com.ncnf.feed.ui.FeedFragment;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.socialObject.ui.SocialObjFragment;

import java.util.List;

public class MapFeedFragment extends FeedFragment {

    private final Window globalWindow;
    private final FragmentManager fragmentManager;

    protected MapFeedFragment(List<SocialObject> list, Window globalWindow, FragmentManager fragmentManager){
        super(list);

        this.globalWindow = globalWindow;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onEventClick(SocialObject e){
        Fragment fragment = new SocialObjFragment(e);

        ConstraintLayout feedContainer = globalWindow.findViewById(R.id.map_feed_container);
        FrameLayout feedFrame = globalWindow.findViewById(R.id.map_feed_fragment);
        Button feedButton = globalWindow.findViewById(R.id.map_feed_button);

        feedContainer.setBackgroundResource(R.drawable.main_background_gradient);
        feedContainer.setVisibility(View.VISIBLE);
        feedFrame.setVisibility(View.VISIBLE);
        feedButton.setVisibility(View.VISIBLE);
        feedButton.setText("Back to Feed");

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_feed_fragment, fragment).addToBackStack(null).commit();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                destroyChildFragment(fragmentManager, feedButton, feedContainer, this);
            }
        };

        feedButton.setOnClickListener(v -> {
            destroyChildFragment(fragmentManager, feedButton, feedContainer, callback);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    private void destroyChildFragment(FragmentManager fragmentManager, Button button, ConstraintLayout feedContainer, OnBackPressedCallback callback){
        fragmentManager.popBackStack();
        button.setText("Back to Map");
        button.setOnClickListener(v -> {
            fragmentManager.popBackStack();
            feedContainer.setVisibility(View.GONE);
        });
        callback.setEnabled(false);
    }
}
