package com.ncnf.user.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.ncnf.R;


public class ProfileAdapter extends FirebaseRecyclerAdapter<Profile, ProfileAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Profile item);
    }

    private final OnItemClickListener listener;

    public ProfileAdapter(FirebaseRecyclerOptions<Profile> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_public_profile_view, parent, false);
        return new ViewHolder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Profile model) {
        holder.bind(model, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView username;

        public ViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.name);
        }

        public void bind(final Profile item, final OnItemClickListener listener) {
            username.setText(item.getUsername());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}
