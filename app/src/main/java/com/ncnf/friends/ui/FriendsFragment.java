package com.ncnf.friends.ui;

import android.os.Bundle;
import android.util.Log;
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
import com.ncnf.R;
import com.ncnf.Utils;
import com.ncnf.database.builder.UserBuilder;
import com.ncnf.repositories.UsersRepository;
import com.ncnf.user.User;
import com.ncnf.user.UserAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendsFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public UsersRepository userRepo;

    private RecyclerView recycler;
    private UserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FriendsActivity parentActivity = (FriendsActivity)getActivity();
        //Handle recyclerView
        recycler = getView().findViewById(R.id.friends_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.hasFixedSize();
        adapter = new UserAdapter(new ArrayList<>(), parentActivity::showPublicProfileFragment);
        recycler.setAdapter(adapter);

        this.userRepo.loadUser(user.getUuid()).thenAccept(user1 -> {
            user1.getFriends().thenAccept(users -> adapter.setUsers(users));
        }).exceptionally(exception -> {
            return null;
        });
        
    }

    private void displayUser(User user){
        Toast.makeText(getActivity(), "TEST_PROFILE_DISPLAY", Toast.LENGTH_LONG).show();
    }
}