package com.example.mailrem.app.components;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.util.Log;
import com.example.mailrem.app.Constants;

class AccountsCursorLoader extends CursorLoader {

    private final AccountsDataBase dataBase;

    public AccountsCursorLoader(Context context, AccountsDataBase dataBase) {
        super(context);
        Log.d(Constants.LOG_TAG, "AccountsCursorLoader constructor");

        this.dataBase = dataBase;
    }

    @Override
    public Cursor loadInBackground() {
        Log.d(Constants.LOG_TAG, "AccountsCursorLoader loadInBackground");

        return dataBase.getCursor();
    }
}
