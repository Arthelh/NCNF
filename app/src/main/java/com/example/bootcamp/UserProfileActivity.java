package com.example.bootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import static com.example.bootcamp.Utils.*;

public class UserProfileActivity extends AppCompatActivity {

    private PrivateUser user;
    private boolean firstNameChanged;
    private boolean lastNameChanged;
    private boolean birthDateChanged;
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
        this.prepareFields();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            user = PrivateUser.getInstance();
            user.loadBD(new DatabaseLambda() {
                @Override
                public void applyAfterLoad(Map<String, Object> map) {
                    String first_name = map.getOrDefault(FIRST_NAME_KEY, EMPTY_STRING).toString();
                    String last_name = map.getOrDefault(LAST_NAME_KEY, EMPTY_STRING).toString();
                    String birth_date = map.getOrDefault(BIRTH_YEAR_KEY, EMPTY_STRING).toString();

                    firstName.setText(first_name);
                    lastName.setText(last_name);
                    birthDate.setText(birth_date);
                }
            });
            fillEmailAddress();
        }
    }

    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }

    private void fillEmailAddress(){
        email.setEnabled(false);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


    }

    private void prepareFields(){
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

    public void saveNewFields(View view){

        Log.d(MainActivity.TAG, "we have changed first : " + firstNameChanged + " last : " + lastNameChanged + " birth : " + birthDateChanged);

        if(firstNameChanged){
            this.user.updateFirstName(firstName.getText().toString());
            firstNameChanged = false;
        }

        if(lastNameChanged){
            this.user.updateLastName(lastName.getText().toString());
            lastNameChanged = false;
        }
        if(birthDateChanged){
            this.user.updateBirth(Integer.parseInt(birthDate.getText().toString()));
            birthDateChanged = false;

        }
        findViewById(R.id.userProfileSaveButton).setEnabled(false);
    }
}