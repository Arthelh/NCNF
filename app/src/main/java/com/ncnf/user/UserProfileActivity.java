package com.ncnf.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.ncnf.R;
import com.ncnf.authentication.AuthenticationService;
import com.ncnf.bookmark.BookMarkActivity;
import com.ncnf.friends.ui.FriendsActivity;
import com.ncnf.main.MainActivity;
import com.ncnf.notification.Registration;
import com.ncnf.storage.CacheFileStore;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.graphics.BitmapFactory.decodeResource;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static com.ncnf.utilities.StringCodes.USER_IMAGE_PATH;

@AndroidEntryPoint
public class UserProfileActivity extends AppCompatActivity {

    @Inject
    public User user;

    @Inject
    public Registration registration;

    @Inject
    public CacheFileStore fileStore;

    @Inject
    public AuthenticationService auth;

    private LocalDate birthDay;

    private EditText email;
    private EditText fullName;
    private EditText username;
    private TextView birthDate;
    private Switch notification_switch;

    private ImageView profilePicture;
    private MaterialButton editProfileButton;

    private boolean hasNotifications = false;
    private boolean isEditing = false;

    private static final int IMG_PICK_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        email = findViewById(R.id.userProfileEmail);
        fullName = findViewById(R.id.userProfileFullName);
        username = findViewById(R.id.userProfileUsername);
        birthDate = findViewById(R.id.userProfileBirthDay);

        email.setEnabled(false);
        fullName.setEnabled(false);
        username.setEnabled(false);
        birthDate.setEnabled(false);
        setUpDate();

        notification_switch = findViewById(R.id.profile_notification_switch);

        profilePicture = findViewById(R.id.personal_profile_picture);
        editProfileButton = findViewById(R.id.editProfileButton);
    }

    private void setUpDate() {
        birthDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int selYear = calendar.get(Calendar.YEAR);
            int selMonth = calendar.get(Calendar.MONTH);
            int selDay = calendar.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view1, year, month, day) -> {
                birthDay = LocalDate.of(year, month + 1, day);
                birthDate.setText(birthDay.toString());
            }, selYear, selMonth, selDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        fileStore.setContext(this);

        this.user.loadUserFromDB().thenAccept(u -> {
            if (user != null) {

                fileStore.setPath(USER_IMAGE_PATH, user.getUuid() + ".jpg");
                fileStore.downloadImage(profilePicture, decodeResource(this.getResources(), R.drawable.default_profile_picture));

                setupNotificationSwitch();
                
                email.setText(user.getEmail());

                String firstNameStr = user.getFullName();
                if(!firstNameStr.isEmpty()){
                    fullName.setText(firstNameStr);
                }

                String usernameStr = user.getUsername();
                if(!usernameStr.isEmpty()){
                    username.setText(usernameStr);
                }

                LocalDate birthDateObj = user.getBirthDate();
                if(!(birthDateObj == null)){
                    birthDay = birthDateObj;
                    birthDate.setText(birthDay.toString());
                }
                email.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_button){
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        Intent intent = new Intent(this, MainActivity.class);
        this.user.signOut();
        startActivity(intent);
    }

    public void openFriendsTab(View view){
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    public void openBookmark(View view){
        Intent intent = new Intent(this, BookMarkActivity.class);
        startActivity(intent);
    }


    public void changeProfilePicture(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMG_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IMG_PICK_CODE){
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                fileStore.uploadImage(bitmap).thenAccept(bool -> {
                    if(bool){
                        profilePicture.setImageURI(imageUri);
                        Snackbar.make(findViewById(R.id.userProfileRoot), "Profile picture successfully updated", LENGTH_SHORT).show();
                    } else {
                        imageFailure();
                    }
                });
            } catch (IOException e) {
                imageFailure();
            }
        }
    }

    private void imageFailure(){
        failurePopUp("Error", "Failure when importing the profile picture. Please retry");
    }

    private void failurePopUp(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Retry", ((dialog, which) -> dialog.cancel()))
                .show();
    }

    public void changeProfileState(View view){
        if(isEditing){
            saveChanges();
        } else {
            startToEdit();
        }
    }

    private void saveChanges(){
        user.setBirthDate(birthDay);
        user.setFullName(fullName.getText().toString());
        user.setUsername(username.getText().toString());

        user.changeEmail(auth, email.getText().toString()).thenCompose(emailChanged -> {
            user.setEmail(email.getText().toString());
            return user.saveUserToDB().thenAccept(userFieldsChanged -> {
                if (userFieldsChanged) {
                    Snackbar.make(findViewById(R.id.userProfileRoot), "Changes successfully saved", LENGTH_SHORT).show();
                    resetFields();
                }
            }).exceptionally(exception -> {
                failurePopUp("Error", "Fail to save your profile changes. Please retry");
                return null;
            });
        }).exceptionally(exception -> {
            failurePopUp("Email Error", "We couldn't modify your email : it must have a wrong format or it is already used. Please retry");
            return null;
        });
    }

    private void resetFields(){
        isEditing = false;
        editProfileButton.setIcon(getDrawable(R.drawable.ic_outline_edit_24));

        fullName.setEnabled(false);
        fullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        username.setEnabled(false);
        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        birthDate.setEnabled(false);
        birthDate.setClickable(false);
        ((ImageView)findViewById(R.id.birthday_icon_user_profile)).setImageResource(R.drawable.ic_baseline_cake_24);

        email.setEnabled(false);
        ((ImageView)findViewById(R.id.email_icon_user_profile)).setImageResource(R.drawable.ic_email_black);

    }

    private void startToEdit(){
        isEditing = true;
        editProfileButton.setIcon(getDrawable(R.drawable.ic_outline_check_24));
        fullName.setEnabled(true);
        fullName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

        username.setEnabled(true);
        username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

        birthDate.setEnabled(true);
        birthDate.setClickable(true);
        ((ImageView)findViewById(R.id.birthday_icon_user_profile)).setImageResource(R.drawable.ic_outline_edit_24);

        email.setEnabled(true);
        ((ImageView)findViewById(R.id.email_icon_user_profile)).setImageResource(R.drawable.ic_outline_edit_24);

    }

    private void setupNotificationSwitch() {
        Snackbar errorMsg = Snackbar.make(findViewById(R.id.userProfileRoot), "An error happened! Try again later", LENGTH_SHORT);
        Snackbar successMsg = Snackbar.make(findViewById(R.id.userProfileRoot), "Notifications successfully changed", LENGTH_SHORT);

        notification_switch.setChecked(hasNotifications);
        notification_switch.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                registration.register().thenAccept(v->successMsg.show()).exceptionally(exception -> {
                    errorMsg.show();
                    return null;
                });
            } else {
                registration.unregister().thenAccept(v->successMsg.show()).exceptionally(exception -> {
                    errorMsg.show();
                    return null;
                });
            }
        });
    }

}