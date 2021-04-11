package com.ncnf.user.Profile;


import com.ncnf.user.PrivateUser;

import java.util.concurrent.ExecutionException;

import static com.ncnf.Utils.NAME_KEY;

//TODO : Placeholder for now
public class PublicProfile implements Profile{
    private final String username;
    private final String email;

    public PublicProfile(String username, String email){
        this.email = email;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
