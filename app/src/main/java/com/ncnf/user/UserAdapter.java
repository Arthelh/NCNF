package com.ncnf.user;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.storage.FileStore;
import com.ncnf.utilities.InputValidator;
import com.ncnf.utilities.StringCodes;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    protected final Context context;
    private final List<User> users;
    private final UserAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public UserAdapter(Context context, List<User> users, OnItemClickListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    public void setUsers(List<User> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        notifyDataSetChanged();
    }

    public void addItem(User u){
        this.users.add(u);
        notifyDataSetChanged();
    }

    public void removeItem(User u){
        this.users.remove(u);
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

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView username;
        private final TextView profilePictureText;
        private final ImageView profilePicture;

        Activity parentActivity;

        public UserViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.user_card_view_name);
            username = v.findViewById(R.id.user_card_view_username);
            profilePictureText = v.findViewById(R.id.profile_picture_placeholder);
            profilePicture = v.findViewById(R.id.profile_picture);
        }

        public void bind(final User u, final UserAdapter.OnItemClickListener listener){
            // Set item listener
            setItemListener(u, listener);

            // Set car text fields
            setTextFields(u);

            // Set profile picture
            setProfilePicture(u);
        }

        private void setTextFields(User u){
            String firstNameText = u.getFirstName();
            if(InputValidator.isInvalidString(firstNameText)) firstNameText = "empty";
            String lastNameText = u.getLastName();
            if(InputValidator.isInvalidString(lastNameText)) lastNameText = "empty";
            String usernameText = u.getUsername();

            if(InputValidator.isInvalidString(usernameText)) usernameText = "empty";

            String concatNameText = firstNameText + " " + lastNameText;
            name.setText(concatNameText);

            String initialsText = firstNameText.toUpperCase().charAt(0) + String.valueOf(lastNameText.toUpperCase().charAt(0));
            profilePictureText.setText(initialsText);

            username.setText(usernameText);
        }

        private void setProfilePicture(User u){
//            FileStore file = new CacheFileStore(context, Utils.USER_IMAGE_PATH, u.getUuid() + ".jpg");
            FileStore file = new CacheFileStore(context, StringCodes.USER_IMAGE_PATH, "default.jpg"); // TODO : Change to actual PP
            file.downloadImage(profilePicture);
            profilePictureText.setVisibility(View.INVISIBLE);
        }

        private void setItemListener(User u, OnItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(u));
        }
    }
}
