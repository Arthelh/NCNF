package com.ncnf.views.fragments.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

    private EditText orgName;
    private EditText orgEmail;
    private EditText orgPhone;
    private EditText orgAddress;
    private ImageView orgPicture;

    public OrganizationViewFragment(){
        String uuid = getArguments().getString("organization_id");
        organizationRepository.getByUUID(uuid).thenAccept(o ->
                this.organization = o.get(0)
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organization_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        fillViews();
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
