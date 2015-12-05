package com.example.mailrem.app.components;

import android.accounts.Account;
import android.os.Parcel;

public class MailAccount extends Account {
    public static final String TYPE = "com.example.mailrem.app";

    public static final String TOKEN_FULL_ACCESS = "com.example.mailrem.app.TOKEN_FULL_ACCESS";

    public static final String KEY_PASSWORD = "com.example.mailrem.app.KEY_PASSWORD";

    public MailAccount(Parcel in) {
        super(in);
    }

    public MailAccount(String name) {
        super(name, TYPE);
    }
}
