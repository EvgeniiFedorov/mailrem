package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.mailrem.app.R;

public class AccountListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accounts_screen);
    }

    public void onClickAddAccount(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onClickDeleteAccount(View v) {
        //start delete activity
    }
}
