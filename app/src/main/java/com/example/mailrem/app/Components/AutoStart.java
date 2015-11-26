package com.example.mailrem.app.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {

    private final static String LOG_TAG = "mailrem_log";

    private final static int ID_NOTIFICATION = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "auto start");

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            new Notifications(context)
                    .sendNotification("Auto start", ID_NOTIFICATION);
        }
    }
}
