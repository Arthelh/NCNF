package com.ncnf.adapters;

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
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.models.User;
import com.ncnf.utilities.InputValidator;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.graphics.BitmapFactory.decodeResource;
import static com.ncnf.utilities.StringCodes.USER_IMAGE_PATH;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    protected final Context context;
    private final List<User> users;
    private final UserListAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public UserListAdapter(Context context, List<User> users, OnItemClickListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }


    /**
     * Add a list of users to the user list
     * @param newUsers List of users to add
     */
    public void setUsers(List<User> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        notifyDataSetChanged();
    }

    /**
     * Add a User to the user list and notify the observers
     * @param u User to add
     */
    public void addItem(User u){
        this.users.add(u);
        notifyDataSetChanged();
    }

    /**
     * Remove a User to the user list and notify the observers
     * @param u User to remove
     */
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
            profilePicture = v.findViewById(R.id.event_picture);
        }

        public void bind(final User u, final UserListAdapter.OnItemClickListener listener){
            // Set item listener
            setItemListener(u, listener);

            // Set car text fields
            setTextFields(u);

            // Set profile picture
            setProfilePicture(u);
        }

        private void setTextFields(User u){
            String fullNameText = u.getFullName();
            if(InputValidator.isInvalidString(fullNameText)) fullNameText = "empty";

            String usernameText = u.getUsername();
            if(InputValidator.isInvalidString(usernameText)) usernameText = "empty";

            name.setText(fullNameText);

            String initialsText = fullNameText.substring(0, 2);
            profilePictureText.setText(initialsText);

            username.setText(usernameText);
        }

        private void setProfilePicture(User u){
            FirebaseCacheFileStore fileStore = new FirebaseCacheFileStore();
            fileStore.setContext(context);
            fileStore.setPath(USER_IMAGE_PATH, u.getUuid() + ".jpg");
            fileStore.downloadImage(profilePicture, decodeResource(context.getResources(), R.drawable.default_profile_picture));
        }

        private void setItemListener(User u, OnItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(u));
        }
    }
}
