package com.example.mailrem.app.components;

import android.accounts.*;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.example.mailrem.app.R;

import java.io.IOException;

public class AccountListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.accounts_screen);
    }

    public void onClickAddAccount(View v) {
        AccountManager accountManager = AccountManager.get(this);
        accountManager.addAccount(MailAccount.TYPE, MailAccount.TOKEN_FULL_ACCESS, null, null, this,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            future.getResult();
                        } catch (OperationCanceledException e) {
                            AccountListActivity.this.finish();
                        } catch (IOException e) {
                            AccountListActivity.this.finish();
                        } catch (AuthenticatorException e) {
                            AccountListActivity.this.finish();
                        }
                    }
                }, null
        );
    }
}
