package com.example.mailrem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class UpdateData extends BroadcastReceiver {

    private final static String LOG_TAG = "log_debug";
    private final static String FILE_NAME = "setting";
    private final static String COUNT = "Count";
    private final static String COUNT_DEFAULT_VALUE = "0";
    private final static int SPACED_REPETITION = 3 * 1000;
    private final static int COUNT_REPEAT = 3;
    private final static int ID_NOTIFICATION = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "update br start");

        int count = getCountFromSettings(context);

        Notifications.sendNotification(context, Integer.toString(count), ID_NOTIFICATION);

        if (count < COUNT_REPEAT) {
            setAlarmManager(context);
            ++count;
        }
        else {
            count = 0;
        }

        setCountInSettings(context, count);
    }

    private int getCountFromSettings(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String text = sPref.getString(COUNT, COUNT_DEFAULT_VALUE);
        return Integer.parseInt(text);
    }

    private void setCountInSettings(Context context, int count) {
        SharedPreferences sPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(COUNT, Integer.toString(count));
        editor.apply();
    }

    public void setAlarmManager(Context context) {
        Log.d(LOG_TAG, "set alarm");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentToBR = new Intent(context, UpdateData.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intentToBR, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + SPACED_REPETITION, pendingIntent);
    }
}
