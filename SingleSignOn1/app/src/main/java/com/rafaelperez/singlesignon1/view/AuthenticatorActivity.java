package com.rafaelperez.singlesignon1.view;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rafaelperez.singlesignon1.R;
import com.rafaelperez.singlesignon1.authentication.AccountGeneral;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements View.OnClickListener {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    private final int REQ_SIGNUP = 1;
    private final String TAG = this.getClass().getSimpleName();

    private AccountManager accountManager;
    private String authTokenType;

    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        accountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        authTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (authTokenType == null) {
            authTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
        }
        if (accountName != null) {
            etEmail.setText(accountName);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                signIn();
                break;
            case R.id.btnSignUp:
                //todo: add signup functionality
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void signIn() {
        final String userName = etEmail.getText().toString();
        final String userPass = etPassword.getText().toString();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        MyAsynTask asyncTask = new MyAsynTask(this, userName, accountType, userPass);
        asyncTask.execute();
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authTokenType = this.authTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, accountPassword, null);
            accountManager.setAuthToken(account, authTokenType, authToken);
        } else {
            accountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    private class MyAsynTask extends AsyncTask<String, Void, Intent> {
        private Context context;
        private String userName;
        private String accountType;
        private String userPass;

        MyAsynTask(Context context, String userName, String accountType, String userPass) {
            this.context = context;
            this.userName = userName;
            this.accountType = accountType;
            this.userPass = userPass;
        }

        @Override
        protected Intent doInBackground(String... params) {
            String authToken = null;
            Bundle data = new Bundle();
            try {
                //todo: sign in with retrofit
                //authToken = AccountGeneral.sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);
                data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                data.putString(PARAM_USER_PASS, userPass);
            } catch (Exception e) {
                data.putString(KEY_ERROR_MESSAGE, e.getMessage());
            }

            final Intent intent = new Intent();
            intent.putExtras(data);
            return intent;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                Toast.makeText(context, intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
            } else {
                finishLogin(intent);
            }
        }
    }
}
