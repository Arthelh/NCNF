package com.ncnf.user.organization.unused;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ncnf.R;
import com.ncnf.user.organization.Organization;
import com.ncnf.user.organization.OrganizationTabFragment;
import com.ncnf.user.organization.OrganizationViewFragment;

import static com.ncnf.utilities.StringCodes.*;

public class OrganizationTokenFragment extends OrganizationViewFragment {

    private EditText tokenBox;

    public OrganizationTokenFragment(Organization organization) {
        super(organization);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organization_view_token, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenBox = requireView().findViewById(R.id.organization_enter_token);
        Button validateButton = requireView().findViewById(R.id.organization_validate_button);

        validateButton.setOnClickListener(v -> registerOrganization());
    }

    private void registerOrganization() {
        String token = tokenBox.getText().toString();
        if (verifyToken(token)){

            //TODO add AlertDialogue to inform the user it worked

            FragmentManager fm = requireActivity().getSupportFragmentManager();
            Fragment organizationListFragment = fm.findFragmentByTag(FRAGMENT_ORGANIZATION_TAG);

            //Sanity check
            if(organizationListFragment instanceof OrganizationTabFragment){
                fm.beginTransaction()
                        .replace(((ViewGroup) requireView().getParent()).getId(), organizationListFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                throw new IllegalStateException("Reached this fragment illegally");
            }
        } else {
            tokenBox.setError("Invalid Token");
        }
    }

    //TODO set standard for token verification
    private boolean verifyToken(String token){
        return true;
    }

}
