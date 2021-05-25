package com.ncnf.views.fragments.organization;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.activity.OnBackPressedCallback;
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
import com.ncnf.utilities.PopUpAlert;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.FRAGMENT_ORGANIZATION_TAG;
import static com.ncnf.utilities.StringCodes.generatePerViewID;

@AndroidEntryPoint
public class OrganizationTabFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public OrganizationRepository organizationRepository;

    //Needed for Popup
    private LayoutInflater layoutInflater;
    private FragmentManager fm;

    private TextView emptyView;
    private RecyclerView recycler;
    OrganizationListAdapter adapter;
    private AlertDialog alertDialog;

    private List<Organization> organizations = new LinkedList<>();
    private Bundle savedInstanceState;

    public OrganizationTabFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
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

        adapter = new OrganizationListAdapter(organizations, this::onOrganizationClick);
        recycler.setAdapter(adapter);

        //TODO Handle exceptions
        organizationRepository.getUserOrganizations(user.getUuid()).thenAccept(org -> {
            organizations = org;
            adapter.setOrganizations(organizations);
            updateVisibility();
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
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

        //Check if fragment already exists
        String orgProfileTag = generatePerViewID(o);
        Fragment orgProfileFrag = fm.findFragmentByTag(orgProfileTag);

        //It doesn't so create new corresponding Fragment
        if (!(orgProfileFrag instanceof OrganizationViewFragment)) {

            Bundle args = new Bundle();
            args.putString("organization_id",o.getUuid().toString());

            orgProfileFrag = new OrganizationProfileTabs();
            requireActivity().getSupportFragmentManager().setFragmentResult("organization_id_key", args);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fm.popBackStack();
                recycler.setVisibility(View.VISIBLE);
                this.setEnabled(false);
            }
        };


        recycler.setVisibility(View.INVISIBLE);
        fm.beginTransaction()
              .hide(this)
              .replace(((ViewGroup) requireView().getParent()).getId(), orgProfileFrag, orgProfileTag)
              .addToBackStack(null)
              .commit();

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
       /* fm.beginTransaction()
                .replace(((ViewGroup) requireView().getParent()).getId(), orgViewFrag, orgViewTag)
                .addToBackStack(FRAGMENT_ORGANIZATION_TAG)
                .commit();*/
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.organization_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    //TODO OVERRIDE DEFAULT BACK ARROW
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
        alertDialog = builder.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new PositiveListener(alertDialog, organizationNameOrIdInput));
    }

    class PositiveListener implements View.OnClickListener {
        private final EditText inputText;

        public PositiveListener(AlertDialog dialog, EditText inputText) {
            this.inputText = inputText;
        }

        @Override
        public void onClick(View v) {

            if (verifyTextInput(inputText)) {
                String token = inputText.getText().toString();
                organizationRepository.getOrganizationsWithToken(token).thenAccept(o -> {
                    int orgSize = o.size();
                    if (o.size() == 1) {
                        organizationRepository.addUserToOrganization(user.getUuid(), o.get(0).getUuid().toString());
                        adapter.addOrganization(o.get(0));
                        updateVisibility();
                    } else {
                        throw new IllegalStateException("Too many organizations using the same token");
                    }
                }).exceptionally(e -> {
                    e.printStackTrace();
                    displayPopUp(v, "No organization found");
                    return null;
                });
                alertDialog.dismiss();
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

    private void displayPopUp(View view, String errorText) {

        // inflate the layout of the popup window
        View popupView = layoutInflater.inflate(R.layout.pop_up_invalide_organization, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        //TODO CHANGE

        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

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
