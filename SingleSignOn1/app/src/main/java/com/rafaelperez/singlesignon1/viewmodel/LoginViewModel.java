package com.rafaelperez.singlesignon1.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<Boolean> signingIn = new MutableLiveData<>();
    public LiveData<Boolean> getSigningIn() {
        return signingIn;
    }

    private MutableLiveData<Boolean> accessDenied = new MutableLiveData<>();
    public LiveData<Boolean> getAccessDenied() {
        return accessDenied;
    }

    public LoginViewModel() {
        super();
        init();
    }

    public void init() {
        signingIn.setValue(false);
        accessDenied.setValue(false);
    }

    public void signIn() {
        signingIn.setValue(true);
    }

    public void authenticate(String token) {

    }
}
