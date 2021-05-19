package com.ncnf.views.activities.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.views.fragments.login.SignInFragment;
import com.ncnf.views.fragments.login.SignUpFragment;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.NEXT_ACTIVITY_EXTRA_KEY;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private Class<?> activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            this.activity = (Class<?>)extras.get(NEXT_ACTIVITY_EXTRA_KEY);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, new SignInFragment(activity))
                    .commit();
        }

        findViewById(R.id.loginButton).setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, new SignInFragment(activity))
                .commit());

        findViewById(R.id.registerButton).setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, new SignUpFragment(activity))
                .commit());
    }

}