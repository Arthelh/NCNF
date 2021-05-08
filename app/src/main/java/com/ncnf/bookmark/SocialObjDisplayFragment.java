package com.ncnf.bookmark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.feed.ui.SocialObjAdapter;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.socialObject.ui.SocialObjFragment;
import com.ncnf.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.SAVED_EVENTS_KEY;

@AndroidEntryPoint
public class SocialObjDisplayFragment extends Fragment implements SocialObjAdapter.OnSocialObjListener {

    private List<SocialObject> objToDisplay;
    private SocialObjAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private final String collection;

    @Inject
    public User user;

    public SocialObjDisplayFragment(String collection){
        this.collection = collection;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        objToDisplay = new ArrayList<>();
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.SavedEventsRecyclerView);
        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        adapter = new SocialObjAdapter(objToDisplay, this::onSocialObjectClick, SocialObjAdapter.SortingMethod.DATE);
        recycler.setAdapter(adapter);
        getEventList(view.findViewById(R.id.SavedEventsRecyclerView));

        user.loadUserFromDB();
    }

    private void getEventList(View view){
        if(user != null){
            CompletableFuture list;
            if(collection == SAVED_EVENTS_KEY){
                list = user.getSavedEvents();
            } else {
                list = user.getParticipatingGroups();
            }

            list.thenAccept(objects -> {
                if(objects != null){
                    this.adapter.setSocialObjects((List)objects);
                }
            });
        }
    }

    @Override
    public void onSocialObjectClick(SocialObject socialObject) {
        Fragment fragment = new SocialObjFragment(socialObject);
        Window globalWindow = getActivity().getWindow();
        FragmentManager fragmentManager = getChildFragmentManager();

        ConstraintLayout feedContainer = globalWindow.findViewById(R.id.display_event_container);
        FrameLayout feedFrame = globalWindow.findViewById(R.id.display_event_fragment);
        Button feedButton = globalWindow.findViewById(R.id.display_event_button);

        feedContainer.setBackgroundResource(R.drawable.main_background_gradient);
        feedContainer.setVisibility(View.VISIBLE);
        feedFrame.setVisibility(View.VISIBLE);
        feedButton.setVisibility(View.VISIBLE);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.display_event_fragment, fragment).commit();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                destroyChildFragment(fragmentManager, fragment, feedContainer, this);
            }
        };

        feedButton.setOnClickListener(v -> {
            destroyChildFragment(fragmentManager, fragment, feedContainer, callback);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    private void destroyChildFragment(FragmentManager fragmentManager, Fragment fragment, ConstraintLayout feedContainer, OnBackPressedCallback callback){
        fragmentManager.beginTransaction().remove(fragment).commit();
        feedContainer.setVisibility(View.GONE);
        callback.setEnabled(false);
    }
}