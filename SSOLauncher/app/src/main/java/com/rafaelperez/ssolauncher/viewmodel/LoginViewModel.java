package com.rafaelperez.ssolauncher.viewmodel;

import android.content.pm.PackageManager;

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

    //private int readAccountsPermission;
    private MutableLiveData<Integer> readAccountsPermission = new MutableLiveData<>();
    public LiveData<Integer> getReadAccountsPermission() {
        return readAccountsPermission;
    }

    public LoginViewModel() {
        super();
        init();
    }

    public void init() {
        signingIn.setValue(false);
        accessDenied.setValue(false);
        readAccountsPermission.setValue(PackageManager.PERMISSION_DENIED);
    }

    public void signIn() {
        signingIn.setValue(true);
    }

    public void grantReadAccountsPermission() {
        readAccountsPermission.setValue(PackageManager.PERMISSION_GRANTED);
    }

    public void authenticate(String token) {

    }
}
