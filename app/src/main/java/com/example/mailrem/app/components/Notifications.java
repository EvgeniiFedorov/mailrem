package com.example.mailrem.app.components;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.activity.MainActivity;
import com.example.mailrem.app.pojo.MessageWrap;
import com.example.mailrem.app.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Notifications {

    private final static long[] VIBRATION = {120, 110, 100, 90, 80, 70, 60, 50, 40, 30};
    private final Context context;

    public Notifications(Context context) {
        Log.d(Constants.LOG_TAG, "Notifications constructor");

        this.context = context;
    }

    public void notifyNewMessages(Map<MessageWrap, Integer> messages) {
        Log.d(Constants.LOG_TAG, "Notifications notifyNewMessages");

        Set<Map.Entry<MessageWrap, Integer>> set = messages.entrySet();
        switch (set.size()) {
            case 0:
                return;
            case 1:
                Map.Entry<MessageWrap, Integer> entry =
                        set.iterator().next();
                notifyNewMessage(entry.getKey(), entry.getValue());
                return;
            default:
                for (int status = Constants.START_STAGE;
                     status < Constants.COUNT_STAGE - 1; ++status) {
                    List<MessageWrap> notifyMessages = new LinkedList<MessageWrap>();

                    for (Map.Entry<MessageWrap, Integer> record : set) {
                        if (record.getValue() == status) {
                            notifyMessages.add(record.getKey());
                        }
                    }

                    notifyNewMessages(notifyMessages, status);
                }
        }
    }

    private void notifyNewMessages(List<MessageWrap> messages, int status) {
        Log.d(Constants.LOG_TAG, "Notifications notifyNewMessages");

        Intent intentToActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Not answered messages");

        for (MessageWrap message : messages) {
            inboxStyle.addLine(message.getFrom() + " " + message.getSubject());
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(String.valueOf(messages.size()) +
                        " not answered messages")
                .setContentText("Content text")
                .setContentIntent(pendingIntent)
                .setTicker("Reminder of the answer")
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setVibrate(VIBRATION);

        notificationManager.notify(status, mBuilder.build());
    }

    private void notifyNewMessage(MessageWrap message, int status) {
        Log.d(Constants.LOG_TAG, "Notifications notifyNewMessage");

        Intent intentToActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .addAction(0, "Later", pendingIntent)
                .addAction(0, "Answer", pendingIntent)
                .addAction(0, "Ignore", pendingIntent)
                .setSmallIcon(R.mipmap.notify_lv3)
                .setContentTitle("GitHub <noreply@github.com>")
                .setContentText(message.getSubject())
                .setContentIntent(pendingIntent)
                .setTicker("Reminder of the answer")
                .setAutoCancel(true)
                .setVibrate(VIBRATION);

        notificationManager.notify(status, mBuilder.build());
    }
}
