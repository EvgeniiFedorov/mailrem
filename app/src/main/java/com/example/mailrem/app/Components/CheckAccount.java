package com.example.mailrem.app.components;

import android.os.AsyncTask;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.pojo.Account;
import com.example.mailrem.app.pojo.MailAgent;

public class CheckAccount extends AsyncTask<Account, Void, Account> {

    private OnTaskCompleted listener;

    @Override
    protected Account doInBackground(Account... params) {
        Log.d(Constants.LOG_TAG, "CheckAccount doInBackground");

        try {
            MailAgent mailAgent = new MailAgent();
            mailAgent.connect(params[0]);
            mailAgent.disconnect();

            return params[0];
        } catch (Exception e) {
            Log.i(Constants.LOG_TAG, "CheckAccount doInBackground: authorization fail");
            return null;
        }
    }

    @Override
    protected void onPostExecute(Account result) {
        Log.d(Constants.LOG_TAG, "CheckAccount onPostExecute");

        listener.onTaskCompleted();
    }

    public void setOnTaskCompletedListener(OnTaskCompleted listener) {
        Log.d(Constants.LOG_TAG, "CheckAccount setOnTaskCompletedListener");

        this.listener = listener;
    }
}
