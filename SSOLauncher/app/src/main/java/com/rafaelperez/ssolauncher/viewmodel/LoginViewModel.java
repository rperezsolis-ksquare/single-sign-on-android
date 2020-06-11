package com.rafaelperez.ssolauncher.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<Boolean> signingIn = new MutableLiveData<>();
    public LiveData<Boolean> getSigningIn() {
        return signingIn;
    }

    public void signIn() {
        signingIn.setValue(true);
    }
}
