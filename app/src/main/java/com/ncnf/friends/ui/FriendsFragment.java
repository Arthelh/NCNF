package com.ncnf.friends.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ncnf.R;
import com.ncnf.user.FriendsRepository;
import com.ncnf.user.User;
import com.ncnf.user.UserAdapter;
import com.ncnf.views.fragments.PublicProfileFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendsFragment extends Fragment {

    @Inject
    public FirebaseUser user;

    @Inject
    public FriendsRepository friendsRepository;

    private MaterialSearchBar materialSearchBar;
    private RecyclerView recycler;
    private TextView emptyRecyclerText;
    private MaterialButton addFriendButton;
    private UserAdapter adapter;

    private boolean isAddFriendView;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.fragmentManager = getChildFragmentManager();
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get fragment views
        materialSearchBar = requireView().findViewById(R.id.friends_search_bar);
        recycler = requireView().findViewById(R.id.friends_recycler_view);
        emptyRecyclerText = requireView().findViewById(R.id.friends_text);
        addFriendButton = requireView().findViewById(R.id.friends_switch_button);

        // Setup RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.hasFixedSize();

        // Setup button
        addFriendButton.setOnClickListener(this::onAddFriendButtonClicked);

        // Setup friends view
        isAddFriendView = false;
        setupFriendsView();
    }

    private void onAddFriendButtonClicked(View view){
        if(!isAddFriendView){
            setupAddFriendView();
            isAddFriendView = true;
        } else {
            setupFriendsView();
            isAddFriendView = false;
        }
    }

    private void setupFriendsView(){
        addFriendButton.setIcon(requireActivity().getDrawable(R.drawable.ic_baseline_add_24));

        // Hide search bar
        this.materialSearchBar.setVisibility(View.GONE);

        // Remove items in recycler
        adapter = new FriendsAdapter(requireActivity(), new ArrayList<>(), this::showUserPublicProfile, this.friendsRepository, this.user.getUid(), FriendsAdapter.FriendsType.REMOVABLE);
        recycler.setAdapter(adapter);
        hideEmptyRecyclerText();

        // Hide empty recycler text
        friendsRepository.getFriends(user.getUid()).thenAccept(users -> {
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

    private void setupAddFriendView(){
        addFriendButton.setIcon(requireContext().getDrawable(R.drawable.ic_baseline_format_list_bulleted_24));

        // Show search bar
        materialSearchBar.setVisibility(View.VISIBLE);

        // Remove items in recycler
        adapter = new FriendsAdapter(requireActivity(), new ArrayList<>(), item -> {}, this.friendsRepository, this.user.getUid(), FriendsAdapter.FriendsType.ADDABLE);
        recycler.setAdapter(adapter);
        hideEmptyRecyclerText();

        // Setup searchBar
        materialSearchBar.requestFocus();
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {}

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if(!text.toString().isEmpty()){
                    friendsRepository.searchFriends(text.toString()).thenAccept(users -> {
                        if(users.isEmpty()){
                            showEmptyRecyclerText(getString(R.string.empty_search_friend_recycler_text));
                        } else {
                            hideEmptyRecyclerText();
                        }
                        adapter.setUsers(users);
                    }).exceptionally(exception -> {
                        showEmptyRecyclerText(exception.getMessage());
                        return null; // TODO : handle exception
                    });
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //Define behaviour for navigation button
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch();
                }
            }
        });
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