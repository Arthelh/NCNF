package com.ncnf.user.organization;

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

public class OrganizationViewFragment extends Fragment {

    private final Organization organization;

    private EditText orgName;
    private EditText orgEmail;
    private EditText orgPhone;
    private EditText orgAddress;
    private ImageView orgPicture;

    public OrganizationViewFragment(Organization organization){
        this.organization = organization;
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
