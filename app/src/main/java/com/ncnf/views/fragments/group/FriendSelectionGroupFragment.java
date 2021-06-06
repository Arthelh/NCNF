package com.ncnf.views.fragments.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ncnf.R;
import com.ncnf.adapters.FriendsSelectorList;
import com.ncnf.adapters.UserListAdapter;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.FriendsRepository;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.views.activities.group.GroupActivity;
import com.ncnf.views.fragments.user.PublicProfileFragment;
import com.ncnf.views.fragments.user.UserProfileTabFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;

@AndroidEntryPoint
public class FriendSelectionGroupFragment extends Fragment {


    private User user;
    private FriendsRepository friendsRepository;
    private GroupRepository groupRepository;

    private RecyclerView recycler;
    private TextView emptyRecyclerText;
    private UserListAdapter adapter;

    private Group group;
    private Button saveMembers;

    public FriendSelectionGroupFragment(User user, FriendsRepository friendsRepository){
        this.user = user;
        this.friendsRepository = friendsRepository;
    }

    public FriendSelectionGroupFragment(User user, FriendsRepository friendsRepository, Group group, GroupRepository groupRepository){
        this.user = user;
        this.friendsRepository = friendsRepository;
        this.group = group;
        this.groupRepository = groupRepository;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_selection_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get fragment views
        recycler = requireView().findViewById(R.id.friends_selector_group_recycler_view);
        emptyRecyclerText = requireView().findViewById(R.id.friends_group_selector_text);
        saveMembers = requireView().findViewById(R.id.add_grp_members_button);
        if(group != null) {
            saveMembers.setVisibility(View.VISIBLE);
            saveMembers.setOnClickListener(this::saveNewMembers);
        }

        // Setup RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.hasFixedSize();

        setupMembersView();
    }

    public void setupMembersView(){
        // Remove items in recycler
        Context context = requireActivity();
        adapter = new FriendsSelectorList(context, new ArrayList<>(), this::showUserPublicProfile, this.user.getUuid());
        recycler.setAdapter(adapter);
        hideEmptyRecyclerText();
        // Hide empty recycler text

        friendsRepository.getFriends(user.getUuid()).thenAccept(users -> {
            if(group != null) {
                List<User> newUsers = new ArrayList<>();
                for(int i = 0; i < users.size(); ++i) {
                    if(!group.getMembers().contains(users.get(i).getUuid())) {
                        newUsers.add(users.get(i));
                    }
                }
                users = new ArrayList<>(newUsers);
            }
            if (users.isEmpty()) {
                showEmptyRecyclerText(getString(R.string.empty_friends_recycler_text));
                saveMembers.setVisibility(View.INVISIBLE);
            } else {
                hideEmptyRecyclerText();
            }
            adapter.setUsers(users);
        }).exceptionally(exception -> {
            showEmptyRecyclerText(exception.getMessage());
            return null; // TODO : handle exception
        });

    }

    public List<User> getMembers(){
        return ((FriendsSelectorList)this.adapter).getMembers();
    }


    private void showEmptyRecyclerText(String text){
        emptyRecyclerText.setText(text);
        emptyRecyclerText.setVisibility(View.VISIBLE);
    }

    private void hideEmptyRecyclerText(){
        emptyRecyclerText.setVisibility(View.GONE);
    }

    private void showUserPublicProfile(User user) {
        FragmentManager fragmentManager = getParentFragmentManager();

        PublicProfileFragment publicProfileFragment = new PublicProfileFragment(user);

        fragmentManager.beginTransaction().replace(((ViewGroup) requireView().getParent()).getId(), publicProfileFragment)
                .addToBackStack(null)
                .commit();
    }

    private void saveNewMembers(View v) {
        List<String> usersUUIDS = group.getMembers();
        for(int i = 0; i < getMembers().size(); ++i) {
            usersUUIDS.add(getMembers().get(i).getUuid());
        }
        group.setMembers(usersUUIDS);
        groupRepository.storeGroup(group).thenAccept(aBoolean -> {
            Intent intent = new Intent(getContext(), GroupActivity.class);
            startActivity(intent);
        });
    }
}