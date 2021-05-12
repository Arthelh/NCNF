package com.ncnf.user.organization.unused;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ncnf.R;
import com.ncnf.user.organization.Organization;
import com.ncnf.user.organization.OrganizationViewFragment;
import com.ncnf.user.organization.unused.OrganizationTokenFragment;


public class OrganizationViewPlusToken extends Fragment {

    private Organization organization;

    public OrganizationViewPlusToken(Organization organization){
        this.organization = organization;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organization_view_token, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_display_organization, new OrganizationViewFragment(organization));
        transaction.add(R.id.fragment_display_token, new OrganizationTokenFragment(organization));
        transaction.commit();
    }
}
