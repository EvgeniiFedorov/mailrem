package com.example.mailrem.app.components;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.mailrem.app.R;

public class LoginForm extends Fragment implements LoaderManager.LoaderCallbacks<String>, View.OnClickListener {

    private EditText editLogin;
    private EditText editPassword;
    private Button buttonCancel;
    private Button buttonSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_form, container, false);
        editLogin = (EditText) view.findViewById(R.id.login);
        editPassword = (EditText) view.findViewById(R.id.password);
        buttonSignIn = (Button) view.findViewById(R.id.button_sign_in);
        buttonCancel = (Button) view.findViewById(R.id.button_cancel);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        buttonCancel.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        buttonCancel.setOnClickListener(null);
        buttonSignIn.setOnClickListener(null);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_in:
                if (TextUtils.isEmpty(editLogin.getText())) {
                    editLogin.setError(getString(R.string.login_empty));
                } else if (TextUtils.isEmpty(editPassword.getText())) {
                    editPassword.setError(getString(R.string.password_empty));
                } else {
                    ((LoginActivity) getActivity()).onTokenReceived(
                            new MailAccount(editLogin.getText().toString()),
                            editPassword.getText().toString(), "");
//                getLoaderManager().restartLoader(R.id.auth_token_loader, null, this);
                }
                break;
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String token) {
        ((LoginActivity) getActivity()).onTokenReceived(
                new MailAccount(editLogin.getText().toString()),
                editPassword.getText().toString(), token
        );
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}