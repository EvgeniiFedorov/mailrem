package com.example.mailrem.app.components;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "SettingsFragment onCreate");

        addPreferencesFromResource(R.xml.preferences);
    }
}