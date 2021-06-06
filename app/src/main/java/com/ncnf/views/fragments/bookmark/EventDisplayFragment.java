package com.ncnf.views.fragments.bookmark;

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

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.adapters.EventListAdapter;
import com.ncnf.models.Event;
import com.ncnf.repositories.UserRepository;
import com.ncnf.views.fragments.event.EventFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventDisplayFragment extends Fragment implements EventListAdapter.OnSocialObjListener {

    private List<Event> objToDisplay;
    private EventListAdapter adapter;
    private RecyclerView.LayoutManager lManager;

    @Inject
    public FirebaseUser user;

    @Inject
    public UserRepository userRepository;

    public EventDisplayFragment(){
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
        RecyclerView recycler = view.findViewById(R.id.SavedEventsRecyclerView);
        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        adapter = new EventListAdapter(getContext(), objToDisplay, this::onEventClick);
        recycler.setAdapter(adapter);

        user.getUid();
        userRepository.loadUser(user.getUid()).thenAccept(user ->{
            user.getSavedEvents().thenAccept(list -> {
               if(list != null){
                   this.adapter.setEvents(list);
               }
            });
        });
    }

    @Override
    public void onEventClick(Event event) {
        Fragment fragment = new EventFragment(event);
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