package com.example.mailrem.app.components;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.Bundle;
import com.example.mailrem.app.R;

public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String EXTRA_TOKEN_TYPE = "com.example.mailrem.app.EXTRA_TOKEN_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticator);
    }

    public void onTokenReceived(Account account, String password, String token) {
        AccountManager accountManager = AccountManager.get(this);
        Bundle result = new Bundle();

        if (accountManager.addAccountExplicitly(account, password, new Bundle())) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            accountManager.setAuthToken(account, account.type, token);
        } else {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_already_exists));
        }

        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
        finish();
    }
}
