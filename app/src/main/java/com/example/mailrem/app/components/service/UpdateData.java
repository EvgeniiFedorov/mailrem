package com.example.mailrem.app.components.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.AccountsDataBase;
import com.example.mailrem.app.components.MessagesDataBase;
import com.example.mailrem.app.pojo.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UpdateData extends BroadcastReceiver {

    private static final String INTERVAL_INTENT_FIELD = "login";

    private static volatile boolean stopUpdate = false;

    private static final Lock lock = new ReentrantLock();

    public static synchronized void startUpdateProcess(Context context, long interval) {
        Log.d(Constants.LOG_TAG, "UpdateData startUpdateProcess");

        setNextUpdate(context.getApplicationContext(), interval);
    }

    public static synchronized void stopUpdate(Context context) {
        Log.d(Constants.LOG_TAG, "UpdateData stopUpdate");

        stopUpdate = true;
        setNextUpdate(context.getApplicationContext(), 0);
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
                    long interval = intent.getLongExtra(INTERVAL_INTENT_FIELD, 0);
                    updateDB(context);

                    setNextUpdate(context, interval);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private static void setNextUpdate(Context context, long interval) {
        Log.d(Constants.LOG_TAG, "UpdateDate setNextUpdate");

        Intent intentThis = new Intent(context, UpdateData.class);
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
                stopUpdate = false;
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + interval, pendingThis);
            }
        } finally {
            lock.unlock();
        }
    }

    private void updateDB(Context context) {
        Log.d(Constants.LOG_TAG, "UpdateDate updateDB");

        boolean changeMessageDB = false;

        try {
            MailAgent mailAgent = new MailAgent();

            AccountsDataBase dbAccount = AccountsDataBase.getInstance(context);
            dbAccount.open();

            List<Account> accounts = dbAccount.getAllAccounts();

            MessagesDataBase dbMessage = MessagesDataBase.getInstance(context);
            dbMessage.open();

            for (Account account : accounts) {
                long lastDate = dbAccount.getLastDate(account);

                mailAgent.connect(account);
                List<MessageWrap> messagesWrap = mailAgent
                        .getUnreadMessagesFromAllFoldersSinceDate(new Date(lastDate));
                mailAgent.disconnect();

                for (MessageWrap messageWrap : messagesWrap) {
                    if (messageWrap.getDate().getTime() > lastDate) {
                        lastDate = messageWrap.getDate().getTime();
                    }
                }

                MessageAnalyzer analyzer = new MessageAnalyzer(context);
                messagesWrap = analyzer.analyzeMessages(messagesWrap);

                for (MessageWrap messageWrap : messagesWrap) {
                    if (dbMessage.addIfNotExistMessage(messageWrap)) {
                        changeMessageDB = true;
                    }
                }

                dbAccount.updateLastDate(account, lastDate);
            }

            dbMessage.close();
            dbAccount.close();

            if (changeMessageDB) {
                ProcessesManager.restartNotify(context);
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "UpdateDate updateDB: exception - "
                    + e.getMessage());
        }
    }
}
