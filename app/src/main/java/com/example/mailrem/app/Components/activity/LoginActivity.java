package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.AccountsDataBase;
import com.example.mailrem.app.components.OnTaskCompleted;
import com.example.mailrem.app.pojo.Account;
import com.example.mailrem.app.components.CheckAccount;
import com.example.mailrem.app.pojo.TryExtendDefinitionAccount;

public class LoginActivity extends Activity implements OnTaskCompleted{

    private final static String LOG_TAG = "mailrem_log";

    private static final int REQUEST_CODE = 1;
    private static final String HOST = "host";
    private static final String PORT = "port";

    private EditText editLogin;
    private EditText editPassword;
    private TextView textInfo;
    private ProgressBar progressBar;

    CheckAccount checkAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_form);
        editLogin = (EditText) findViewById(R.id.login);
        editPassword = (EditText) findViewById(R.id.password);
        textInfo = (TextView) findViewById(R.id.text_info);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void onClickButtonSignIn(View v) {
        String login = editLogin.getText().toString();
        String password = editPassword.getText().toString();
        if (login.isEmpty()) {
            editLogin.setError(getString(R.string.login_empty));
        } else if (password.isEmpty()) {
            editPassword.setError(getString(R.string.password_empty));
        } else {
            Account account = TryExtendDefinitionAccount.getAccount(this, login, password);
            if (account == null) {
                Intent intent = new Intent(this, AdvancedLoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                tryConnect(account);
            }
        }
    }

    public void onClickButtonCancel(View v) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String login = editLogin.getText().toString();
        String password = editPassword.getText().toString();
        String host = data.getStringExtra(HOST);
        int port = data.getIntExtra(PORT, 0);

        Account account = new Account(login, password, host, port);
        tryConnect(account);
    }

    public void tryConnect(Account account) {
        checkAccount = new CheckAccount();
        checkAccount.setOnTaskCompletedListener(this);
        checkAccount.execute(account);
        progressBar.setVisibility(View.VISIBLE);
        textInfo.setBackgroundColor(Color.GRAY);
        textInfo.setText(account.getHost() + ":" + account.getPort() + "\n" +
                getString(R.string.connection_progress));
    }

    @Override
    public void onTaskCompleted() {
        progressBar.setVisibility(View.INVISIBLE);
        textInfo.setText(getString(R.string.default_info_text));

        Account resultAccount = null;
        try {
            resultAccount = checkAccount.get();
        } catch (Exception e) {
            Log.d(LOG_TAG, "exception in asyncTask.get: "
                    + e.getMessage());
        }

        if (resultAccount != null) {
            AccountsDataBase db = new AccountsDataBase(this);
            db.addIfNotExistAccount(resultAccount);
            finish();
        } else {
            textInfo.setBackgroundColor(Color.RED);
            textInfo.setText(getString(R.string.authorization_fail));
        }
    }
}
