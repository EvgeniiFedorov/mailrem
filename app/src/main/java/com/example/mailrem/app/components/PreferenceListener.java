package com.example.mailrem.app.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.pojo.ProcessesManager;

public class PreferenceListener implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String UPDATE = "update_";
    private static final String NOTIFY = "notify_";
    private static final String STAGE = "stage_";

    private final Context context;

    public PreferenceListener(Context context) {
        Log.d(Constants.LOG_TAG, "PreferenceListener constructor");

        this.context = context;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(Constants.LOG_TAG, "PreferenceListener onSharedPreferenceChanged");

        if (key.startsWith(UPDATE)) {
            ProcessesManager.restartUpdate(context);
        } else if (key.startsWith(NOTIFY) || key.startsWith(STAGE)) {
            ProcessesManager.restartNotify(context);
        }
    }
}
