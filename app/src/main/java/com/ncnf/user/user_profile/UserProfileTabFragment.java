package com.ncnf.user.user_profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.ncnf.R;
import com.ncnf.main.MainActivity;
import com.ncnf.notification.Registration;
import com.ncnf.user.User;

import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class UserProfileTabFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public Registration registration;

    private boolean firstNameChanged = false;
    private boolean lastNameChanged = false;
    private boolean birthDateChanged = false;

    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private EditText birthDate;
    private Switch notification_switch;
    private Button saveButton;
    private Button logoutButton;

    private boolean hasNotifications = false;


    public UserProfileTabFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        initUser();
    }

    private void initViews(){
        email = requireView().findViewById(R.id.userProfileEmail);
        firstName = requireView().findViewById(R.id.userProfileFirstName);
        lastName = requireView().findViewById(R.id.userProfileLastName);
        birthDate = requireView().findViewById(R.id.userProfileDateOfBirth);
        notification_switch = requireView().findViewById(R.id.profile_notification_switch);
        saveButton = requireView().findViewById(R.id.userProfileSaveButton);
        logoutButton = requireView().findViewById(R.id.logOutButton);

        addTextWatcherFirstName();
        addTextWatcherLastName();
        addTextWatcherBirthDate();

        saveButton.setOnClickListener(this::saveNewFields);
        logoutButton.setOnClickListener(this::logOut);
    }

    private void initUser() {
        this.user.loadUserFromDB().thenAccept(u -> {
            if (user != null) {
                setupNotificationSwitch();

                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                birthDate.setText(user.getBirthDate().toString());
                requireView().findViewById(R.id.userProfileSaveButton).setEnabled(false);
                email.setEnabled(false);
                email.setText(user.getEmail());
            }
        });
    }

    public void logOut(View view) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        this.user.signOut();
        startActivity(intent);
    }


    private void addTextWatcherFirstName(){
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!firstName.getText().toString().isEmpty()) {
                    requireView().findViewById(R.id.userProfileSaveButton).setEnabled(true);
                    user.setFirstName(firstName.getText().toString());
                    firstNameChanged = true;
                }
            }
        });
    }

    private void addTextWatcherLastName(){
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!lastName.getText().toString().isEmpty()) {
                    requireView().findViewById(R.id.userProfileSaveButton).setEnabled(true);
                    user.setLastName(lastName.getText().toString());
                    lastNameChanged = true;
                }
            }
        });
    }

    private void addTextWatcherBirthDate(){
        birthDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(!birthDate.getText().toString().isEmpty()){
                    requireView().findViewById(R.id.userProfileSaveButton).setEnabled(true);
                    user.setBirthDate(new Date());
                    birthDateChanged = true;
                }
            }
        });
    }

    // TODO: find a way to refactor -> save multiple fields at the time but how ?
    public void saveNewFields(View view){

        if(firstNameChanged || lastNameChanged || birthDateChanged){
            user.saveUserToDB().thenAccept(result -> {
                requireView().findViewById(R.id.userProfileSaveButton).setEnabled(false);
            }).exceptionally(exception -> {
                requireView().findViewById(R.id.userProfileSaveButton).setEnabled(true);
                return null;
            });
        }
    }



    private void setupNotificationSwitch() {
        Snackbar errorMsg = Snackbar.make(requireView().findViewById(R.id.userProfileRoot), "An error happened! Try again later", LENGTH_SHORT);
        notification_switch.setChecked(hasNotifications);
        notification_switch.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                registration.register().exceptionally(exception -> {
                    errorMsg.show();
                    return null;
                });
            } else {
                registration.unregister().exceptionally(exception -> {
                    errorMsg.show();
                    return null;
                });
            }
        });
    }
}