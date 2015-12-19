package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.PreferenceListener;
import com.example.mailrem.app.components.SettingsFragment;

public class SettingsActivity extends Activity {

    private PreferenceListener preferenceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "SettingsActivity onCreate");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        preferenceListener = new PreferenceListener(this);
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        preferences.registerOnSharedPreferenceChangeListener(preferenceListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Constants.LOG_TAG, "SettingsActivity onDestroy");

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener);
    }
}
