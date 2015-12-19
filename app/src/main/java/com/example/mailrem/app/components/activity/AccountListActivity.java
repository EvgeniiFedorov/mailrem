package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.AccountsDataBase;
import com.example.mailrem.app.pojo.Account;
import com.example.mailrem.app.pojo.ProcessesManager;

public class AccountListActivity extends Activity {

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "AccountListActivity onCreate");

        setContentView(R.layout.activity_accounts_screen);
    }

    public void onClickAddAccount(View view) {
        Log.d(Constants.LOG_TAG, "AccountListActivity onClickAddAccount");

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void onClickCancel(View view) {
        Log.d(Constants.LOG_TAG, "AccountListActivity onClickCancel");

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.LOG_TAG, "AccountListActivity onActivityResult");

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Account account = data.getParcelableExtra(Constants.ACCOUNT_INTENT_FIELD);
                AccountsDataBase db = AccountsDataBase.getInstance(this);

                if (db.addIfNotExistAccount(account)) {
                    if (db.countAccount() == 1) {
                        ProcessesManager.start(this);

                        Toast.makeText(this, R.string.account_add_ok, Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(this, R.string.account_already_exists, Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    }
}
