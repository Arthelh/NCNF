package com.ncnf.authentication.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, new SignInFragment())
                    .commit();
        }

        findViewById(R.id.loginButton).setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, new SignInFragment())
                .commit());

        findViewById(R.id.registerButton).setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, new SignUpFragment())
                .commit());
    }

}