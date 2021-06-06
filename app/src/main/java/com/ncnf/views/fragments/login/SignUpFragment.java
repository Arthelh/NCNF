package com.ncnf.views.fragments.login;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.ncnf.R;
import com.ncnf.authentication.firebase.FirebaseAuthentication;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.models.User;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.InputValidator.isValidPassword;
import static com.ncnf.utilities.InputValidator.verifyEmailInput;
import static com.ncnf.utilities.StringCodes.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static com.ncnf.utilities.StringCodes.EMPTY_FIELD_STRING;
import static com.ncnf.utilities.StringCodes.EMPTY_STRING;
import static com.ncnf.utilities.StringCodes.INVALID_PASSWORD_STRING;
import static com.ncnf.utilities.StringCodes.PASSWORDS_DO_NOT_MATCH_STRING;
import static com.ncnf.utilities.StringCodes.POPUP_POSITIVE_BUTTON;
import static com.ncnf.utilities.StringCodes.POPUP_TITLE;

@AndroidEntryPoint
public class SignUpFragment extends Fragment {

    @Inject
    FirebaseAuthentication auth;

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button registerButton;

    private final Class<?> activity;

    public SignUpFragment(Class<?> activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get all items from view
        getItemsFromView();

        showProgressBar(false);

        registerButton.setOnClickListener(v -> signUp());
    }

    public void signUp() {
        String emailString = this.email.getText().toString();
        String passwordString = this.password.getText().toString();

        if(checkInputs()){
            register(emailString, passwordString);
        }
    }

    public void register(String email, String password){
        showProgressBar(true);

        CompletableFuture<Boolean> futureResponse= auth.register(email, password);

        futureResponse.thenCompose(res -> {
            Log.d(DEBUG_TAG, "Successful register for " + email);
            User user = CurrentUserModule.getCurrentUser();
            Log.d(DEBUG_TAG, "UUID : " + user.getUuid());
            return user.saveUserToDB();
        }).thenRun(() -> {
            Intent intent = new Intent(getActivity(), activity);
            startActivity(intent);
            requireActivity().finish();
        }).exceptionally(exception -> {
            Log.d(DEBUG_TAG,"Unsuccessful register for " + email + " : " + exception.getCause().getMessage());
            setException(exception.getCause().getMessage());
            Log.d(DEBUG_TAG, "Deleting user.");
            this.auth.delete();
            return null;
        }).thenRun(() -> showProgressBar(false));
    }

    private void getItemsFromView(){
        this.email = getView().findViewById(R.id.signUpEmail);
        this.password = getView().findViewById(R.id.signUpPassword);
        this.confirmPassword = getView().findViewById(R.id.signUpConfirmPassword);
        this.registerButton = getView().findViewById(R.id.signUpRegisterButton);
    }

    private boolean checkInputs(){
        boolean passed = true;

        String emailString = this.email.getText().toString();
        String passwordString = this.password.getText().toString();
        String confirmPasswordString = this.confirmPassword.getText().toString();

        if(emailString.isEmpty()){
            email.setError(EMPTY_FIELD_STRING);
            passed = false;
        } else if(passwordString.isEmpty()){
            password.setError(EMPTY_FIELD_STRING);
            passed = false;
        } else if(confirmPasswordString.isEmpty()){
            confirmPassword.setError(EMPTY_FIELD_STRING);
            passed = false;
        } else if(!passwordString.equals(confirmPasswordString)){
            confirmPassword.setError(PASSWORDS_DO_NOT_MATCH_STRING);
            password.setText(EMPTY_STRING);
            confirmPassword.setText(EMPTY_STRING);
            passed = false;
        } else if (!verifyEmailInput(emailString)){
            email.setError(BADLY_FORMATTED_EMAIL_STRING);
            passed = false;
        } else if (!isValidPassword(passwordString)){
            password.setError(INVALID_PASSWORD_STRING);
            password.setText(EMPTY_STRING);
            confirmPassword.setText(EMPTY_STRING);
            passed = false;
        }

        return passed;
    }

    private void showProgressBar(Boolean show){
        ProgressBar bar = getView().findViewById(R.id.sign_up_spinner);
        if(show){
            bar.setVisibility(View.VISIBLE);
        } else {
            bar.setVisibility(View.INVISIBLE);
        }
    }

    private void setException(String s){
        AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
        popup.setCancelable(true);
        popup.setTitle(POPUP_TITLE);
        popup.setMessage(s);
        popup.setPositiveButton(POPUP_POSITIVE_BUTTON, (dialog, which) -> {
            dialog.cancel();
        });
        popup.show();
    }
}