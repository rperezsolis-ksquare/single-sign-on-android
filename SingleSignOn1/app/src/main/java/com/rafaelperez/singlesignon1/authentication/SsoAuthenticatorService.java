package com.rafaelperez.singlesignon1.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SsoAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        SsoAuthenticator authenticator = new SsoAuthenticator(this);
        return authenticator.getIBinder();
    }
}
