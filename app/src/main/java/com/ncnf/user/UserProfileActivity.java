package com.ncnf.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.ncnf.R;
import com.ncnf.bookmark.BookMarkActivity;
import com.ncnf.main.MainActivity;
import com.ncnf.notification.Registration;

import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class UserProfileActivity extends AppCompatActivity {

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

    private boolean hasNotifications = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        email = findViewById(R.id.userProfileEmail);
        firstName = findViewById(R.id.userProfileFirstName);
        lastName = findViewById(R.id.userProfileLastName);
        birthDate = findViewById(R.id.userProfileDateOfBirth);
        notification_switch = findViewById(R.id.profile_notification_switch);

        addTextWatcherFirstName();
        addTextWatcherLastName();
        addTextWatcherBirthDate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.user.loadUserFromDB().thenAccept(u -> {
            if (user != null) {
                setupNotificationSwitch();

                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                birthDate.setText(user.getBirthDate().toString());
                findViewById(R.id.userProfileSaveButton).setEnabled(false);
                email.setEnabled(false);
                email.setText(user.getEmail());
            }
        });
    }

    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
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
                    findViewById(R.id.userProfileSaveButton).setEnabled(true);
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
                    findViewById(R.id.userProfileSaveButton).setEnabled(true);
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
                    findViewById(R.id.userProfileSaveButton).setEnabled(true);
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
                findViewById(R.id.userProfileSaveButton).setEnabled(false);
            }).exceptionally(exception -> {
                findViewById(R.id.userProfileSaveButton).setEnabled(true);
                return null;
            });
        }
    }



    private void setupNotificationSwitch() {
        Snackbar errorMsg = Snackbar.make(findViewById(R.id.userProfileRoot), "An error happened! Try again later", LENGTH_SHORT);
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

    public void openBookmark(View view){
        startActivity(new Intent(this, BookMarkActivity.class));
    }
}