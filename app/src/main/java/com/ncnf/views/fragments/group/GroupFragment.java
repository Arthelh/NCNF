package com.ncnf.views.fragments.group;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.adapters.GroupsAdapter;
import com.ncnf.models.Group;
import com.ncnf.models.SocialObject;
import com.ncnf.models.User;
import com.ncnf.repositories.GroupRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public GroupRepository repository;

    private FragmentManager fragmentManager;
    private RecyclerView recycler;
    private GroupsAdapter adapter;

    private TextView isEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.fragmentManager = getChildFragmentManager();

        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = requireView().findViewById(R.id.group_recycler_view);
        // Setup RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.hasFixedSize();

        isEmpty = view.findViewById(R.id.no_owned_group);

        setUpGroupsView();
    }

    private void setUpGroupsView() {

        user.loadUserFromDB().thenAccept(user -> {
            List<String> allGroups = new ArrayList<>(user.getOwnedGroupsIds());
            allGroups.addAll(user.getParticipatingGroupsIds());
            CompletableFuture<List<Group>> groupsFuture = repository.loadMultipleGroups(allGroups);

            groupsFuture.thenAccept(groups -> {
                if(groups.isEmpty()) {
                    isEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    adapter = new GroupsAdapter(getContext(), groups, this::openGroupPage);
                    recycler.setAdapter(adapter);
                }
            });

        });

    }

    private void openGroupPage(Group g) {
        GroupDisplayFragment nextFrag= new GroupDisplayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", g.getUuid().toString());
        nextFrag.setArguments(bundle);


        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                .addToBackStack("tag")
                .commit();


    }


}