package com.example.mailrem.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final static String LOG_TAG = "log_debug";
    private final static int ID_NOTIFICATION = 2;
    private final static String MAIL_HOST = "imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";

    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "create activity");
        setContentView(R.layout.activity_main);

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

        new Notifications(this)
                .sendNotification("Send from activity", ID_NOTIFICATION);
    }

    public void onClickButtonMail(View v) {
        Log.d(LOG_TAG, "button get message");
        try {
            MailAgent mailAgent = new MailAgent();
            mailAgent.connect(MAIL_HOST, SERVER_PORT, USER_EMAIL, USER_PASSWORD);
            mailAgent.openFolder("INBOX");

            MessageWrap[] messages = mailAgent.getUnreadMessages();
            MessageWrap message = messages[0];

            textView.setText(message.getSubject());

            mailAgent.closeFolder();
            mailAgent.disconnect();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
