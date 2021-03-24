package com.ncnf.authentication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.user.PrivateUser;
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
import static com.ncnf.Utils.USERs_COLLECTION_KEY;
import static com.ncnf.Utils.isValidEmail;
import static com.ncnf.Utils.isValidPassword;

@AndroidEntryPoint
public class SignUpFragment extends Fragment {

    @Inject
    AuthenticationService auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setProgressBar(View.INVISIBLE);
        setFieldsEmpty();

        getView().findViewById(R.id.signUpButton).setOnClickListener(v -> signUp());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void signUp() {
        String email = getFieldText(R.id.signUpEmail);
        String password = getFieldText(R.id.signUpPassword);
        String confirmed = getFieldText(R.id.signUpConfirmPassword);

        setFieldsEmpty();

        if(email.isEmpty() || password.isEmpty() || confirmed.isEmpty()){
            setException(EMPTY_FIELD_STRING);
            return;
        } else if(!password.equals(confirmed)){
            setException(PASSWORDS_DO_NOT_MATCH_STRING);
            return;
        } else if (!isValidEmail(email)){
            setException(BADLY_FORMATTED_EMAIL_STRING);
            return;
        } else if (!isValidPassword(password)){
            setException(INVALID_PASSWORD_STRING);
            return;
        }

        register(email, password);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void register(String email, String password){
        setProgressBar(View.VISIBLE);

        CompletableFuture<AuthenticationResponse> futureResponse= auth.register(email, password);

        futureResponse.thenAccept(response -> {
            if(response.isSuccessful()){
                Log.d(DEBUG_TAG, "Successful register for " + email);
                FirebaseUser fb = response.getResult().getUser();
                PrivateUser user = new PrivateUser(fb, USERs_COLLECTION_KEY + fb.getUid());
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
            setProgressBar(View.INVISIBLE);
        });
    }

    private void setFieldsEmpty(){
        ((EditText)getView().findViewById(R.id.signUpEmail)).setText(EMPTY_STRING);
        ((EditText)getView().findViewById(R.id.signUpPassword)).setText(EMPTY_STRING);
        ((EditText)getView().findViewById(R.id.signUpConfirmPassword)).setText(EMPTY_STRING);
    }

    private String getFieldText(int id){
        return ((EditText)getView().findViewById(id)).getText().toString();
    }

    private void setProgressBar(int visibility){
        ProgressBar bar = getView().findViewById(R.id.progressBar);
        bar.setVisibility(visibility);
    }

    private void setException(String s){
        TextView exception = getView().findViewById(R.id.exceptionSignUp);
        exception.setText(s);
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }


}