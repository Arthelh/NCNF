package com.ncnf.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.main.MainActivity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.BIRTH_YEAR_KEY;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;

@AndroidEntryPoint
public class UserProfileActivity extends AppCompatActivity {

    @Inject
    public PrivateUser user;

    private boolean firstNameChanged = false;
    private boolean lastNameChanged = false;
    private boolean birthDateChanged = false;
    EditText email;
    EditText firstName;
    EditText lastName;
    EditText birthDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        email = findViewById(R.id.userProfileEmail);
        firstName = findViewById(R.id.userProfileFirstName);
        lastName = findViewById(R.id.userProfileLastName);
        birthDate = findViewById(R.id.userProfileDateOfBirth);


        addTextWatcherFirstName();
        addTextWatcherLastName();
        addTextWatcherBirthDate();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        if(user != null){
             CompletableFuture<DatabaseResponse> future = user.loadUserFromDB();
             future.thenAccept(task -> {

                if(task.isSuccessful()) {
                    Map<String, Object> map = (Map<String, Object>) task.getResult();
                    String first_name = map.get(FIRST_NAME_KEY).toString();
                    String last_name = map.get(LAST_NAME_KEY).toString();
                    String birth_date = map.get(BIRTH_YEAR_KEY).toString();
                    // TODO: include email in the request
                    String user_email = user.getEmail();

                    firstName.setText(first_name);
                    lastName.setText(last_name);
                    birthDate.setText(birth_date);
                    findViewById(R.id.userProfileSaveButton).setEnabled(false);
                    email.setEnabled(false);
                    email.setText(user_email);
                } else {
                    Log.d(DEBUG_TAG, "Unable to load user from db : " + task.getException().getMessage());
                }
            });
        }

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
                    birthDateChanged = true;
                }
            }
        });
    }

    // TODO: find a way to refactor -> save multiple fields at the time but how ?
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveNewFields(View view){
        findViewById(R.id.userProfileSaveButton).setEnabled(false);

        if(firstNameChanged){
            saveFirstName();
        }

        if(lastNameChanged){
            saveLastName();
        }

        if(birthDateChanged){
            saveBirthDate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveFirstName(){
        CompletableFuture<DatabaseResponse> changed = this.user.updateFirstName(firstName.getText().toString());
        changed.thenAccept(task -> {
            if(task.isSuccessful()) {
                firstNameChanged = false;
            } else {
                findViewById(R.id.userProfileSaveButton).setEnabled(true);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveLastName(){
        CompletableFuture<DatabaseResponse> changed = this.user.updateLastName(lastName.getText().toString());
        changed.thenAccept(task -> {
            if(task.isSuccessful()){
                lastNameChanged = false;
            } else {
                findViewById(R.id.userProfileSaveButton).setEnabled(true);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveBirthDate(){
        CompletableFuture<DatabaseResponse> changed = this.user.updateBirth(Integer.parseInt(birthDate.getText().toString()));
        changed.thenAccept(task -> {
            if(task.isSuccessful()){
                birthDateChanged = false;
            } else {
                findViewById(R.id.userProfileSaveButton).setEnabled(true);
            }
        });
    }
}