package com.ncnf.views.fragments.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ncnf.R;
import com.ncnf.models.Organization;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.graphics.BitmapFactory.decodeResource;
import static com.ncnf.utilities.StringCodes.ORGANIZATIONS_IMAGE_PATH;

@AndroidEntryPoint
public class OrganizationViewFragment extends Fragment {

    @Inject
    public OrganizationRepository organizationRepository;

    @Inject
    public FirebaseCacheFileStore fileStore;

    private Organization organization;
    private String uuid;

    private TextView orgName;
    private TextView orgEmail;
    private TextView orgPhone;
    private TextView orgAddress;
    
    private ImageView orgPicture;

    public OrganizationViewFragment(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.uuid = this.getArguments().getString("organization_id");
        return inflater.inflate(R.layout.fragment_organization_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        organizationRepository.getByUUID(uuid).thenAccept(o -> {
            this.organization = o.get(0);
            fillViews();
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    private void initView(){
        orgName = requireView().findViewById(R.id.organization_profile_full_name);
        orgAddress = requireView().findViewById(R.id.organization_profile_address);
        orgEmail = requireView().findViewById(R.id.organization_profile_email);
        orgPhone = requireView().findViewById(R.id.organization_profile_phone);
        orgPicture = requireView().findViewById(R.id.organization_profile_picture_image);
    }

    private void fillViews(){
        orgName.setText(organization.getName());
        orgAddress.setText(organization.getAddress());
        orgEmail.setText(organization.getEmail());
        orgPhone.setText(organization.getPhoneNumber());
        fileStore.setContext(this.getContext());
        fileStore.setPath(ORGANIZATIONS_IMAGE_PATH, organization.getUuid() + ".jpg");
        fileStore.downloadImage(orgPicture, decodeResource(this.getResources(), R.drawable.default_profile_picture));
        //orgPicture
    }
}
