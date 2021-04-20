package com.ncnf.authentication.ui;

import android.app.AlertDialog;
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

import com.ncnf.R;
import com.ncnf.authentication.AuthenticationResponse;
import com.ncnf.authentication.AuthenticationService;
import com.ncnf.user.UserProfileActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EMPTY_FIELD_STRING;
import static com.ncnf.Utils.NEXT_ACTIVITY_EXTRA_KEY;
import static com.ncnf.Utils.POPUP_POSITIVE_BUTTON;
import static com.ncnf.Utils.POPUP_TITLE;
import static com.ncnf.utilities.InputValidator.verifyEmailInput;

@AndroidEntryPoint
public class SignInFragment extends Fragment {
    @Inject
    AuthenticationService auth;

    private EditText email;
    private EditText password;
    private TextView exceptionText;
    private Button loginButton;
    private final Class<?> activity;

    public SignInFragment(Class<?> activity){
        this.activity = activity;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getItemsFromView();

        showProgressBar(false);

        loginButton.setOnClickListener(v -> signIn());
    }

    
    public void signIn() {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        if(checkInputs()){
            logIn(emailString, passwordString);
        }
    }

    
    private void logIn(String email, String password){

        showProgressBar(true);

        CompletableFuture<AuthenticationResponse> futureResponse = auth.logIn(email, password);

        futureResponse.thenAccept(response -> {
            if(response.isSuccessful()){
                Log.d(DEBUG_TAG,"Successful login for " + email);
                Intent intent = new Intent(getActivity(), this.activity);
                startActivity(intent);
            } else {
                Log.d(DEBUG_TAG,"Unsuccessful login for " + email + " : " + response.getException().getMessage());

                setException(response.getException().getMessage());
            }
            showProgressBar(false);
        });
    }

    private boolean checkInputs(){
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        if(emailString.isEmpty()){
            email.setError(EMPTY_FIELD_STRING);
            return false;
        } else if(passwordString.isEmpty()) {
            password.setError(EMPTY_FIELD_STRING);
            return false;
        } else if (!verifyEmailInput(emailString)){
            email.setError(BADLY_FORMATTED_EMAIL_STRING);
            return false;
        }

        return true;
    }

    private void getItemsFromView(){
        this.email = getView().findViewById(R.id.signInEmail);
        this.password = getView().findViewById(R.id.signInPassword);
        this.exceptionText = getView().findViewById(R.id.exceptionSignIn);
        this.loginButton = getView().findViewById(R.id.signInLoginButton);
    }

    private void showProgressBar(Boolean show){
        ProgressBar bar = getView().findViewById(R.id.progressBar2);
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
//        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}