package com.example.mailrem.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import javax.mail.Message;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UpdateService extends Service {

    private final static String LOG_TAG = "log_debug";
    private final static int ID_NOTIFICATION = 4;
    private final static String MAIL_HOST = "imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "create service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "start service");

        MailAgent mailAgent = new MailAgent();
        try {
            Calendar start = Calendar.getInstance();
            start.set(2015, 9, 1);
            Date startDate = start.getTime();
            Date endDate = new Date();

            mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);
            mailAgent.openFolder("INBOX");

            Message[] messages = mailAgent.getMessagesInPeriod(startDate, endDate);
            List<String> messageTitles = new LinkedList<>();

            for (Message message : messages) {
                if (!MessageAnalyzer.isAnswered(message)) {
                    String title = MessageAnalyzer.getSubject(message);
                    messageTitles.add(title);
                }
            }

            mailAgent.closeFolder();
            mailAgent.disconnect();

            Notifications notificator = new Notifications(getBaseContext());
            notificator.notifyNewMessage(messageTitles, ID_NOTIFICATION);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            stopSelf();
        }
        stopSelf();

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG, "destroy service");
        super.onDestroy();
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
