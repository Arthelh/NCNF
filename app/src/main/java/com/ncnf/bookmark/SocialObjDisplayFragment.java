package com.ncnf.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.feed.ui.SocialObjAdapter;
import com.ncnf.socialObject.SocialObjActivity;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.SAVED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;



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
        Intent intent = new Intent(getActivity(), SocialObjActivity.class);
        intent.putExtra(UUID_KEY, socialObject.getUuid().toString());
        startActivity(intent);
    }
}