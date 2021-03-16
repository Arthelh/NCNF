package com.ncnf.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.R;
import com.ncnf.user.PrivateUser;
import com.ncnf.user.UserProfileActivity;

import static com.ncnf.Utils.*;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Intent intent;
    private PrivateUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.auth = FirebaseAuth.getInstance();
        this.intent = new Intent(this, UserProfileActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setProgressBar(View.INVISIBLE);
        setFieldsEmpty();
    }

    public void signUp(View view) {
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

        setProgressBar(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(DEBUG_TAG,"New user successfully authenticated");
                    user = PrivateUser.getInstance();
                    user.createDBUser(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            startActivity(intent);
                            finish();
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            auth.getCurrentUser().delete();
                            setException("Couldn't create a new user : please try again");
                        }
                    });

                } else {
                    Log.d(DEBUG_TAG,"Error authenticating new user " + task.getException().toString());
                    /*
                    TODO: Match exception to check if user exists or not
                     */
                }

            }
        });
        setProgressBar(View.INVISIBLE);
    }

    private void setFieldsEmpty(){
        ((EditText)findViewById(R.id.signUpEmail)).setText(EMPTY_STRING);
        ((EditText)findViewById(R.id.signUpPassword)).setText(EMPTY_STRING);
        ((EditText)findViewById(R.id.signUpConfirmPassword)).setText(EMPTY_STRING);
    }

    private String getFieldText(int id){
        return ((EditText)findViewById(id)).getText().toString();
    }

    private void setProgressBar(int visibility){
        ProgressBar bar = findViewById(R.id.progressBar);
        bar.setVisibility(visibility);
    }

    private void setException(String s){
        TextView exception = findViewById(R.id.exceptionSignUp);
        exception.setText(s);
    }
}