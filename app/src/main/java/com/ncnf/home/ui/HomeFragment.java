package com.ncnf.home.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.authentication.ui.LoginActivity;
import com.ncnf.event.create.EventCreateActivity;
import com.ncnf.event.update.EventNewsActivity;
import com.ncnf.friends.ui.FriendsActivity;
import com.ncnf.user.UserProfileActivity;
import com.ncnf.user_search.UserSearchActivity;


public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.homeProfileButton).setOnClickListener(this::gotToProfile);
        getView().findViewById(R.id.homeCreateEventButton).setOnClickListener(this::goToEventCreation);
        getView().findViewById(R.id.homeEventNewsButton).setOnClickListener(this::goToEventNews);
        getView().findViewById(R.id.homeSearchUserButton).setOnClickListener(this::goToFriends);
    }

    public void gotToProfile(View view){
        Intent intent;

        if(!isConnected()){
            intent = new Intent(getContext(), LoginActivity.class);
        } else {
            intent = new Intent(getContext(), UserProfileActivity.class);
        }

        startActivity(intent);
    }

    public void goToEventCreation(View view){
        Intent intent;

        if(!isConnected()){
            intent = new Intent(getContext(), LoginActivity.class);
        } else {
            intent = new Intent(getContext(), EventCreateActivity.class);
        }

        startActivity(intent);
    }

    public void goToFriends(View view){
        Intent intent = new Intent(getContext(), FriendsActivity.class);
        startActivity(intent);
    }

    // Temporary
    public void goToEventNews(View view){
        Intent intent = new Intent(getContext(), EventNewsActivity.class);
        intent.putExtra("uuid", "361f8d6f-ccf0-4ee3-a596-d62a910874f6");
        startActivity(intent);
    }

    private boolean isConnected(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
    
}