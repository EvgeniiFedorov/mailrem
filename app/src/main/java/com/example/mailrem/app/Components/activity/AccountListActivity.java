package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.AccountsDataBase;
import com.example.mailrem.app.pojo.Account;

public class AccountListActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    private static final String ACCOUNT = "Account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accounts_screen);
    }

    public void onClickAddAccount(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void onClickCancel(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Account account = data.getParcelableExtra(ACCOUNT);
                AccountsDataBase db = new AccountsDataBase(this);
                db.addIfNotExistAccount(account);
            }
        }
    }
}
