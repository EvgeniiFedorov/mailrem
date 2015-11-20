package com.example.mailrem.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final static String LOG_TAG = "mailrem_log";

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

        UpdateData.startUpdateProcess(getBaseContext(), 10 * 1000);
    }

    public void onClickButtonStop(View v) {
        Log.d(LOG_TAG, "button stop click");

        UpdateData.stopUpdate(getBaseContext());
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

            MessageWrap[] messages = mailAgent.getUnreadMessages("INBOX");
            MessageWrap message = messages[0];

            textView.setText(message.getSubject());

            mailAgent.disconnect();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }
}
