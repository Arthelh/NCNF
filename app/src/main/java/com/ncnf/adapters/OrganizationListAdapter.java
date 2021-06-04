package com.ncnf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.ncnf.R;
import com.ncnf.models.Organization;
import com.ncnf.models.User;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.graphics.BitmapFactory.decodeResource;
import static com.ncnf.utilities.StringCodes.ORGANIZATIONS_IMAGE_PATH;
import static com.ncnf.utilities.StringCodes.USER_IMAGE_PATH;

public class OrganizationListAdapter extends RecyclerView.Adapter<OrganizationListAdapter.OrganizationViewHolder> {

    private final Context context;

    private List<Organization> organizations;
    private final OrganizationListener organizationListener;

    public interface OrganizationListener {
        void onOrganizationClick(Organization organization);
    }

    public OrganizationListAdapter(Context context, List<Organization> organizations, OrganizationListener organizationListener) {
        //ensure proper copy of the List
        this.context = context;
        this.organizations = new LinkedList<>(organizations);
        this.organizationListener = organizationListener;
    }

    public void addOrganization(Organization organization) {
        // Add the organization at the beginning of the list
        organizations.add(0, organization);
        // Notify the insertion so the view can be refreshed
        notifyItemInserted(0);
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return organizations.size();
    }

    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.organization_card_view, viewGroup, false);

        return new OrganizationViewHolder(v, organizationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {
        holder.bind(organizations.get(position), organizationListener);
    }

    public class OrganizationViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        private final ImageView organizationPicture;
        private final TextView organizationEmail;
        private final TextView organizationName;

        public OrganizationViewHolder(View v, OrganizationListener e) {
            super(v);
            organizationName = v.findViewById(R.id.organization_card_view_name);
            organizationEmail = v.findViewById(R.id.organization_card_view_email);
            organizationPicture = v.findViewById(R.id.organization_profile_full_name);
            //add timestamp
        }

        public void bind(final Organization o, final OrganizationListener listener) {
            organizationName.setText(o.getName());
            organizationEmail.setText(o.getEmail());
            setOrganizationPicture(o);
            //organizationPicture s
            itemView.setOnClickListener(v -> listener.onOrganizationClick(o));
        }

        private void setOrganizationPicture(Organization o){
            FirebaseCacheFileStore fileStore = new FirebaseCacheFileStore();
            fileStore.setContext(context);
            fileStore.setPath(ORGANIZATIONS_IMAGE_PATH, o.getUuid() + ".jpg");
            fileStore.downloadImage(organizationPicture, decodeResource(context.getResources(), R.drawable.default_profile_picture));
        }
    }
}
