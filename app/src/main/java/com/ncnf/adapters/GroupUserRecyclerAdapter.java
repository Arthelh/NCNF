package com.ncnf.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

import java.util.ArrayList;
import java.util.List;

public class GroupUserRecyclerAdapter extends RecyclerView.Adapter<GroupUserRecyclerAdapter.ViewHolder> {

    private List<String> users;

    public GroupUserRecyclerAdapter(ArrayList<String> users) {
        this.users = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public GroupUserRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_user_row, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupUserRecyclerAdapter.ViewHolder holder, int position) {
        ((ViewHolder)holder).username.setText(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addUser(String name) {
        if(!users.contains(name)) {
            users.add(name);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.group_user_name);
        }
    }
}
