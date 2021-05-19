package com.ncnf.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.models.Organization;

import java.util.LinkedList;
import java.util.List;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrganizationViewHolder> {

    private final List<Organization> items;
    private final OrganizationListener organizationListener;

    public interface OrganizationListener {
        void onEventClick(Organization organization);
    }

    public OrganizationAdapter(List<Organization> items, OrganizationListener organizationListener) {
        //ensure proper copy of the List
        this.items = new LinkedList<>(items);
        this.organizationListener = organizationListener;
    }

    public void addOrganization(Organization organization) {
        // Add the organization at the beginning of the list
        items.add(0, organization);
        // Notify the insertion so the view can be refreshed
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.organization_row, viewGroup, false);

        return new OrganizationViewHolder(v, organizationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {
        holder.bind(items.get(position), organizationListener);
    }

    public static class OrganizationViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        private final ImageView organizationPicture;
        private final TextView organizationName;

        public OrganizationViewHolder(View v, OrganizationListener e) {
            super(v);
            organizationName = v.findViewById(R.id.organization_row_name);
            organizationPicture = v.findViewById(R.id.organization_row_picture);
            //add timestamp
        }

        public void bind(final Organization o, final OrganizationListener listener) {
            organizationName.setText(o.getName());
            //TODO Set picture
            //organizationPicture s
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEventClick(o);
                }
            });
        }
    }
}
