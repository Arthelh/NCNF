package com.ncnf.user.user_profile;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ncnf.R;
import com.ncnf.authentication.AuthenticationService;
import com.ncnf.bookmark.BookMarkActivity;
import com.ncnf.friends.ui.FriendsActivity;
import com.ncnf.main.MainActivity;
import com.ncnf.notification.Registration;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.user.User;
import com.ncnf.user.UserTabActivity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.graphics.BitmapFactory.decodeResource;
import static android.icu.lang.UProperty.INT_START;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

import static com.ncnf.utilities.InputValidator.verifyEmailInput;
import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static com.ncnf.utilities.StringCodes.ORGANIZATION_NAME;
import static com.ncnf.utilities.StringCodes.USER_IMAGE_PATH;

@AndroidEntryPoint
public class UserProfileTabFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public Registration registration;

    @Inject
    public CacheFileStore fileStore;

    @Inject
    public AuthenticationService auth;

    private LocalDate birthDay;

    private TextView email;
    private EditText fullName;
    private EditText username;
    private TextView birthDate;
    private Switch notification_switch;

    private AlertDialog emailDialog;

    private ImageView profilePicture;
    private MaterialButton editProfileButton;

    private boolean isEditing = false;

    private static final int IMG_PICK_CODE = 1000;
    private final char usernamePrefix = '@';
    TextView emailMessage;

    public UserProfileTabFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailMessage = new TextView(getActivity());

        email = requireView().findViewById(R.id.userProfileEmail);
        fullName = requireView().findViewById(R.id.userProfileFullName);
        username = requireView().findViewById(R.id.userProfileUsername);
        birthDate = requireView().findViewById(R.id.userProfileBirthDay);

        email.setEnabled(false);
        email.setOnClickListener(this::changeEmail);
        fullName.setEnabled(false);
        username.setEnabled(false);
        birthDate.setEnabled(false);
        setUpDate();
        setUpDialog("Please enter your new email");


        notification_switch = requireView().findViewById(R.id.profile_notification_switch);

        profilePicture = requireView().findViewById(R.id.personal_profile_picture);
        editProfileButton = requireView().findViewById(R.id.editProfileButton);

        initUser();

        requireView().findViewById(R.id.friends_profile_button).setOnClickListener(this::openFriendsTab);
        requireView().findViewById(R.id.bookmark_profile_button).setOnClickListener(this::openBookmark);
        requireView().findViewById(R.id.editProfileButton).setOnClickListener(this::changeProfileState);
        requireView().findViewById(R.id.editProfilePictureButton).setOnClickListener(this::changeProfilePicture);
    }

    private void setUpDate() {
        birthDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int selYear = calendar.get(Calendar.YEAR);
            int selMonth = calendar.get(Calendar.MONTH);
            int selDay = calendar.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view1, year, month, day) -> {
                birthDay = LocalDate.of(year, month + 1, day);
                birthDate.setText(birthDay.toString());
            }, selYear, selMonth, selDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
    }


    private void initUser() {
        fileStore.setContext(requireActivity());

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
                    setUsernameView(usernameStr);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

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
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("disconnected", true);
        this.user.signOut();
        startActivity(intent);
    }

    public void openFriendsTab(View view){
        Intent intent = new Intent(requireActivity(), FriendsActivity.class);
        startActivity(intent);
    }

    public void openBookmark(View view){
        Intent intent = new Intent(requireActivity(), BookMarkActivity.class);
        startActivity(intent);
    }

    public void changeProfilePicture(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMG_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == AppCompatActivity.RESULT_OK && requestCode == IMG_PICK_CODE){
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                fileStore.uploadImage(bitmap).thenAccept(bool -> {
                    if(bool){
                        profilePicture.setImageURI(imageUri);
                        Snackbar.make(requireActivity().findViewById(android.R.id.content), "Profile picture successfully updated", LENGTH_SHORT).show();
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
        new AlertDialog.Builder(requireActivity())
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
        if(username.getText().toString().charAt(0) == usernamePrefix){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Username can't start with @ symbol", LENGTH_SHORT).show();
            return;
        }

        user.setBirthDate(birthDay);
        user.setFullName(fullName.getText().toString());
        user.setUsername(username.getText().toString());

        user.saveUserToDB().thenAccept(userFieldsChanged -> {
            if (userFieldsChanged) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Changes successfully saved", LENGTH_SHORT).show();
                resetFields();
            }
        }).exceptionally(exception -> {
            failurePopUp("Error", "Fail to save your profile changes.\n Please disconnect, reconnect and try again" + exception.getMessage());
            return null;
        });
    }

    private void resetFields() {
        isEditing = false;
        editProfileButton.setIcon(requireActivity().getDrawable(R.drawable.ic_outline_edit_24));

        fullName.setEnabled(false);
        fullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        username.setEnabled(false);
        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        setUsernameView(username.getText().toString());

        birthDate.setEnabled(false);
        birthDate.setClickable(false);
        ((ImageView) requireActivity().findViewById(R.id.birthday_icon_user_profile)).setImageResource(R.drawable.ic_baseline_cake_24);

        email.setEnabled(false);
        email.setClickable(false);
        ((ImageView) requireActivity().findViewById(R.id.email_icon_user_profile)).setImageResource(R.drawable.ic_email_black);
    }

    public void changeEmail(View view){
        emailDialog.show();
    }

    private void setUpDialog(String message){
        final EditText input = new EditText(this.getContext());
        input.setHint("type your email here");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);


    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getActivity());
        alertDialog.setTitle("Change Email");
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_email_black);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Next", (dialog1, which1) -> {
            dialog1.cancel();

            String newEmail = input.getText().toString();
            if(checkEmail(emailDialog, newEmail)){
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this.getActivity());
                confirmDialog.setTitle("Confirm email");
                confirmDialog.setView(mkEmailString(user.getEmail(), newEmail));
                confirmDialog.setPositiveButton("Confirm", (dialog, which) -> {
                    Log.d(DEBUG_TAG, "going here");
                    user.changeEmail(auth, newEmail).thenAccept(bool ->{
                        if(bool){
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Email successfully changed", LENGTH_LONG).show();
                            email.setText(newEmail);
                            user.setEmail(newEmail);
                        }
                    }).exceptionally(e -> {
                        Log.d(DEBUG_TAG, "going bad " + e.getMessage());

                        failurePopUp("Error", "Failed to change email : please retry\n" + e.getMessage());
                        return null;
                    });
                });
                confirmDialog.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    setUpDialog("Please enter your new email");
                });
                confirmDialog.show();
            } else {
                emailDialog.show();
            }
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            setUpDialog("Please enter your new email");
        });
        emailDialog = alertDialog.create();
    }

    private boolean checkEmail(AlertDialog alertDialog, String newEmail){
        if(alertDialog != null || alertDialog.isShowing()){
            if(!verifyEmailInput(newEmail)){
                setUpDialog("The email you entered is incorrect : please enter a correct email address");
                return false;
            } else if(newEmail.equals(user.getEmail())){
                setUpDialog("Please enter an email address different than your current email address");
                return false;
            }
            return true;
        }
        return false;
    }


    private void startToEdit(){
        isEditing = true;
        editProfileButton.setIcon(requireActivity().getDrawable(R.drawable.ic_outline_check_24));
        fullName.setEnabled(true);
        fullName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

        username.setText(user.getUsername());
        username.setEnabled(true);
        username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

        birthDate.setEnabled(true);
        birthDate.setClickable(true);
        ((ImageView)requireActivity().findViewById(R.id.birthday_icon_user_profile)).setImageResource(R.drawable.ic_outline_edit_24);

        email.setEnabled(true);
        email.setClickable(true);
        ((ImageView)requireActivity().findViewById(R.id.email_icon_user_profile)).setImageResource(R.drawable.ic_outline_edit_24);
    }

    private void setupNotificationSwitch() {
        Snackbar successMsg = Snackbar.make(getActivity().findViewById(android.R.id.content), "Notifications successfully changed", LENGTH_SHORT);
        Snackbar errorMsg = Snackbar.make(getActivity().findViewById(android.R.id.content), "An error happened! Try again later", LENGTH_SHORT);

        notification_switch.setChecked(user.getNotifications());
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

    private void setUsernameView(String newUsername){
        SpannableString ss = new SpannableString(usernamePrefix + newUsername);
        ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        username.setText(ss);
    }

    private TextView mkEmailString(String email1, String email2){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,20,10,10);

        emailMessage.setLayoutParams(params);
        emailMessage.setGravity(Gravity.CENTER);

        String start = "Do you want to switch from\n";
        String middle = " to ";
        String end = " ?";
        String finalString = start + email1 + middle + email2 + end;
        Spannable sb = new SpannableString( finalString );

        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), finalString.indexOf(email1), finalString.indexOf(email1) + email1.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(50), finalString.indexOf(email1), finalString.indexOf(email1) + email1.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), finalString.indexOf(email2), finalString.indexOf(email2) + email2.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(50), finalString.indexOf(email2), finalString.indexOf(email2) + email2.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        emailMessage.setText(sb, TextView.BufferType.SPANNABLE);
        return emailMessage;
    }
}