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

    private boolean firstNameChanged;
    private boolean lastNameChanged;
    private boolean birthDateChanged;
    EditText email;
    EditText firstName;
    EditText lastName;
    EditText birthDate;
    private Intent intent;
    // private final Event event = new PublicEvent("name of event", new Date(), new GeoPoint(1.2, 2.1), "data", "1234", EventCategory.Conference, 0, 0, new ArrayList<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        email = findViewById(R.id.userProfileEmail);
        firstName = findViewById(R.id.userProfileFirstName);
        lastName = findViewById(R.id.userProfileLastName);
        birthDate = findViewById(R.id.userProfileDateOfBirth);
        this.prepareFields();
        // this.intent = new Intent(this, UserBookmark.class);
        // findViewById(R.id.userProfileProgresssBar).setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        if(user != null){
             CompletableFuture<DatabaseResponse> future = user.loadUserFromBD();
             future.thenAccept(task -> {

                if(task.isSuccessful()) {
                    Map<String, Object> map = (Map<String, Object>) task.getResult();
                    String first_name = map.get(FIRST_NAME_KEY).toString();
                    String last_name = map.get(LAST_NAME_KEY).toString();
                    String birth_date = map.get(BIRTH_YEAR_KEY).toString();
                    // TODO: include email in the request
                    String user_email = "foo@bar.com";

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

            // this.event.setOwner(PrivateUser.getInstance().getID());
            // event.storeEventInDB();
        }

    }

    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.user.signOut();
        startActivity(intent);
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

    private void setProgressBar(int visibility){
        // if(!firstNameChanged && !lastNameChanged && !birthDateChanged){
        //   findViewById(R.id.userProfileProgresssBar).setVisibility(View.INVISIBLE);
        // }
    }

    // TODO: find a way to refactor -> save multiple fields at the time but how ?
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveNewFields(View view){
        findViewById(R.id.userProfileSaveButton).setEnabled(false);

        if(firstNameChanged){
            CompletableFuture<DatabaseResponse> changed = this.user.updateFirstName(firstName.getText().toString());
            changed.thenAccept(task -> {
                if(task.isSuccessful()) {
                    firstNameChanged = false;
                } else {
                    findViewById(R.id.userProfileSaveButton).setEnabled(true);
                }
                setProgressBar(View.INVISIBLE);
            });
        }

        if(lastNameChanged){
            CompletableFuture<DatabaseResponse> changed = this.user.updateLastName(lastName.getText().toString());
            changed.thenAccept(task -> {
                if(task.isSuccessful()){
                    lastNameChanged = false;
                } else {
                    findViewById(R.id.userProfileSaveButton).setEnabled(true);
                }
                setProgressBar(View.INVISIBLE);
            });
        }
        if(birthDateChanged){
            CompletableFuture<DatabaseResponse> changed = this.user.updateBirth(Integer.parseInt(birthDate.getText().toString()));
            changed.thenAccept(task -> {
                if(task.isSuccessful()){
                    birthDateChanged = false;
                } else {
                    findViewById(R.id.userProfileSaveButton).setEnabled(true);
                }
                setProgressBar(View.INVISIBLE);
            });

        }
    }

    public void viewBookmark(View view){
        startActivity(intent);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void saveEvent(View view){
//        setProgressBar(View.VISIBLE);
//
//        CompletableFuture<DatabaseResponse> response  = PrivateUser.getInstance().saveEvent(event);
//        response.thenAccept(task -> {
//            setProgressBar(View.INVISIBLE);
//            String alertMessage;
//           if(task.isSuccessful()){
//                alertMessage = "Event has been successfully saved in your bookmark";
//           } else {
//               alertMessage = "Couldn't save this event : please retry";
//           }
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
//           builder.setCancelable(false);
//           builder.setTitle("Saving event");
//           builder.setMessage(alertMessage);
//           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//               @Override
//               public void onClick(DialogInterface dialog, int which) {
//                   return;
//               }
//           });
//           builder.show();
//        });
//    }
}