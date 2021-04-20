package com.ncnf.user.Profile;


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
