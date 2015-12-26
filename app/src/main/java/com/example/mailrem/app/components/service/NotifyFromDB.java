package com.example.mailrem.app.components.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.MessagesDataBase;
import com.example.mailrem.app.components.Notifications;
import com.example.mailrem.app.pojo.MessageWrap;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NotifyFromDB extends BroadcastReceiver {

    private static final int DELAY_NOTIFY = 10;

    private static volatile boolean stopNotify = false;

    private static final Lock lock = new ReentrantLock();

    public static synchronized void startNotifyProcess(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB startNotifyProcess");

        setNextNotify(context.getApplicationContext());
    }

    public static synchronized void stopNotify(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB stopNotify");

        stopNotify = true;
        setNextNotify(context.getApplicationContext());
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

            if (lock.tryLock()) {
                try {
                    notifyFromDB(context);
                    setNextNotify(context);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private static void setNextNotify(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB setNextNotify");

        Intent intentThis = new Intent(context, NotifyFromDB.class);

        PendingIntent pendingThis = PendingIntent.getBroadcast(context, 0,
                intentThis, PendingIntent.FLAG_UPDATE_CURRENT);

        lock.lock();
        try {
            AlarmManager alarmManager = (AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);

            if (Looper.myLooper() == Looper.getMainLooper() && stopNotify) {
                Log.i(Constants.LOG_TAG, "NotifyFromDB setNextNotify: notify cancel");

                alarmManager.cancel(pendingThis);
                stopNotify = false;
            } else {
                int nextNotifyTime = getNextNotifyTime(context) + DELAY_NOTIFY;

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + nextNotifyTime * 1000, pendingThis);
            }
        } finally {
            lock.unlock();
        }
    }

    private void notifyFromDB(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB notifyFromDB");

        Notifications notifier = new Notifications(context);
        MessagesDataBase db = MessagesDataBase.getInstance(context);
        db.open();

        Map<MessageWrap, Integer> messages = db.getAndUpdateMessagesForNotify();
        db.close();

        notifier.notifyNewMessages(messages);
    }

    private static int getNextNotifyTime(Context context) {
        Log.d(Constants.LOG_TAG, "NotifyFromDB getNextNotifyTime");

        MessagesDataBase db = MessagesDataBase.getInstance(context);
        db.open();

        int nextNotifyTime = db.nextNotifyTime();
        db.close();
        return nextNotifyTime;
    }
}
