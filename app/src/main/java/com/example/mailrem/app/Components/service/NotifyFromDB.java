package com.example.mailrem.app.components.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.MessagesDataBase;
import com.example.mailrem.app.components.Notifications;
import com.example.mailrem.app.pojo.MessageWrap;

import java.util.Map;

public class NotifyFromDB extends BroadcastReceiver {

    private static final int DELAY_NOTIFY = 10 * 1000;

    private static volatile boolean stopNotify = false;

    public static void startNotifyProcess(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB startNotifyProcess");

        setNextNotify(context);
    }

    public static void stopNotify() {
        Log.d(Constants.LOG_TAG, "NotifyFromDB stopNotify");

        stopNotify = true;
    }

    public NotifyFromDB() {
        Log.d(Constants.LOG_TAG, "NotifyFromDB constructor");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB onReceive");

        new NotifyThread(context).start();
    }

    private class NotifyThread extends Thread {
        private final Context context;

        public NotifyThread(Context context) {
            Log.d(Constants.LOG_TAG, "NotifyTread constructor");

            this.context = context;
        }

        public void run() {
            Log.d(Constants.LOG_TAG, "NotifyTread run");

            notifyFromDB(context);
            setNextNotify(context);
        }
    }

    private static void setNextNotify(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB setNextNotify");

        if (stopNotify) {
            Log.i(Constants.LOG_TAG, "NotifyFromDB setNextNotify: update cancel");

            stopNotify = false;
            return;
        }

        int nextNotifyTime = getNextNotifyTime(context) + DELAY_NOTIFY;

        Intent intentThis = new Intent(context, NotifyFromDB.class);

        PendingIntent pendingThis = PendingIntent.getBroadcast(context, 0,
                intentThis, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingThis);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + nextNotifyTime, pendingThis);
    }

    private void notifyFromDB(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB notifyFromDB");

        Notifications notifier = new Notifications(context);
        MessagesDataBase db = MessagesDataBase.getInstance(context);

        Map<MessageWrap, Integer> messages = db.getAndUpdateMessagesForNotify();
        notifier.notifyNewMessages(messages);
    }

    private static int getNextNotifyTime(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB getNextNotifyTime");

        MessagesDataBase db = MessagesDataBase.getInstance(context);
        return db.nextNotifyTime();
    }
}
