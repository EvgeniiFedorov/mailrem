package com.example.mailrem.app.components.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {

    private static final String LOG_TAG = "mailrem_log";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "auto start");

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //action
        }
    }
}
