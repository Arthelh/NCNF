package com.ncnf.authentication;

import com.google.firebase.auth.FirebaseAuth;

public class AuthenticationSystem {

    private final FirebaseAuth auth;


    public AuthenticationSystem(){
        auth = FirebaseAuth.getInstance();
    }


}
