package com.ncnf.friends.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ncnf.R;
import com.ncnf.user.FriendsRepository;
import com.ncnf.user.User;
import com.ncnf.user.UserAdapter;

import java.util.List;

public class FriendsRequestAdapter extends UserAdapter {

    private final FriendsRepository friendsRepository;

    public FriendsRequestAdapter(List<User> users, OnItemClickListener onClick, FriendsRepository friendsRepository) {
        super(users, onClick);
        this.friendsRepository = friendsRepository;
    }

    @NonNull
    @Override
    public FriendsRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_request_card_view, parent, false);
        return new FriendsRequestViewHolder(v, friendsRepository);
    }

    public static class FriendsRequestViewHolder extends UserViewHolder {

        private final FriendsRepository friendsRepository;
        private final Button acceptButton;
        private final Button declineButton;
        private final TextView messageView;

        public FriendsRequestViewHolder(View v, FriendsRepository friendsRepository) {
            super(v);

            acceptButton = v.findViewById(R.id.accept_friends_request);
            declineButton = v.findViewById(R.id.decline_friends_request);
            messageView = v.findViewById(R.id.friends_request_message);
            this.friendsRepository = friendsRepository;
        }

        public void bind(final User u, final UserAdapter.OnItemClickListener onClick) {
            super.bind(u, onClick);

            acceptButton.setOnClickListener(view -> {
                friendsRepository.updateRequest(true, u.getUuid());
                displayMessage("The request was accepted !");
            });

            declineButton.setOnClickListener(view -> {
                friendsRepository.updateRequest(false, u.getUuid());
                displayMessage("The request was declined !");
            });
        }

        private void displayMessage(String message) {
            acceptButton.setVisibility(View.INVISIBLE);
            declineButton.setVisibility(View.INVISIBLE);
            messageView.setText(message);
            messageView.setVisibility(View.VISIBLE);

        }
    }
}
