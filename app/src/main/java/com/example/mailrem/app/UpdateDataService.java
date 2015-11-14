package com.example.mailrem.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateDataService extends IntentService {

    private final static String LOG_TAG = "mailrem_log";

    private final static String MAIL_HOST = "imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";

    public static void setNextUpdate(Context context, long interval, long uid) {
        Intent intentThis = new Intent(context, UpdateDataService.class);
        Log.d(LOG_TAG, "uid from set ************* " + String.valueOf(uid));
        intentThis.putExtra("uid", uid);
        intentThis.putExtra("interval", interval);
        PendingIntent pendingThis = PendingIntent.getService(context, 0, intentThis, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingThis);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pendingThis);
    }

    public static void stopUpdate(Context context) {
        Intent intentThis = new Intent(context, UpdateDataService.class);
        PendingIntent pendingThis = PendingIntent.getService(context, 0, intentThis, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingThis);
    }

    public UpdateDataService() {
        super("UpdateDataService");
        Log.d(LOG_TAG, "constructor intentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "new intent to intentService");
        MailAgent mailAgent = new MailAgent();
        long maxUID = 0;

        try {
            long uid = intent.getLongExtra("uid", 10);
            maxUID = uid;
            mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);

            Log.d(LOG_TAG, "uid ************* " + String.valueOf(uid));
            MessageWrap[] messagesWrap = mailAgent.getMessagesSinceUID(uid, "INBOX");

            mailAgent.disconnect();

            DataBase dataBase = new DataBase(getBaseContext());
            for (MessageWrap messageWrap : messagesWrap) {
                dataBase.addIfNotExistMessage(messageWrap);
                if (Long.valueOf(messageWrap.getUID()).compareTo(maxUID) >= 0) {
                    maxUID = messageWrap.getUID() + 1;
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        long interval = intent.getLongExtra("interval", 1000);
        Log.d(LOG_TAG, "maxuid ************* " + String.valueOf(maxUID));
        setNextUpdate(getBaseContext(), interval, maxUID);
    }
}
