package com.ncnf.views.fragments.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.adapters.UserListAdapter;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.repositories.UserRepository;
import com.ncnf.views.activities.group.FriendsTrackerActivity;
import com.ncnf.views.fragments.user.PublicProfileFragment;
import com.ncnf.views.fragments.user.UserProfileTabFragment;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupDisplayFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public GroupRepository repository;

    @Inject
    public UserRepository userRepository;

    private String groupID;
    private Group group;

    private TextView groupName;
    private TextView groupAddress;
    private TextView groupDescription;
    private TextView groupOwner;

    private RecyclerView recycler;
    private UserListAdapter adapter;

    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupName = view.findViewById(R.id.group_display_name);
        groupDescription = view.findViewById(R.id.group_display_description);
        groupAddress = view.findViewById(R.id.group_address);
        groupOwner = view.findViewById(R.id.group_owner);

        recycler = view.findViewById(R.id.group_attendees_view);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        button = view.findViewById(R.id.open_map_button);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            this.groupID = bundle.getString("GROUP_ID");

            CompletableFuture<Group> thisGroup = repository.loadGroup(groupID);

            thisGroup.thenAccept(group -> {
                this.group = group;
                prepareAllFields();
            });

        }
    }

    private void prepareAllFields() {

        groupName.setText(group.getName());
        groupDescription.setText(group.getDescription());
        groupAddress.setText(group.getAddress());

        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FriendsTrackerActivity.class);
            intent.putExtra("GROUP_ID", group.getUuid().toString());
            startActivity(intent);
        });

        userRepository.loadUser(group.getOwnerId()).thenAccept(user -> {
            if(user.getFullName() != null && user.getFullName().length() > 0) {
                if(user.getUsername() != null && user.getUsername().length() > 0) {
                    groupOwner.setText(user.getFullName() + " (@" + user.getUsername() + ")");
                }
                else {
                    groupOwner.setText(user.getFullName());
                }
            }
            else {
                if(user.getUsername() != null && user.getUsername().length() > 0) {
                    groupOwner.setText("@" + user.getUsername());
                }
                else {
                    groupOwner.setText(user.getEmail());
                }
            }
        });

        List<String> uuids = new ArrayList<>(group.getAttendees());
        uuids.add(group.getOwnerId());
        CompletableFuture<List<User>> users = userRepository.loadMultipleUsers(uuids);
        users.thenAccept(users1 -> {
            adapter = new UserListAdapter(getContext(), users1, this::showUserPublicProfile);
            recycler.setAdapter(adapter);
        });

    }

    private void showUserPublicProfile(User user) {

        FragmentManager fragmentManager = getParentFragmentManager();

        if(user.getUuid().equals(this.user.getUuid())) {
            UserProfileTabFragment userProfileTabFragment = new UserProfileTabFragment();
            fragmentManager.beginTransaction().replace(((ViewGroup) requireView().getParent()).getId(), userProfileTabFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            PublicProfileFragment publicProfileFragment = new PublicProfileFragment(user);
            fragmentManager.beginTransaction().replace(((ViewGroup) requireView().getParent()).getId(), publicProfileFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
