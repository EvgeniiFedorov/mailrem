package com.example.mailrem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import javax.mail.Message;

public class MainActivity extends Activity {

    private final static String LOG_TAG = "log_debug";
    private final static int ID_NOTIFICATION = 2;
    private final static String MAIL_HOST = "imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";

    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "create activity");
        setContentView(R.layout.main);

        textView = (TextView) findViewById(R.id.viewMail);
    }

    public void onClickButtonStart(View v) {
        Log.d(LOG_TAG, "button start click");

        startService(new Intent(this, UpdateService.class));
    }

    public void onClickButtonStop(View v) {
        Log.d(LOG_TAG, "button stop click");

        stopService(new Intent(this, UpdateService.class));
    }

    public void onClickButtonNotification(View v) {
        Log.d(LOG_TAG, "button send notification click");

        new UpdateData().sendNotification(this, "Send from activity", ID_NOTIFICATION);
    }

    public void onClickButtonMail(View v) {
        Log.d(LOG_TAG, "button get message");
        try {
            MailAgent mailAgent = new MailAgent();
            mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);
            Message[] messages = mailAgent.getUnreadMessages("INBOX");
            mailAgent.disconnect();
            Message message = messages[0];
            Log.d(LOG_TAG, "1");
            //String encoding = message.getEncoding();
            //String text = message.getSubject();
            Log.d(LOG_TAG, "2");
            if (message == null) {
                Log.d(LOG_TAG, "null");
            } else {
                Log.d(LOG_TAG, message.getSubject());
            }
            Log.d(LOG_TAG, "3");
            //MessageAnalyzer messageAnalyzer = new MessageAnalyzer(messages[0]);
            //textView.setText(messageAnalyzer.getSubject());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
