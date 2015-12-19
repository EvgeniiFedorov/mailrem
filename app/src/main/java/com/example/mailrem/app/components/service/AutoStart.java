package com.example.mailrem.app.components.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.pojo.ProcessesManager;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "AutoStart onReceive");

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ProcessesManager.start(context);
        }
    }
}
