package com.ncnf.views.fragments.group;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.adapters.GroupsAdapter;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.repositories.UserRepository;


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

    @Inject
    public UserRepository userRepository;

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
            List<String> allGroups = new ArrayList<>(user.getParticipatingGroupsIds());
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

        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", g.getUuid().toString());

        if(g.getOwnerId().equals(user.getUuid())) {
            GroupDisplayFragmentOwner nextFrag= new GroupDisplayFragmentOwner();
            nextFrag.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                    .addToBackStack("tag")
                    .commit();

        }
        else {
            GroupDisplayFragment nextFrag= new GroupDisplayFragment();
            nextFrag.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                    .addToBackStack("tag")
                    .commit();
        }
    }


}