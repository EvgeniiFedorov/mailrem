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
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.R;
import com.example.mailrem.app.components.OnTaskCompleted;
import com.example.mailrem.app.pojo.Account;
import com.example.mailrem.app.components.CheckAccount;
import com.example.mailrem.app.pojo.TryExtendDefinitionAccount;

public class LoginActivity extends Activity implements OnTaskCompleted {

    private static final int REQUEST_CODE = 1;

    private EditText editLogin;
    private EditText editPassword;
    private TextView textInfo;
    private CheckBox checkBox;
    private ProgressBar progressBar;

    private CheckAccount checkAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "LoginActivity onCreate");

        setContentView(R.layout.activity_login_form);
        editLogin = (EditText) findViewById(R.id.login);
        editPassword = (EditText) findViewById(R.id.password);
        textInfo = (TextView) findViewById(R.id.text_info);
        checkBox = (CheckBox) findViewById(R.id.use_custom_server);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Intent inputIntent = getIntent();
        String login = inputIntent.getStringExtra(Constants.LOGIN_INTENT_FIELD);

        if (login != null) {
            editLogin.setText(login);
            editLogin.setEnabled(false);
        }
    }

    public void onClickButtonSignIn(View v) {
        Log.d(Constants.LOG_TAG, "LoginActivity onClickButtonSignIn");

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
        Log.d(Constants.LOG_TAG, "LoginActivity onClickButtonCancel");

        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.LOG_TAG, "LoginActivity onActivityResult");

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String login = editLogin.getText().toString();
                String password = editPassword.getText().toString();
                String host = data.getStringExtra(Constants.HOST_INTENT_FIELD);
                int port = data.getIntExtra(Constants.PORT_INTENT_FIELD, 0);

                Account account = new Account(login, password, host, port);
                tryConnect(account);
            }
        }
    }

    public void tryConnect(Account account) {
        Log.d(Constants.LOG_TAG, "LoginActivity tryConnect");

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
        Log.d(Constants.LOG_TAG, "LoginActivity onTaskCompleted");

        progressBar.setVisibility(View.INVISIBLE);
        textInfo.setText(getString(R.string.default_info_text));

        Account resultAccount = null;
        try {
            resultAccount = checkAccount.get();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "LoginActivity onTaskCompleted: " +
                    "exception in asyncTask.get - " + e.getMessage());
        }

        if (resultAccount != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.ACCOUNT_INTENT_FIELD, resultAccount);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            textInfo.setBackgroundColor(Color.RED);
            textInfo.setText(getString(R.string.authorization_fail));
        }
    }
}
