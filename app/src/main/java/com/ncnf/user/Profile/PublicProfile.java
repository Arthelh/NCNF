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

    public PublicProfile(PrivateUser user) throws ExecutionException, InterruptedException {
        this.email = user.getEmail();
        this.username = (String) user.getField(NAME_KEY).get().getResult();
        }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
