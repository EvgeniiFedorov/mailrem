package com.example.mailrem.app.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ScheduleManager {

    private static final String DURATION_STAGE = "stage_duration_";
    private static final String FREQUENCY_STAGE = "stage_frequency_";

    private final SharedPreferences sharedPreferences;

    public ScheduleManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int frequencyStage(int status) {
        String frequency = sharedPreferences.getString(FREQUENCY_STAGE + status, "err");
        return 60 * Integer.parseInt(frequency);
    }

    public int durationStage(int status) {

        String duration = sharedPreferences.getString(DURATION_STAGE + status, "err");
        return 60 * 60 * Integer.parseInt(duration);
    }
}
