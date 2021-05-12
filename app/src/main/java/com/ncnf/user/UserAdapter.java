package com.ncnf.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.utilities.InputValidator;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> users;
    private final UserAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User item);
    }

    public UserAdapter(List<User> users, OnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public void setUsers(List<User> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_view, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
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

        public void bind(final User u, final UserAdapter.OnItemClickListener listener) {
            String firstNameText = u.getFirstName();
            if(InputValidator.isInvalidString(firstNameText)) firstNameText = "empty";
            String lastNameText = u.getLastName();
            if(InputValidator.isInvalidString(lastNameText)) lastNameText = "empty";
            String usernameText = u.getUsername();
            if(InputValidator.isInvalidString(usernameText)) usernameText = "empty";
            
            String concatNameText = firstNameText + " " + lastNameText;
            name.setText(concatNameText);

            String initialsText = String.valueOf(firstNameText.toUpperCase().charAt(0)) + String.valueOf(lastNameText.toUpperCase().charAt(0));
            profilePictureText.setText(initialsText);

            username.setText(usernameText);

            itemView.setOnClickListener(v -> listener.onItemClick(u));
        }
    }
}
