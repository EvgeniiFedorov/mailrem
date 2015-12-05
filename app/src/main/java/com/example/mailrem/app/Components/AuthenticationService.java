package com.example.mailrem.app.components;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class AuthenticationService extends Service {
    private static final Object lock = new Object();
    private MailAuthenticator authenticator;

    @Override
    public void onCreate() {
        synchronized (lock) {
            if (authenticator == null) {
                authenticator = new MailAuthenticator(getApplicationContext());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
