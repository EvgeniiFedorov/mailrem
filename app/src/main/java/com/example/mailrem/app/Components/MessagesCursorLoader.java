package com.example.mailrem.app.components;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

class MessagesCursorLoader extends CursorLoader {

    private final MessagesDataBase dataBase;

    public MessagesCursorLoader(Context context, MessagesDataBase dataBase) {
        super(context);
        this.dataBase = dataBase;
    }

    @Override
    public Cursor loadInBackground() {
        return dataBase.getCursor();
    }
}
