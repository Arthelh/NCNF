package com.ncnf.views.fragments.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.adapters.FriendsRequestListAdapter;
import com.ncnf.repositories.FriendsRepository;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendsRequestsFragment extends Fragment {

    @Inject
    public FriendsRepository friendsRepository;

    @Inject
    public FirebaseUser user;

    private RecyclerView recycler;
    private FriendsRequestListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Handle recyclerView
        recycler = getView().findViewById(R.id.friends_requests_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.hasFixedSize();
        adapter = new FriendsRequestListAdapter(requireActivity(), new ArrayList<>(), item -> {}, friendsRepository, user.getUid());
        recycler.setAdapter(adapter);

        friendsRepository.awaitingRequests(user.getUid()).thenAccept(users -> {
            adapter.setUsers(users);
        }).exceptionally(exception -> {
            return null; // TODO : handle exception
        });
    }
}
