package com.example.mailrem.app.components;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mailrem.app.pojo.MailAgent;
import com.example.mailrem.app.pojo.MessageWrap;

public class UpdateData extends BroadcastReceiver {

    private final static String LOG_TAG = "mailrem_log";


    private final static String FILE_NAME = "setting";
    private final static String UID_FIELD = "uid";
    private final static String UID_DEFAULT_VALUE = "0";

    //private final static String MAIL_HOST = "imap.mail.ru";
    //private final static int SERVER_PORT = 993;
    //private final static String USER_EMAIL = "ttestname1@mail.ru";
    //private final static String USER_PASSWORD = "testpassword";
    private final static String MAIL_HOST = "imap.gmail.com";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@gmail.com";
    private final static String USER_PASSWORD = "testpassword1";

    private static volatile boolean stopUpdate = false;

    public static void startUpdateProcess(Context context, long interval) {
        Log.d(LOG_TAG, "start update process");
        long uid = getUIDFromSettings(context);

        setNextUpdate(context, interval, uid);
    }

    public static void stopUpdate() {
        Log.d(LOG_TAG, "stop update");
        stopUpdate = true;
    }

    public UpdateData() {
        Log.d(LOG_TAG, "constructor updateData");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "update br onReceive");

        new UpdateThread(context, intent).start();
    }

    private class UpdateThread extends Thread {
        private final Context context;
        private final Intent intent;

        public UpdateThread(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        public void run() {
            long uid = intent.getLongExtra("uid", 0);
            long interval = intent.getLongExtra("interval", 0);
            long nextUID = updateDB(context, uid);

            setNextUpdate(context, interval, nextUID);
        }
    }

    private static void setNextUpdate(Context context, long interval, long uid) {
        Log.d(LOG_TAG, "set next update");

        if (stopUpdate) {
            stopUpdate = false;
            Log.d(LOG_TAG, "set next update delete");
            setUIDToSettings(context, uid);
            return;
        }

        Intent intentThis = new Intent(context, UpdateData.class);
        intentThis.putExtra("uid", uid);
        intentThis.putExtra("interval", interval);

        PendingIntent pendingThis = PendingIntent.getBroadcast(context, 0,
                intentThis, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingThis);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pendingThis);
    }

    private long updateDB(Context context, long uid) {
        try {
            long nextUID = uid;

            MailAgent mailAgent = new MailAgent();
            mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);

            MessageWrap[] messagesWrap = mailAgent.getMessagesSinceUID(uid, "INBOX");

            mailAgent.disconnect();

            DataBase dataBase = new DataBase(context);
            for (MessageWrap messageWrap : messagesWrap) {
                dataBase.addIfNotExistMessage(messageWrap);
                if (messageWrap.getUID() >= nextUID) {
                    nextUID = messageWrap.getUID() + 1;
                }
            }
            return nextUID;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            return uid;
        }
    }

    private static long getUIDFromSettings(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String text = sPref.getString(UID_FIELD, UID_DEFAULT_VALUE);
        return Long.parseLong(text);
    }

    private static void setUIDToSettings(Context context, long uid) {
        SharedPreferences sPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(UID_FIELD, Long.toString(uid));
        editor.apply();
    }
}
