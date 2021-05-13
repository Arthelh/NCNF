package com.ncnf.organization;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.user.User;
import com.ncnf.organization.helpers.OrganizationAdapter;
import com.ncnf.utilities.InputValidator;

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

    private List<Organization> organizations;

    public OrganizationTabFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.layoutInflater = inflater;
        this.fm = getChildFragmentManager();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_organizations_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO check error
       // fm.beginTransaction().addToBackStack(FRAGMENT_ORGANIZATION_TAG);

        recycler = requireView().findViewById(R.id.organization_list_recyclerview);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lm);

        emptyView = requireView().findViewById(R.id.empty_organization_view);

        organizations = new LinkedList<>();
        //TODO Handle exceptions
        organizationRepository.getUserOrganizations(user.getUuid())
                .thenApply(organizations::addAll)
                .exceptionally(e -> null);

        //Set visibility
        if (organizations.isEmpty()) {
            recycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        OrganizationAdapter adapter = new OrganizationAdapter(organizations, this::onOrganizationClick);
        recycler.setAdapter(adapter);
    }

    private void onOrganizationClick(Organization o) {

        //Check if fragment already exists
        String orgViewTag = generatePerViewID(o);
        Fragment orgViewFrag = fm.findFragmentByTag(orgViewTag);

        //It doesn't so create new corresponding Fragment
        if(!(orgViewFrag instanceof OrganizationViewFragment)) {
            orgViewFrag = new OrganizationViewFragment(o);
        }
        fm.beginTransaction()
                .replace(((ViewGroup) requireView().getParent()).getId(), orgViewFrag, orgViewTag)
                .addToBackStack(FRAGMENT_ORGANIZATION_TAG)
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.organization_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    //TODO OVERRIDE DEFAULT BACK ARROW
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //No switch case because R.ids won't be supported in switch case stmts soon
        if (id == R.id.add_organization_button) {
            organizationSearchAlertDialogue();
        }
        return super.onOptionsItemSelected(item);
    }

    private void organizationSearchAlertDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        final EditText organizationNameOrIdInput = new EditText(requireActivity());

        builder.setTitle("Enter name or Id of organization")
                .setView(organizationNameOrIdInput)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true);
        AlertDialog alertDialog = builder.create();
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
            //TODO : aend token

            //Unused
           /* if(verifyTextInput(inputText)) {

                List<Organization> organizations = new LinkedList<>();
                user.getOrganizationWithName(inputText.getText().toString()).thenApply(organizations::addAll).exceptionally(exception -> {
                    return null; // TODO : handle exception
                });

                Optional<Organization> organization = verifyDatabaseResults(organizations);

                if(organization.isPresent()) {
                    //Prepare Fragment
                    OrganizationViewPlusToken organizationViewAndToken = new OrganizationViewPlusToken(organization.get());
                    fm.beginTransaction()
                            .replace(((ViewGroup) requireView().getParent()).getId(), organizationViewAndToken)
                            .commit();
                }
            }*/
            //dialog.dismiss();
        }
    }



    private boolean verifyTextInput(EditText input){
        if (InputValidator.verifyGenericInput(input)){
            InputValidator.setErrorMsg(input, "Token cannot be empty");
            return false;
        }
        //TODO add additional checks
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
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        ((TextView)popupView.findViewById(R.id.popup_invalid_organization_text)).setText(errorText);

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

// ===========================================================================================//

//UNUSED


  /*  private Optional<Organization> verifyDatabaseResults(List<Organization> organizations){
       if(organizations.size() == 1) {
           return Optional.of(organizations.get(0));
       } else {
            displayPopUp(getView(), (organizations.size() == 0) ? "No Organizations found" : "Narrow down search with more precise name");
            return Optional.empty();
       }
    }*/


   /* private void moveToCreateOrganization(){
        emptyView.setVisibility(View.GONE);
        OrganizationCreateFragment organizationCreateFragment = new OrganizationCreateFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(((ViewGroup) requireView().getParent()).getId(), organizationCreateFragment, FRAGMENT_ORGANIZATION_CREATION_TAG)
                .addToBackStack(null)
                .commit();
    }*/

    /*@Override
    public void onHiddenChanged(boolean hidden) {
        Log.d("BBB", "FvreihhrgeioegrhhigiergogizreirgezgrzogerzgreF1");
        Fragment currentFragment = fm.findFragmentByTag("f2");
        if(fm.getBackStackEntryCount() == 0 && currentFragment instanceof OrganizationTabFragment){
            if (organizations.isEmpty()) {
                recycler.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recycler.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
    }
*/