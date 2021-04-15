package com.ncnf.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ncnf.R;

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
    protected void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position, @NonNull PrivateUser model) {
        holder.bind(model, listener);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_view, parent, false);
        return new UserViewHolder(v);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public UserViewHolder(View v) {
            super(v);
        }

        public void bind(final PrivateUser u, final UserAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(u));
        }
    }
}
