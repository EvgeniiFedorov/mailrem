package com.example.mailrem;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
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
    private final static int COUNT_REPEAT = 10;
    private final static int ID_NOTIFICATION = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "update br start");

        int count = getCountFromSettings(context);

        sendNotification(context, Integer.toString(count), ID_NOTIFICATION);

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

    public void sendNotification(Context context, String message, int idNotification) {
        Log.d(LOG_TAG, "send notification");
        Intent intentToActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        long[] vibration = new long[10];
        for (int i = 0; i < 10; ++i) {
            vibration[i] = (12 - i) * 10;
        }

        Notification notification = new Notification.Builder(context)
                .addAction(R.drawable.ic_launcher, "1", pendingIntent)
                .addAction(R.drawable.ic_launcher, "2", pendingIntent)
                .addAction(R.drawable.ic_launcher, "3", pendingIntent)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Ticker")
                .setContentTitle("Content title")
                .setContentText(message)
                .setContentInfo("Content info")
                .setSubText("Sub text")
                .setAutoCancel(true)
                .setVibrate(vibration)
                .build();

        notificationManager.notify(idNotification, notification);
    }
}
