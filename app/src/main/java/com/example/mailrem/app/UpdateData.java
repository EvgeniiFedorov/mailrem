package com.example.mailrem.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

public class UpdateData extends BroadcastReceiver {

    private final static String LOG_TAG = "mailrem_log";

    private final static int ID_NOTIFICATION = 4;

    private final static String FILE_NAME = "setting";
    private final static String COUNT = "Count";
    private final static String COUNT_DEFAULT_VALUE = "0";
    private final static int SPACED_REPETITION = 3 * 1000;

    private final static String MAIL_HOST = "imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";

    public static void setNextUpdate(Context context, long interval, long uid) {
        Log.d(LOG_TAG, "set next update");

        Intent intentThis = new Intent(context, UpdateData.class);
        intentThis.putExtra("uid", uid);
        intentThis.putExtra("interval", interval);

        PendingIntent pendingThis = PendingIntent.getBroadcast(context, 0,
                intentThis, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingThis);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pendingThis);
    }

    public static void stopUpdate(Context context) {
        Log.d(LOG_TAG, "stop update");

        Intent intentThis = new Intent(context, UpdateData.class);
        PendingIntent pendingThis = PendingIntent.getBroadcast(context, 0, intentThis, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingThis);
    }

    public UpdateData() {
        Log.d(LOG_TAG, "constructor updateData");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "update br onReceive");

        MailAgent mailAgent = new MailAgent();
        long nextUID = 0;

        try {
            long uid = intent.getLongExtra("uid", 0);
            nextUID = uid;
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
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        try {
            DataBase db = new DataBase(context);
            List<MessageWrap> messages = db.getMessagesForNotify();

            Notifications notificator = new Notifications(context);
            notificator.notifyNewMessage(messages, ID_NOTIFICATION);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        long interval = intent.getLongExtra("interval", 0);
        setNextUpdate(context, interval, nextUID);
    }

    private int getCountFromSettings(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String text = sPref.getString(COUNT, COUNT_DEFAULT_VALUE);
        return Integer.parseInt(text);
    }

    private void setCountInSettings(Context context, int count) {
        SharedPreferences sPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(COUNT, Integer.toString(count));
        editor.apply();
    }
}
