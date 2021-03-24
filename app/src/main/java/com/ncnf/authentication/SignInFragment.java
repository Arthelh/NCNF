package com.ncnf.authentication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ncnf.R;
import com.ncnf.user.UserProfileActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EMPTY_FIELD_STRING;
import static com.ncnf.Utils.isValidEmail;

@AndroidEntryPoint
public class SignInFragment extends Fragment {
    @Inject
    AuthenticationService auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setProgressBar(View.INVISIBLE);
        ((EditText)getView().findViewById(R.id.signInEmail)).getText().clear();
        ((EditText)getView().findViewById(R.id.signInPassword)).getText().clear();

        getView().findViewById(R.id.signInButton).setOnClickListener(v -> signIn());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void signIn() {
        EditText emailField = getView().findViewById(R.id.signInEmail);
        String email = emailField.getText().toString();

        EditText passwordField = getView().findViewById(R.id.signInPassword);
        String password = passwordField.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            setException(EMPTY_FIELD_STRING);
            return;
        } else if (!isValidEmail(email)){
            setException(BADLY_FORMATTED_EMAIL_STRING);
            return;
        }

        logIn(email, password);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void logIn(String email, String password){

        setProgressBar(View.VISIBLE);

        CompletableFuture<AuthenticationResponse> futureResponse = auth.logIn(email, password);

        futureResponse.thenAccept(response -> {
            if(response.isSuccessful()){
                Log.d(DEBUG_TAG,"Successful login for " + email);
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            } else {
                Log.d(DEBUG_TAG,"Unsuccessful login for " + email + " : " + response.getException().getMessage());
                setException(response.getException().getMessage());
            }
            setProgressBar(View.INVISIBLE);
        });
    }

    private void setProgressBar(int visibility){
        ProgressBar bar = getView().findViewById(R.id.progressBar2);
        bar.setVisibility(visibility);
    }

    private void setException(String s){
        TextView exception = getView().findViewById(R.id.exceptionSignIn);
        exception.setText(s);
    }
}