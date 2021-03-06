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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ncnf.R;
import com.ncnf.authentication.firebase.FirebaseAuthentication;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.InputValidator.verifyEmailInput;
import static com.ncnf.utilities.StringCodes.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static com.ncnf.utilities.StringCodes.EMPTY_FIELD_STRING;
import static com.ncnf.utilities.StringCodes.POPUP_POSITIVE_BUTTON;
import static com.ncnf.utilities.StringCodes.POPUP_TITLE;

@AndroidEntryPoint
public class SignInFragment extends Fragment {
    @Inject
    FirebaseAuthentication auth;

    private EditText email;
    private EditText password;
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

        CompletableFuture<Boolean> futureResponse = auth.logIn(email, password);

        futureResponse.thenAccept(result -> {
            Log.d(DEBUG_TAG,"Successful login for " + email);
            Intent intent = new Intent(getActivity(), activity);
            startActivity(intent);
            requireActivity().finish();
        }).exceptionally(exception -> {
            Log.d(DEBUG_TAG,"Unsuccessful login for " + email + " : " + exception.getCause().getMessage());

            setException(exception.getCause().getMessage());
            showProgressBar(false);
            return null;
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
        this.loginButton = getView().findViewById(R.id.signInLoginButton);
    }

    private void showProgressBar(Boolean show){
        ProgressBar bar = getView().findViewById(R.id.sign_in_spinner);
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