package com.example.mailrem.app.components;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.activity.MainActivity;
import com.example.mailrem.app.components.activity.MessageViewActivity;
import com.example.mailrem.app.pojo.MessageWrap;
import com.example.mailrem.app.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Notifications {

    private static final long[] VIBRATION = {120, 110, 100, 90, 80, 70, 60, 50, 40, 30};
    private static final int LIGHTS_ON = 1000;
    private static final int LIGHTS_OFF = 5000;

    private static final String SOUND_BOX = "notify_sound";
    private static final String VIBRATION_BOX = "notify_vibration";

    private final Context context;

    public Notifications(Context context) {
        Log.d(Constants.LOG_TAG, "Notifications constructor");

        this.context = context;
    }

    public void notifyNewMessages(Map<MessageWrap, Integer> messages) {
        Log.d(Constants.LOG_TAG, "Notifications notifyNewMessages");

        Set<Map.Entry<MessageWrap, Integer>> set = messages.entrySet();

        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        boolean sound = sharedPreferences.getBoolean(SOUND_BOX, false);
        boolean vibration = sharedPreferences.getBoolean(VIBRATION_BOX, true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        for (int status = Constants.START_STAGE; status < Constants.COUNT_STAGE - 1;
                ++status) {

            List<MessageWrap> notifyMessages = new LinkedList<MessageWrap>();

            for (Map.Entry<MessageWrap, Integer> record : set) {
                if (record.getValue() == status) {
                    notifyMessages.add(record.getKey());
                }
            }

            if (notifyMessages.size() != 0) {
                NotificationCompat.Builder builder;

                if (notifyMessages.size() == 1) {
                    builder = notifyNewMessage(notifyMessages.get(0));
                } else {
                    builder = notifyNewMessages(notifyMessages);
                }

                switch (status) {
                    case 0:
                        builder.setSmallIcon(R.mipmap.notify_lv1);
                        break;
                    case 1:
                        builder.setSmallIcon(R.mipmap.notify_lv2);
                        break;
                    case 2:
                        builder.setSmallIcon(R.mipmap.notify_lv3);
                        break;
                    default:
                }


                if (vibration) {
                    builder.setVibrate(VIBRATION);
                }

                if (sound) {
                    Uri soundUri = RingtoneManager.getDefaultUri(
                            RingtoneManager.TYPE_NOTIFICATION);
                    builder.setSound(soundUri);
                }

                builder.setLights(Color.BLUE, LIGHTS_ON, LIGHTS_OFF);

                notificationManager.notify(status, builder.build());
            }
        }
    }

    private NotificationCompat.Builder notifyNewMessages(List<MessageWrap> messages) {
        Log.d(Constants.LOG_TAG, "Notifications notifyNewMessages");

        Intent intentToActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Not answered messages");

        for (MessageWrap message : messages) {
            inboxStyle.addLine(message.getFromName() + " " + message.getSubject());
        }

        return new NotificationCompat.Builder(context)
                .setContentTitle(String.valueOf(messages.size()) +
                        " not answered messages")
                .setContentIntent(pendingIntent)
                .setTicker("Reminder of the answer")
                .setStyle(inboxStyle)
                .setAutoCancel(true);
    }

    private NotificationCompat.Builder notifyNewMessage(MessageWrap message) {
        Log.d(Constants.LOG_TAG, "Notifications notifyNewMessage");

        Intent intent = new Intent(context, MessageViewActivity.class);
        intent.putExtra(Constants.MESSAGE_INTENT_FIELD, message);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Builder(context)
                .setContentTitle(message.getFromName() +
                        " <" + message.getFromAddress() + ">")
                .setContentText(message.getSubject())
                .setContentIntent(pendingIntent)
                .setTicker("Reminder of the answer")
                .setAutoCancel(true);
    }
}
