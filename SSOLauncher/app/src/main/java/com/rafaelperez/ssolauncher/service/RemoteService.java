package com.rafaelperez.ssolauncher.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.rafaelperez.ssolauncher.R;
import com.rafaelperez.ssolauncher.domain.AuthCodeResponse;
import com.rafaelperez.ssolauncher.domain.ValidateAuthCodeRequest;
import com.rafaelperez.ssolauncher.repository.AuthCodeRepository;
import com.rafaelperez.ssolauncher.view.LoginFragment;

import retrofit2.Call;
import retrofit2.Response;

public class RemoteService extends Service {
    private static final String KEY_MSG = "msg";
    private static final String KEY_AUTHCODE = "authCode";
    private static final String KEY_ID = "id";
    private static final int SUCCESS = 1;
    private static final int FAIL = 2;

    private Messenger messenger; //receives remote invocations
    private AuthCodeRepository repository;
    private AccountManager accountManager;
    private String accessToken;
    private String refreshToken;
    private String userName;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new AuthCodeRepository();
        accountManager = AccountManager.get(RemoteService.this);
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
        if (accounts.length==1) {
            accessToken = accountManager.getUserData(accounts[0], LoginFragment.KEY_ACCESS_TOKEN);
            refreshToken = accountManager.getUserData(accounts[0], LoginFragment.KEY_REFRESH_TOKEN);
            userName = accounts[0].name;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(this.messenger == null) {
            synchronized(RemoteService.class) {
                if(this.messenger == null) {
                    this.messenger = new Messenger(new IncomingHandler());
                }
            }
        }
        return this.messenger.getBinder();
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message receivedMessage) {
            System.out.println("*****************************************");
            System.out.println("Remote Service successfully invoked!!!!!!");
            System.out.println("*****************************************");

            int what = receivedMessage.what;
            Messenger replyTo = receivedMessage.replyTo;
            if (what == 1) {
                Bundle bundle = (Bundle) receivedMessage.obj;
                String msg = (String) bundle.getString(KEY_MSG);
                String authCode = bundle.getString(KEY_AUTHCODE);
                Long id = bundle.getLong(KEY_ID);
                Toast.makeText(RemoteService.this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                ValidateAuthCodeRequest request = new ValidateAuthCodeRequest("sso_launcher", "123456789", authCode, id);
                Call<AuthCodeResponse> call = repository.validateAuthCode(request);
                call.enqueue(new retrofit2.Callback<AuthCodeResponse>() {
                    @Override
                    public void onResponse(Call<AuthCodeResponse> call, Response<AuthCodeResponse> response) {
                        switch (response.code()) {
                            case 200:
                                if(response.body().getResponse().equals("Valid code")) {
                                    Bundle bundleToSend = new Bundle();
                                    bundleToSend.putString(LoginFragment.KEY_ACCESS_TOKEN, accessToken);
                                    bundleToSend.putString(LoginFragment.KEY_REFRESH_TOKEN, refreshToken);
                                    bundleToSend.putString(LoginFragment.KEY_ID_TOKEN_USERNAME, userName);
                                    Message message = Message.obtain(null, SUCCESS);
                                    message.obj = bundleToSend;
                                    try {
                                        replyTo.send(message);
                                    } catch (RemoteException rme) {
                                        Toast.makeText(RemoteService.this, "Invocation Failed!!", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Message message = Message.obtain(null, FAIL);
                                    try {
                                        replyTo.send(message);
                                    } catch (RemoteException rme) {
                                        Toast.makeText(RemoteService.this, "Invocation Failed!!", Toast.LENGTH_LONG).show();
                                    }
                                }
                                break;
                            case 401:
                                Toast.makeText(RemoteService.this, "Unauthorized client", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthCodeResponse> call, Throwable t) {

                    }
                });
            }
        }
    }
}
