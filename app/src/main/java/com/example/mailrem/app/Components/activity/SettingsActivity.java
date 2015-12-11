package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.os.Bundle;
import com.example.mailrem.app.components.SettingsFragment;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}