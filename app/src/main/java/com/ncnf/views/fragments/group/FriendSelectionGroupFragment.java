package com.ncnf.views.fragments.group;

import android.content.Context;
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

import com.ncnf.R;
import com.ncnf.adapters.FriendsSelectorList;
import com.ncnf.adapters.UserListAdapter;
import com.ncnf.models.User;
import com.ncnf.repositories.FriendsRepository;
import com.ncnf.views.fragments.user.PublicProfileFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendSelectionGroupFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public FriendsRepository friendsRepository;

    private RecyclerView recycler;
    private TextView emptyRecyclerText;
    private UserListAdapter adapter;

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

        // Setup RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.hasFixedSize();
        setupMembersView();
    }

    private void setupMembersView(){
        // Remove items in recycler
        Context context = requireActivity();
        adapter = new FriendsSelectorList(context, new ArrayList<>(), this::showUserPublicProfile, this.user.getUuid());
        recycler.setAdapter(adapter);
        hideEmptyRecyclerText();
        // Hide empty recycler text
        friendsRepository.getFriends(user.getUuid()).thenAccept(users -> {
            if(users.isEmpty()){
                showEmptyRecyclerText(getString(R.string.empty_friends_recycler_text));
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
}