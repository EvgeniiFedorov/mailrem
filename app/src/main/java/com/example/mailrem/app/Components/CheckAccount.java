package com.example.mailrem.app.components;

import android.os.AsyncTask;
import android.util.Log;
import com.example.mailrem.app.pojo.Account;
import com.example.mailrem.app.pojo.MailAgent;

public class CheckAccount extends AsyncTask<Account, Void, Account> {

    private final static String LOG_TAG = "mailrem_log";

    private OnTaskCompleted listener;

    @Override
    protected Account doInBackground(Account... params) {

        try {
            MailAgent mailAgent = new MailAgent();
            mailAgent.connect(params[0]);
            mailAgent.disconnect();

            return params[0];
        } catch (Exception e) {
            Log.i(LOG_TAG, "authorization fail");
            return null;
        }
    }

    @Override
    protected void onPostExecute(Account result) {
        listener.onTaskCompleted();
    }

    public void setOnTaskCompletedListener(OnTaskCompleted listener) {
        this.listener = listener;
    }
}
