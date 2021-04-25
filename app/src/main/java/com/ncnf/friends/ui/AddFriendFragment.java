package com.ncnf.friends.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ncnf.R;
import com.ncnf.Utils;
import com.ncnf.database.builder.UserBuilder;
import com.ncnf.user.User;
import com.ncnf.user.UserAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddFriendFragment extends Fragment {

    private RecyclerView recycler;
    private UserAdapter adapter;
    private CollectionReference usersRef;
    private MaterialSearchBar materialSearchBar;

    @Inject
    public User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Handle recyclerView
        recycler = getView().findViewById(R.id.add_friend_recycler_view);
        recycler.hasFixedSize();

        materialSearchBar = getView().findViewById(R.id.add_friend_search_bar);

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) { }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                searchUserWithName(text.toString());
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

    //Search the database for a user with the given name
    private void searchUserWithName(String name){
        //Handle recyclerView
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserAdapter(new ArrayList<>(), this::displayUser);
        recycler.setAdapter(adapter);

        user.loadUserFromDB().thenCompose(user1 -> user.getAllUsers()).thenAccept(users -> {
            adapter.setUsers(users);
        }).exceptionally(exception -> {
            return null; // TODO : handle exception
        });

    }


    //TODO decide how and what to display
    private void displayUser(User user){
        Toast.makeText(getActivity(), "TEST_PROFILE_DISPLAY", Toast.LENGTH_LONG).show();
    }

}