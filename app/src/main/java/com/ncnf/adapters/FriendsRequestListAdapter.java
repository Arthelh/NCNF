package com.ncnf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ncnf.R;
import com.ncnf.repositories.FriendsRepository;
import com.ncnf.models.User;

import java.util.List;

public class FriendsRequestListAdapter extends UserListAdapter {

    private final FriendsRepository friendsRepository;
    private final String uuid;

    public FriendsRequestListAdapter(Context context, List<User> users, OnItemClickListener onClick, FriendsRepository friendsRepository, String my_uuid) {
        super(context, users, onClick);
        this.friendsRepository = friendsRepository;
        this.uuid = my_uuid;
    }

    @NonNull
    @Override
    public FriendsRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_request_card_view, parent, false);
        return new FriendsRequestViewHolder(v, friendsRepository, uuid);
    }

    public class FriendsRequestViewHolder extends UserViewHolder {

        private final FriendsRepository friendsRepository;
        private final String uuid;
        private final Button acceptButton;
        private final Button declineButton;
        private final TextView messageView;

        public FriendsRequestViewHolder(View v, FriendsRepository friendsRepository, String uuid) {
            super(v);

            acceptButton = v.findViewById(R.id.accept_friends_request);
            declineButton = v.findViewById(R.id.decline_friends_request);
            messageView = v.findViewById(R.id.friends_request_message);
            this.friendsRepository = friendsRepository;
            this.uuid = uuid;
        }

        public void bind(final User u, final UserListAdapter.OnItemClickListener onClick) {
            super.bind(u, onClick);

            acceptButton.setOnClickListener(view -> {
                friendsRepository.updateRequest(true, uuid, u.getUuid());
                displayMessage(context.getString(R.string.friend_request_accept_text));
            });

            declineButton.setOnClickListener(view -> {
                friendsRepository.updateRequest(false, uuid, u.getUuid());
                displayMessage(context.getString(R.string.friend_request_decline_text));
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
