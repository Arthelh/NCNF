package com.ncnf.authentication.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.authentication.AuthenticationResponse;
import com.ncnf.authentication.AuthenticationService;
import com.ncnf.user.User;
import com.ncnf.user.UserProfileActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EMPTY_FIELD_STRING;
import static com.ncnf.Utils.EMPTY_STRING;
import static com.ncnf.Utils.INVALID_PASSWORD_STRING;
import static com.ncnf.Utils.PASSWORDS_DO_NOT_MATCH_STRING;
import static com.ncnf.utilities.InputValidator.isValidPassword;
import static com.ncnf.utilities.InputValidator.verifyEmailInput;

@AndroidEntryPoint
public class SignUpFragment extends Fragment {

    @Inject
    AuthenticationService auth;

    private Boolean isOrganizer = false;

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private TextView exceptionText;
    private Button organizerButton;
    private Button registerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get all items from view
        getItemsFromView();

        showProgressBar(false);

        registerButton.setOnClickListener(v -> signUp());
        organizerButton.setOnClickListener(v -> {
            if(isOrganizer){
                setPrivateUserView();
            } else {
                setOrganizerView();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void signUp() {
        String emailString = this.email.getText().toString();
        String passwordString = this.password.getText().toString();

        if(checkInputs()){
            register(emailString, passwordString);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void register(String email, String password){
        showProgressBar(true);

        CompletableFuture<AuthenticationResponse> futureResponse= auth.register(email, password);

        futureResponse.thenAccept(response -> {
            if(response.isSuccessful()){
                Log.d(DEBUG_TAG, "Successful register for " + email);
                FirebaseUser fb = response.getResult().getUser();
                User user = new User(fb.getUid(), fb.getEmail());
                user.saveUserToDB().thenAccept(dbResponse -> {
                    if (dbResponse.isSuccessful()) {
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d(DEBUG_TAG, "Deleting user.");
                        fb.delete();
                        setException("Couldn't create a new user : please try again");
                    }
                });
            } else {
                Log.d(DEBUG_TAG,"Unsuccessful register for " + email + " : " + response.getException().getMessage());
                setException(response.getException().getMessage());
            }
            showProgressBar(false);
        });
    }

    private void getItemsFromView(){
        this.email = getView().findViewById(R.id.signUpEmail);
        this.password = getView().findViewById(R.id.signUpPassword);
        this.confirmPassword = getView().findViewById(R.id.signUpConfirmPassword);
        this.exceptionText = getView().findViewById(R.id.exceptionSignUp);
        this.organizerButton = getView().findViewById(R.id.organizerButton);
        this.registerButton = getView().findViewById(R.id.signUpRegisterButton);
    }

    private boolean checkInputs(){
        boolean passed = true;

        String emailString = this.email.getText().toString();
        String passwordString = this.password.getText().toString();
        String confirmPasswordString = this.confirmPassword.getText().toString();

        if(emailString.isEmpty() || passwordString.isEmpty() || confirmPasswordString.isEmpty()){
            setException(EMPTY_FIELD_STRING);
            passed = false;
        } else if(!passwordString.equals(confirmPasswordString)){
            setException(PASSWORDS_DO_NOT_MATCH_STRING);
            confirmPassword.setError(PASSWORDS_DO_NOT_MATCH_STRING);
            password.setText(EMPTY_STRING);
            confirmPassword.setText(EMPTY_STRING);
            passed = false;
        } else if (!verifyEmailInput(emailString)){
            setException(BADLY_FORMATTED_EMAIL_STRING);
            email.setError(BADLY_FORMATTED_EMAIL_STRING);
            passed = false;
        } else if (!isValidPassword(passwordString)){
            setException(INVALID_PASSWORD_STRING);
            password.setError(INVALID_PASSWORD_STRING);
            password.setText(EMPTY_STRING);
            confirmPassword.setText(EMPTY_STRING);
            passed = false;
        }

        return passed;
    }

    private void showProgressBar(Boolean show){
        ProgressBar bar = getView().findViewById(R.id.progressBar);
        if(show){
            bar.setVisibility(View.VISIBLE);
        } else {
            bar.setVisibility(View.INVISIBLE);
        }
    }

    private void setException(String s){
        exceptionText.setText(s);
//        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void setOrganizerView(){
        isOrganizer = true;
        setException("Organizer Registration");
        email.setHint("Business Email..");
        organizerButton.setText("I'm a regular user...");
    }

    private void setPrivateUserView(){
        isOrganizer = false;
        setException("Register");
        email.setHint("Email..");
        organizerButton.setText("I'm an organizer...");
    }

}