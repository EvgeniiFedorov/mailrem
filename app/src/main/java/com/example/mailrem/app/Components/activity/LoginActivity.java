package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.OnTaskCompleted;
import com.example.mailrem.app.pojo.Account;
import com.example.mailrem.app.components.CheckAccount;
import com.example.mailrem.app.pojo.TryExtendDefinitionAccount;

public class LoginActivity extends Activity implements OnTaskCompleted {

    private static final String LOG_TAG = "mailrem_log";

    private static final int REQUEST_CODE = 1;
    private static final String ACCOUNT = "Account";
    private static final String LOGIN = "Login";

    private static final String HOST = "host";
    private static final String PORT = "port";

    private EditText editLogin;
    private EditText editPassword;
    private TextView textInfo;
    private CheckBox checkBox;
    private ProgressBar progressBar;

    private CheckAccount checkAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_form);
        editLogin = (EditText) findViewById(R.id.login);
        editPassword = (EditText) findViewById(R.id.password);
        textInfo = (TextView) findViewById(R.id.text_info);
        checkBox = (CheckBox) findViewById(R.id.use_custom_server);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Intent inputIntent = getIntent();
        String login = inputIntent.getStringExtra(LOGIN);
        if (login != null) {
            editLogin.setText(login);
            editLogin.setEnabled(false);
        }
    }

    public void onClickButtonSignIn(View v) {
        String login = editLogin.getText().toString();
        String password = editPassword.getText().toString();
        if (login.isEmpty()) {
            editLogin.setError(getString(R.string.login_empty));
        } else if (password.isEmpty()) {
            editPassword.setError(getString(R.string.password_empty));
        } else {
            Account account = null;
            if (!checkBox.isChecked()) {
                account = TryExtendDefinitionAccount.getAccount(this, login, password);
            }

            if (account == null) {
                Intent intent = new Intent(this, AdvancedLoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                tryConnect(account);
            }
        }
    }

    public void onClickButtonCancel(View v) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String login = editLogin.getText().toString();
                String password = editPassword.getText().toString();
                String host = data.getStringExtra(HOST);
                int port = data.getIntExtra(PORT, 0);

                Account account = new Account(login, password, host, port);
                tryConnect(account);
            }
        }
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
            Intent intent = new Intent();
            intent.putExtra(ACCOUNT, resultAccount);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            textInfo.setBackgroundColor(Color.RED);
            textInfo.setText(getString(R.string.authorization_fail));
        }
    }
}
