package com.example.mailrem.app.components.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mailrem.app.components.MessagesDataBase;
import com.example.mailrem.app.components.Notifications;
import com.example.mailrem.app.pojo.MessageWrap;

import java.util.List;

public class NotifyFromDB extends BroadcastReceiver {

    private final static String LOG_TAG = "mailrem_log";

    private static int idNotification = 1;
    private static final int MAX_ID = 10;

    private static volatile boolean stopNotify = false;

    public static void startNotifyProcess(Context context, long interval) {
        Log.d(LOG_TAG, "start notify process");
        setNextNotify(context, interval);
    }

    public static void stopNotify() {
        Log.d(LOG_TAG, "stop notify");
        stopNotify = true;
    }

    public NotifyFromDB() {
        Log.d(LOG_TAG, "constructor NotifyFromDB");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "notify br onReceive");

        new NotifyThread(context, intent).start();
    }

    private class NotifyThread extends Thread {
        private final Context context;
        private final Intent intent;

        public NotifyThread(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        public void run() {
            long interval = intent.getLongExtra("interval", 0);

            notifyFromDB(context);

            setNextNotify(context, interval);
        }
    }

    private static void setNextNotify(Context context, long interval) {
        Log.d(LOG_TAG, "set next notify");

        if (stopNotify) {
            stopNotify = false;
            Log.d(LOG_TAG, "set next notify delete");
            return;
        }

        Intent intentThis = new Intent(context, NotifyFromDB.class);
        intentThis.putExtra("interval", interval);

        PendingIntent pendingThis = PendingIntent.getBroadcast(context, 0,
                intentThis, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingThis);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pendingThis);
    }

    private void notifyFromDB(Context context) {
        try {
            MessagesDataBase db = new MessagesDataBase(context);
            List<MessageWrap> messages = db.getAndUpdateMessagesForNotify();

            Notifications notificator = new Notifications(context);

            if (messages.size() > 1) {
                notificator.notifyNewMessage(messages, idNotification);

                if (idNotification == MAX_ID) {
                    idNotification = 1;
                } else {
                    idNotification++;
                }
            } else if (messages.size() == 1){
                notificator.notifyNewMessage(messages.get(0), idNotification);

                if (idNotification == MAX_ID) {
                    idNotification = 1;
                } else {
                    idNotification++;
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
