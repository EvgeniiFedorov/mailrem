package com.example.mailrem.app.components.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.mailrem.app.R;

public class AdvancedLoginActivity extends Activity{

    private static final String HOST = "host";
    private static final String PORT = "port";

    private EditText editHost;
    private EditText editPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_advanced_login_form);
        editHost = (EditText) findViewById(R.id.host);
        editPort = (EditText) findViewById(R.id.port);
    }

    public void onClickButtonNext(View v) {
        String host = editHost.getText().toString();
        String port = editPort.getText().toString();
        if (host.isEmpty()) {
            editHost.setError(getString(R.string.host_empty));
        } else if (port.isEmpty()) {
            editPort.setError(getString(R.string.port_empty));
        } else {
            try {
                int numPort = Integer.parseInt(port);

                Intent intent = new Intent();
                intent.putExtra(HOST, host);
                intent.putExtra(PORT, numPort);

                setResult(RESULT_OK, intent);
                finish();
            } catch (NumberFormatException e) {
                editPort.setError(getString(R.string.port_cast_fail));
            }
        }
    }

    public void onClickButtonPrev(View v) {
        finish();
    }
}