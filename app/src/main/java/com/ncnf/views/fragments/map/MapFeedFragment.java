package com.ncnf.views.fragments.map;

import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ncnf.R;
import com.ncnf.views.fragments.feed.FeedFragment;
import com.ncnf.models.Event;
import com.ncnf.views.fragments.event.EventFragment;

import java.util.List;

public class MapFeedFragment extends FeedFragment {

    private final Window globalWindow;
    private final FragmentManager fragmentManager;

    public MapFeedFragment(List<Event> list, Window globalWindow, FragmentManager fragmentManager){
        super(list);

        this.globalWindow = globalWindow;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onEventClick(Event e){
        Fragment fragment = new EventFragment(e);

        ConstraintLayout feedContainer = globalWindow.findViewById(R.id.map_feed_container);
        FrameLayout feedFrame = globalWindow.findViewById(R.id.map_feed_fragment);
        //Button feedButton = globalWindow.findViewById(R.id.map_feed_button);

        feedContainer.setBackgroundResource(R.drawable.main_background_gradient);
        feedContainer.setVisibility(View.VISIBLE);
        feedFrame.setVisibility(View.VISIBLE);
        //feedButton.setVisibility(View.VISIBLE);
        //feedButton.setText("Back to Feed");

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_feed_fragment, fragment).addToBackStack(null).commit();


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentManager.popBackStack();
                this.setEnabled(false);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }
}
