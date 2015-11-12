package com.example.mailrem.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

public class Notifications {

    private final static String LOG_TAG = "log_debug";
    private final static long[] VIBRATION = {120, 110, 100, 90, 80, 70, 60, 50, 40, 30};

    private final Context context;

    public Notifications(Context context) {
        this.context = context;
    }

    public void notifyNewMessage(List<String> messageTitles, int idNotification) {
        Log.d(LOG_TAG, "send notification");

        Intent intentToActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Not answered message");

        for (String title : messageTitles) {
            inboxStyle.addLine(title);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .addAction(0, "Later", pendingIntent)
                .addAction(0, "Answer", pendingIntent)
                .addAction(0, "Ignore", pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(String.valueOf(messageTitles.size()) +
                        " not answered messages")
                .setContentText("Content text")
                .setContentIntent(pendingIntent)
                .setTicker("Reminder of the answer")
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setVibrate(VIBRATION);

        notificationManager.notify(idNotification, mBuilder.build());
    }

    public void sendNotification(String message, int idNotification) {
        Log.d(LOG_TAG, "send notification");

        Intent intentToActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .addAction(0, "Ignore", pendingIntent)
                .addAction(0, "Answer", pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Content title")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setTicker("Ticker")
                .setContentInfo("Content info")
                .setSubText("Sub text")
                .setAutoCancel(true)
                .setVibrate(VIBRATION);

        notificationManager.notify(idNotification, mBuilder.build());
    }
}
