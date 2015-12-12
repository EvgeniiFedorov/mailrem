package com.example.mailrem.app.components;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

class AccountsCursorLoader extends CursorLoader {

    AccountsDataBase dataBase;

    public AccountsCursorLoader(Context context, AccountsDataBase dataBase) {
        super(context);
        this.dataBase = dataBase;
    }

    @Override
    public Cursor loadInBackground() {
        return dataBase.getCursor();
    }
}
