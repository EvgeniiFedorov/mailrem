package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;

public class MainActivity extends Activity {

    /*private final static String MAIL_HOST = "imap.mail.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@mail.ru";
    private final static String USER_PASSWORD = "testpassword";
    private final static String MAIL_HOST = "imap.gmail.com";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname1@gmail.com";
    private final static String USER_PASSWORD = "testpassword1";
    private final static String MAIL_HOST = "imap.yandex.ru";
    private final static int SERVER_PORT = 993;
    private final static String USER_EMAIL = "ttestname2@yandex.ru";
    private final static String USER_PASSWORD = "testpassword2";*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "MainActivity onCreate");

        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(Constants.LOG_TAG, "MainActivity onCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(Constants.LOG_TAG, "MainActivity onOptionsItemSelected");

        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.accounts:
                intent = new Intent(this, AccountListActivity.class);
                startActivity(intent);
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }
}
