package com.example.mailrem.app.components.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;

import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.AccountsDataBase;
import com.example.mailrem.app.components.MessagesDataBase;
import com.example.mailrem.app.pojo.Account;
import com.example.mailrem.app.pojo.MailAgent;
import com.example.mailrem.app.pojo.MessageWrap;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UpdateData extends BroadcastReceiver {

    private static final String FILE_NAME = "setting";
    private static final String UID_FIELD = "uid";
    private static final String UID_INTENT_FIELD = "uid";
    private static final String INTERVAL_INTENT_FIELD = "login";

    private static final String UID_DEFAULT_VALUE = "0";

    private static volatile boolean stopUpdate = false;

    private static final Lock lock = new ReentrantLock();

    public static synchronized void startUpdateProcess(Context context, long interval) {
        Log.d(Constants.LOG_TAG, "UpdateData startUpdateProcess");

        long uid = getUIDFromSettings(context);
        setNextUpdate(context.getApplicationContext(), interval, uid);
    }

    public static synchronized void stopUpdate(Context context) {
        Log.d(Constants.LOG_TAG, "UpdateData stopUpdate");

        stopUpdate = true;
        setNextUpdate(context.getApplicationContext(), 0, 0);
    }

    public UpdateData() {
        Log.d(Constants.LOG_TAG, "UpdateData constructor");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "UpdateData onReceive");

        new UpdateThread(context, intent).start();
    }

    private class UpdateThread extends Thread {
        private final Context context;
        private final Intent intent;

        public UpdateThread(Context context, Intent intent) {
            Log.d(Constants.LOG_TAG, "UpdateThread constructor");

            this.context = context;
            this.intent = intent;
        }

        public void run() {
            Log.d(Constants.LOG_TAG, "UpdateThread run");

            if (lock.tryLock()) {
                try {
                    long uid = intent.getLongExtra(UID_INTENT_FIELD, 0);
                    long interval = intent.getLongExtra(INTERVAL_INTENT_FIELD, 0);
                    long nextUID = updateDB(context, uid);

                    setNextUpdate(context, interval, nextUID);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private static void setNextUpdate(Context context, long interval, long uid) {
        Log.d(Constants.LOG_TAG, "UpdateDate setNextUpdate");

        Intent intentThis = new Intent(context, UpdateData.class);
        intentThis.putExtra(UID_INTENT_FIELD, uid);
        intentThis.putExtra(INTERVAL_INTENT_FIELD, interval);

        PendingIntent pendingThis = PendingIntent.getBroadcast(context, 0,
                intentThis, PendingIntent.FLAG_UPDATE_CURRENT);

        lock.lock();
        try {
            AlarmManager alarmManager = (AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);

            if (Looper.myLooper() == Looper.getMainLooper() && stopUpdate) {
                Log.i(Constants.LOG_TAG, "UpdateDate setNextUpdate: update cancel");

                alarmManager.cancel(pendingThis);
                setUIDToSettings(context, uid);
                stopUpdate = false;
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + interval, pendingThis);
            }
        } finally {
            lock.unlock();
        }
    }

    private long updateDB(Context context, long uid) {
        Log.d(Constants.LOG_TAG, "UpdateDate updateDB");

        try {
            long nextUID = uid;

            MailAgent mailAgent = new MailAgent();

            AccountsDataBase db = AccountsDataBase.getInstance(context);
            db.open();

            List<Account> accounts = db.getAllAccounts();
            db.close();

            MessagesDataBase messagesDataBase = MessagesDataBase.getInstance(context);

            for (Account account : accounts) {
                /*mailAgent.connect(account);
                List<MessageWrap> messagesWrap = mailAgent.getMessagesFromAllFoldersSinceUID(uid);
                mailAgent.disconnect();

                for (MessageWrap messageWrap : messagesWrap) {
                    messagesDataBase.addIfNotExistMessage(messageWrap);
                    if (messageWrap.getUID() >= nextUID) {
                        nextUID = messageWrap.getUID() + 1;
                    }
                }*/
            }

            return nextUID;
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "UpdateDate updateDB: exception - "
                    + e.getMessage());

            return uid;
        }
    }

    private static long getUIDFromSettings(Context context) {
        Log.d(Constants.LOG_TAG, "UpdateDate getUIDFromSettings");

        SharedPreferences sPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String text = sPref.getString(UID_FIELD, UID_DEFAULT_VALUE);
        return Long.parseLong(text);
    }

    private static void setUIDToSettings(Context context, long uid) {
        Log.d(Constants.LOG_TAG, "UpdateDate setUIDToSettings");

        SharedPreferences sPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(UID_FIELD, Long.toString(uid));
        editor.apply();
    }
}
