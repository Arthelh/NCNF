package com.ncnf.friends.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.user.friends.FriendsRepository;
import com.ncnf.user.User;
import com.ncnf.user.helpers.UserAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

@AndroidEntryPoint
public class FriendsFragment extends Fragment {

    @Inject
    public FirebaseUser user;

    @Inject
    public FriendsRepository friendsRepository;

    private RecyclerView recycler;
    private UserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Handle recyclerView
        recycler = getView().findViewById(R.id.friends_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.hasFixedSize();
        adapter = new UserAdapter(new ArrayList<>(), this::displayUser);
        recycler.setAdapter(adapter);

        friendsRepository.getFriends(user.getUid()).thenAccept(users -> {
            adapter.setUsers(users);
        }).exceptionally(exception -> {
            return null; // TODO : handle exception
        });
    }

    private void displayUser(User user){
        // TODO: Go to profile
        // Also change the test
        Snackbar.make(getActivity().findViewById(android.R.id.content), "DISPLAY_USER_PROFILE", LENGTH_LONG).show();
    }
}