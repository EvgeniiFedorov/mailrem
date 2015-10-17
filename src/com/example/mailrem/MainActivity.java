package com.example.mailrem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

    private final static String LOG_TAG = "log_debug";
    private final static int ID_NOTIFICATION = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(LOG_TAG, "create activity");
    }

    public void onClickButtonStart(View v) {
        Log.d(LOG_TAG, "button start click");

        startService(new Intent(this, UpdateService.class));
    }

    public void onClickButtonStop(View v) {
        Log.d(LOG_TAG, "button stop click");

        stopService(new Intent(this, UpdateService.class));
    }

    public void onClickButtonNotification(View v) {
        Log.d(LOG_TAG, "button send notification click");

        new UpdateData().sendNotification(this, "Send from activity", ID_NOTIFICATION);
    }
}
