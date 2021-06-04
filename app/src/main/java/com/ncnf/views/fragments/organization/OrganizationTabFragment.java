package com.ncnf.views.fragments.organization;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.models.Organization;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.models.User;
import com.ncnf.adapters.OrganizationListAdapter;
import com.ncnf.utilities.InputValidator;
import com.ncnf.views.activities.organization.OrganizationProfileActivity;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrganizationTabFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public OrganizationRepository organizationRepository;

    //Needed for Popup
    private LayoutInflater layoutInflater;
    private FragmentManager fm;

    private MenuItem addOrgButton;

    public static final String ORGANIZATION_UUID_KEY = "organization_id";

    private TextView emptyView;
    private RecyclerView recycler;
    OrganizationListAdapter adapter;

    private List<Organization> organizations = new LinkedList<>();

    public OrganizationTabFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.layoutInflater = inflater;
        this.fm = getParentFragmentManager();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_organizations_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO check error
        // fm.beginTransaction().addToBackStack(FRAGMENT_ORGANIZATION_TAG);

        recycler = requireView().findViewById(R.id.organization_list_recyclerview);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(requireActivity());
        recycler.setLayoutManager(lm);

        emptyView = requireView().findViewById(R.id.empty_organization_view);
        
        organizations = new LinkedList<>();

        adapter = new OrganizationListAdapter(requireContext(), organizations, this::onOrganizationClick);
        recycler.setAdapter(adapter);

        organizationRepository.getUserOrganizations(user.getUuid()).thenAccept(org -> {
            organizations = org;
            adapter.setOrganizations(organizations);
            updateVisibility();
        });

    }

    private void updateVisibility(){
        if (organizations.isEmpty()) {
            recycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void onOrganizationClick(Organization o) {
        Intent intent = new Intent(this.getContext(), OrganizationProfileActivity.class);
        intent.putExtra(ORGANIZATION_UUID_KEY, o.getUuid().toString());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.organization_menu, menu);
        addOrgButton = menu.findItem(R.id.add_organization_button);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_organization_button) {
            organizationSearchAlertDialogue();
        }
        return super.onOptionsItemSelected(item);
    }

    private void organizationSearchAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        final EditText organizationNameOrIdInput = new EditText(requireActivity());

        builder.setTitle("Enter name or Id of organization")
                .setView(organizationNameOrIdInput)
                .setPositiveButton("Enter", null)
                .setNegativeButton("Cancel", null)
                .setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new PositiveListener(alertDialog, organizationNameOrIdInput));
    }

    class PositiveListener implements View.OnClickListener {
        private final EditText inputText;
        private final AlertDialog dialog;

        public PositiveListener(AlertDialog dialog, EditText inputText) {
            this.inputText = inputText;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {

            if (verifyTextInput(inputText)) {
                String token = inputText.getText().toString();
                organizationRepository.getOrganizationsWithToken(token).thenAccept(o -> {
                    if (o.size() == 1) {
                        organizationRepository.addUserToOrganization(user.getUuid(), o.get(0).getUuid().toString());
                        adapter.addOrganization(o.get(0));
                        updateVisibility();
                    } else {
                        throw new IllegalStateException("Too many organizations using the same token");
                    }
                }).exceptionally(e -> {
                    displayPopUp("No organization found");
                    return null;
                });
                dialog.dismiss();
            }
        }
    }

    private boolean verifyTextInput(EditText input) {
        if (InputValidator.isInvalidString(input.getText().toString())) {
            InputValidator.setErrorMsg(input, "Token cannot be empty");
            return false;
        }
        return true;
    }

    private void displayPopUp(String errorText) {

        // inflate the layout of the popup window
        View popupView = layoutInflater.inflate(R.layout.popup_invalid_organization, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        //TODO CHANGE

        // which view you pass in doesn't matter, it is only used for the window token
        //
        popupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0);

        ((TextView) popupView.findViewById(R.id.popup_invalid_organization_text)).setText(errorText);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
