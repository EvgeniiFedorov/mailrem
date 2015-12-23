package com.example.mailrem.app.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.mailrem.app.Constants;

public class ScheduleManager {

    private static final String DURATION_STAGE = "stage_duration_";
    private static final String FREQUENCY_STAGE = "stage_frequency_";

    private final SharedPreferences sharedPreferences;

    public ScheduleManager(Context context) {
        Log.d(Constants.LOG_TAG, "ScheduleManager constructor");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int frequencyStage(int status) {
        Log.d(Constants.LOG_TAG, "ScheduleManager frequencyStage");

        if (status == Constants.COUNT_STAGE - 1) {
            return durationStage(status);
        }

        String frequency = sharedPreferences.getString(FREQUENCY_STAGE + status, "err");
        return 60 * Integer.parseInt(frequency);
    }

    public int durationStage(int status) {
        Log.d(Constants.LOG_TAG, "ScheduleManager durationStage");

        String duration = sharedPreferences.getString(DURATION_STAGE + status, "err");
        return 60 * Integer.parseInt(duration);
    }
}
