package com.example.mailrem.app.components.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.pojo.ProcessesManager;

public class ConnectivityChange extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "ConnectivityChange onReceive");

        ProcessesManager.restart(context);
    }
}
