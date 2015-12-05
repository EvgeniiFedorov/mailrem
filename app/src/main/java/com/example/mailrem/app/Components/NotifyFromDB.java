package com.example.mailrem.app.components;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mailrem.app.pojo.MessageWrap;

import java.util.List;

public class NotifyFromDB extends BroadcastReceiver {

    private final static String LOG_TAG = "mailrem_log";

    private final static int ID_NOTIFICATION = 1;

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
            DataBase db = new DataBase(context);
            List<MessageWrap> messages = db.getMessagesForNotify();

            Notifications notificator = new Notifications(context);
            notificator.notifyNewMessage(messages, ID_NOTIFICATION);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
