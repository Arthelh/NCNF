package com.ncnf.home.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.authentication.ui.LoginActivity;
import com.ncnf.event.Event;
import com.ncnf.event.create.EventCreateActivity;
import com.ncnf.event.update.EventNewsActivity;
import com.ncnf.main.MainActivity;
import com.ncnf.friends.ui.FriendsActivity;
import com.ncnf.user.UserProfileActivity;

import static com.ncnf.Utils.NEXT_ACTIVITY_EXTRA_KEY;
import static com.ncnf.Utils.UUID_KEY;


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
        getView().findViewById(R.id.homeFriendsButton).setOnClickListener(this::goToFriends);
    }

    public void gotToProfile(View view){
        GoTo(UserProfileActivity.class);
    }

    public void goToEventCreation(View view){
        GoTo(EventCreateActivity.class);
    }

    private void GoTo(Class<?> activity){
        if(!isConnected()){
            createLoginPop(activity);
        } else {
            Intent intent = new Intent(getContext(), activity);
            startActivity(intent);
        }
    }

    public void goToFriends(View view){
        Intent intent = new Intent(getContext(), FriendsActivity.class);
        startActivity(intent);
    }

    // Temporary
    public void goToEventNews(View view){
        Intent intent = new Intent(getContext(), EventNewsActivity.class);
        intent.putExtra(UUID_KEY, "361f8d6f-ccf0-4ee3-a596-d62a910874f6");
        startActivity(intent);
    }

    private boolean isConnected(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private final static String POPUP_TITLE = "Connection Alert";
    private final static String POPUP_MESSAGE = "To do so, a user account is required. Please connect";
    private final static String POPUP_POSITIVE_BUTTON = "Log in / Register";
    private final static String POPUP_NEGATIVE_BUTTON = "Cancel";


    private void createLoginPop(Class<?> activity){
        AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
        popup.setCancelable(true);
        popup.setTitle(POPUP_TITLE);
        popup.setMessage(POPUP_MESSAGE);
        popup.setPositiveButton(POPUP_POSITIVE_BUTTON, (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.putExtra(NEXT_ACTIVITY_EXTRA_KEY, activity);
            startActivity(intent);
        });
        popup.setNegativeButton(POPUP_NEGATIVE_BUTTON, (dialog, which) -> dialog.cancel());
        popup.show();

    }

}