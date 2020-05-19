package com.rafaelperez.singlesignon1.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<Boolean> _signingIn = new MutableLiveData<>();

    public LoginViewModel() {
        super();
        _signingIn.setValue(false);
    }

    public void signIn() {
        _signingIn.setValue(true);
    }

    public void authenticate() {
        //todo: try to autenticate with the service provider

    }

    public MutableLiveData<Boolean> get_signingIn() {
        return _signingIn;
    }
}
