package com.example.mailrem;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Message;

public class UpdateService extends Service {

    private final static String LOG_TAG = "log_debug";
    private final static String MAIL_HOST = "imap.yandex.ru";//"imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@yandex.ru";//"ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "create service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "start service");
        try {
            MailAgent mailAgent = new MailAgent();
            mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);
            Message[] messages = mailAgent.getUnreadMessages("INBOX");
            mailAgent.disconnect();
            IMAPMessage message = (IMAPMessage)messages[0];
            Log.d(LOG_TAG, "1");
            //String encoding = message.getEncoding();
            //String text = message.getSubject();
            Log.d(LOG_TAG, "2");
            if (message == null) {
                Log.d(LOG_TAG, "null");
            } else {
                //message.getInputStream();
                //Log.d(LOG_TAG, "input stream");
                Log.d(LOG_TAG, Integer.toString(message.getMessageNumber()));
            }
            Log.d(LOG_TAG, "3");
            //MessageAnalyzer messageAnalyzer = new MessageAnalyzer(messages[0]);
            //textView.setText(messageAnalyzer.getSubject());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        new UpdateData().setAlarmManager(this);
        stopSelf();

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(LOG_TAG, "destroy service");
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
