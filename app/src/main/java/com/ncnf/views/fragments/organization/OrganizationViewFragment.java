package com.ncnf.views.fragments.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.ncnf.R;
import com.ncnf.models.Organization;
import com.ncnf.repositories.OrganizationRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrganizationViewFragment extends Fragment {

    @Inject
    public OrganizationRepository organizationRepository;

    private Organization organization;
    private String uuid;

    private MaterialTextView orgName;
    private MaterialTextView orgEmail;
    private MaterialTextView orgPhone;
    private MaterialTextView orgAddress;
    
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
        orgName = requireView().findViewById(R.id.organization_display_name);
        orgAddress = requireView().findViewById(R.id.organization_display_postal);
        orgEmail = requireView().findViewById(R.id.organization_display_email);
        orgPhone = requireView().findViewById(R.id.organization_display_phone);
        orgPicture = requireView().findViewById(R.id.organization_display_picture);
    }

    private void fillViews(){
        orgName.setText(organization.getName());
        orgAddress.setText(organization.getAddress());
        orgEmail.setText(organization.getEmail());
        orgPhone.setText(organization.getPhoneNumber());
        //TODO add picture;
        //orgPicture
    }
}
