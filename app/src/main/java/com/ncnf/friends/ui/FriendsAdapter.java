package com.ncnf.friends.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.ncnf.R;
import com.ncnf.user.FriendsRepository;
import com.ncnf.user.User;
import com.ncnf.user.UserAdapter;

import java.util.List;

public class FriendsAdapter extends UserAdapter {

    public enum FriendsType{
        NONE, ADDABLE, REMOVABLE
    }

    private final FriendsRepository friendsRepository;
    private final String uuid;
    private final FriendsType friendsType;

    public FriendsAdapter(Context context, List<User> users, OnItemClickListener onClick, FriendsRepository friendsRepository, String my_uuid, FriendsType friendsType) {
        super(context, users, onClick);
        this.friendsRepository = friendsRepository;
        this.uuid = my_uuid;
        this.friendsType = friendsType;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_card_view, parent, false);
        return new FriendsViewHolder(v, friendsRepository, uuid, friendsType);
    }

    public class FriendsViewHolder extends UserAdapter.UserViewHolder {

        private final FriendsRepository friendsRepository;
        private final String myUuid;
        private final FriendsType friendType;
        public final MaterialButton friendCardButton;

        public FriendsViewHolder(View v, FriendsRepository friendsRepository, String myUuid, FriendsType friendType) {
            super(v);

            this.friendCardButton = v.findViewById(R.id.friend_card_button);
            this.friendsRepository = friendsRepository;
            this.myUuid = myUuid;
            this.friendType = friendType;
        }

        public void bind(final User u, final UserAdapter.OnItemClickListener onClick) {
            super.bind(u, onClick);

            if(friendType == FriendsType.ADDABLE){
                friendCardButton.setIconResource(R.drawable.ic_baseline_person_add_alt_1_24);
                friendCardButton.setOnLongClickListener(v -> {
                    sendAddFriendRequest(u);
                    return false;
                });
            } else if(friendType == FriendsType.REMOVABLE){
                friendCardButton.setIconResource(R.drawable.ic_baseline_remove_circle_24);
                friendCardButton.setOnLongClickListener(v -> {
                    removeFriend(u);
                    return false;
                });
            } else {
                friendCardButton.setVisibility(View.GONE);
            }
        }

        private void sendAddFriendRequest(User u){
            friendsRepository.request(myUuid, u.getUuid()).thenAccept(response -> {
                showDoneButton();
            }).exceptionally(exception -> null);
        }

        private void removeFriend(User u){
            friendsRepository.removeFriend(myUuid, u.getUuid()).thenAccept(response -> {
                removeItem(u);
                showDoneButton();
            }).exceptionally(exception -> null);
        }

        private void showDoneButton(){
            friendCardButton.setEnabled(false);
            friendCardButton.setIconResource(R.drawable.ic_baseline_done_24);
            friendCardButton.setIconTintResource(R.color.quantum_googgreen);
        }

    }
}