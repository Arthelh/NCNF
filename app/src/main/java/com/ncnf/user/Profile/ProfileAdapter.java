package com.ncnf.user.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.ncnf.R;


public class ProfileAdapter extends FirestoreRecyclerAdapter<Profile, ProfileAdapter.ProfileViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Profile item);
    }

    private final OnItemClickListener listener;

    public ProfileAdapter(FirestoreRecyclerOptions<Profile> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_public_profile_view, parent, false);
        return new ProfileViewHolder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull Profile model) {
        holder.bind(model, listener);
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private final TextView username;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_name);
        }


        public void bind(final Profile item, final OnItemClickListener listener) {
            username.setText((item != null) ? item.getUsername() : "No User with that name found");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}
