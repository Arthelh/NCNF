package com.ncnf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.ncnf.R;
import com.ncnf.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendsSelectorList extends UserListAdapter{

    private final String uuid;
    private final List<User> members;

    public FriendsSelectorList(Context context, List<User> users, OnItemClickListener onClick, String my_uuid) {

        super(context, users, onClick);

        this.uuid = my_uuid;
        this.members = new ArrayList<>();
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_selector_card, parent, false);
        return new FriendsViewHolder(v);
    }

    public class FriendsViewHolder extends UserListAdapter.UserViewHolder {

        public final MaterialButton friendCardButton;
        private boolean inGroup;

        public FriendsViewHolder(View v) {
            super(v);
            inGroup = false;
            this.friendCardButton = v.findViewById(R.id.check_friend_button);
        }

        public void bind(final User u, final UserListAdapter.OnItemClickListener onClick) {
            super.bind(u, onClick);
            friendCardButton.setIconResource(R.drawable.ic_round_check_box_outline_blank_24);
            friendCardButton.setOnClickListener(v -> changeMembership(u));
        }

        private void changeMembership(User u) {
            if(!this.inGroup){
                this.friendCardButton.setIconResource(R.drawable.ic_outline_check_box_24);
                this.inGroup = true;
                members.add(u);
            } else {
                this.friendCardButton.setIconResource(R.drawable.ic_round_check_box_outline_blank_24);
                this.inGroup = false;
                members.remove(u);
            }
        }
    }

    public List<User> getMembers() {
        return Collections.unmodifiableList(members);
    }
}

