package com.ncnf.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ncnf.R;
import com.ncnf.Utils;

import io.grpc.stub.StreamObserver;

public class UserAdapter extends FirestoreRecyclerAdapter<PrivateUser, UserAdapter.UserViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PrivateUser item);
    }

    private final UserAdapter.OnItemClickListener listener;

    public UserAdapter(FirestoreRecyclerOptions<PrivateUser> options, UserAdapter.OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position, @NonNull PrivateUser user) {
        holder.bind(user, listener);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_view, parent, false);
        return new UserViewHolder(v);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView username;
        private final TextView profilePictureText;

        public UserViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.user_card_view_name);
            username = v.findViewById(R.id.user_card_view_username);
            profilePictureText = v.findViewById(R.id.profile_picture_placeholder);
        }

        public void bind(final PrivateUser u, final UserAdapter.OnItemClickListener listener) {
            String firstNameText = (String) u.getUserData().getOrDefault(Utils.FIRST_NAME_KEY, "empty");
            if(firstNameText == "") firstNameText = "empty";
            String lastNameText = (String) u.getUserData().getOrDefault(Utils.LAST_NAME_KEY, "empty");
            if(lastNameText == "") lastNameText = "empty";
            String usernameText = (String) u.getUserData().getOrDefault("@"+Utils.LAST_NAME_KEY, "@empty");
            if(usernameText == "") usernameText = "empty";
            
            String concatNameText = firstNameText + " " + lastNameText;
            name.setText(concatNameText);

            String initialsText = String.valueOf(firstNameText.toUpperCase().charAt(0)) + String.valueOf(lastNameText.toUpperCase().charAt(0));
            profilePictureText.setText(initialsText);

            username.setText(usernameText);

            itemView.setOnClickListener(v -> listener.onItemClick(u));
        }
    }
}
