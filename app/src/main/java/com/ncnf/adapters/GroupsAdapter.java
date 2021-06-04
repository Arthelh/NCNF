package com.ncnf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.models.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder>{


    private Context context;
    private List<Group> groups;
    private final GroupsAdapter.OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(Group grp);
    }

    public GroupsAdapter(Context context, List<Group> groups, GroupsAdapter.OnGroupClickListener listener) {
        this.context = context;
        this.groups = new ArrayList<>(groups);
        this.listener = listener;
    }

    public void setGroups(List<Group> newGroup) {
        this.groups.clear();
        this.groups.addAll(newGroup);
        notifyDataSetChanged();
    }

    public void removeItem(Group g){
        this.groups.remove(g);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row, parent, false);
        GroupsAdapter.ViewHolder viewHolder = new GroupsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.ViewHolder holder, int position) {
        holder.bind(groups.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView groupName;
        TextView groupParticipants;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupName = itemView.findViewById(R.id.group_name);
            groupParticipants = itemView.findViewById(R.id.group_num);

        }

        public void bind(final Group g, final GroupsAdapter.OnGroupClickListener listener){
            // Set item listener
            setItemListener(g, listener);

            // Set text fields
            setTextFields(g);

        }

        private void setTextFields(Group g) {
            groupName.setText(g.getName());
            int num = g.getMembers().size();
            groupParticipants.setText(num + " participants");
        }

        private void setItemListener(Group g, GroupsAdapter.OnGroupClickListener listener){
            itemView.setOnClickListener(v -> listener.onGroupClick(g));
        }
    }
}
