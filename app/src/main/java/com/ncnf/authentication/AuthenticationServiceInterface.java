package com.ncnf.authentication;

import androidx.lifecycle.MutableLiveData;

public interface AuthenticationServiceInterface {

    MutableLiveData<Exception> register(String email, String password);

    MutableLiveData<Exception> signIn(String email, String password);


}
