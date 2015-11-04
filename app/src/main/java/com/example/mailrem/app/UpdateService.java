package com.example.mailrem.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {

    private final static String LOG_TAG = "log_debug";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "create service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "start service");

        new UpdateData().setAlarmManager(this);
        stopSelf();

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG, "destroy service");
        super.onDestroy();
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
