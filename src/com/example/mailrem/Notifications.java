package com.example.mailrem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Notifications {

    private final static String LOG_TAG = "log_debug";
    private final static long[] VIBRATION = {120, 110, 100, 90, 80, 70, 60, 50, 40, 30};

    public static void sendNotification(Context context, String message, int idNotification) {
        Log.d(LOG_TAG, "send notification");

        Intent intentToActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(context)
                .addAction(0, "Ignore", pendingIntent)
                .addAction(0, "Answer", pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker("Ticker")
                .setContentTitle("Content title")
                .setContentText(message)
                .setContentInfo("Content info")
                .setSubText("Sub text")
                .setAutoCancel(true)
                .setVibrate(VIBRATION)
                .build();

        notificationManager.notify(idNotification, notification);
    }
}
