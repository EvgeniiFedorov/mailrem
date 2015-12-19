package com.example.mailrem.app.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.pojo.ProcessesManager;

public class PreferenceListener implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private final Context context;

    public PreferenceListener(Context context) {
        Log.d(Constants.LOG_TAG, "PreferenceListener constructor");

        this.context = context;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(Constants.LOG_TAG, "PreferenceListener onSharedPreferenceChanged");

        ProcessesManager.restart(context);
    }
}
