package com.rafaelperez.singlesignon1.viewmodel;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthenticatorViewModel extends ViewModel {

    private MutableLiveData<Boolean> _signingIn = new MutableLiveData<>();
    private MutableLiveData<String> _testToken = new MutableLiveData<>();

    public AuthenticatorViewModel() {
        super();
        _signingIn.setValue(false);
    }

    public void signIn() {
        _signingIn.setValue(true);
    }

    public void authenticate(String email, String password) {
        //todo: get token from sso-server
        _testToken.setValue("test_token");

        /*new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                String authtoken = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);
                final Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);
                res.putExtra(PARAM_USER_PASS, userPass);
                return res;
            }
            @Override
            protected void onPostExecute(Intent intent) {
                finishLogin(intent);
            }
        }.execute();*/
    }

    public MutableLiveData<Boolean> get_signingIn() {
        return _signingIn;
    }

    public MutableLiveData<String> get_testToken() {
        return _testToken;
    }
}
