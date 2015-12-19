package com.example.mailrem.app.components;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.util.Log;
import com.example.mailrem.app.Constants;

class MessagesCursorLoader extends CursorLoader {

    private final MessagesDataBase dataBase;

    public MessagesCursorLoader(Context context, MessagesDataBase dataBase) {
        super(context);
        Log.d(Constants.LOG_TAG, "MessagesCursorLoader constructor");

        this.dataBase = dataBase;
    }

    @Override
    public Cursor loadInBackground() {
        Log.d(Constants.LOG_TAG, "MessagesCursorLoader loadInBackground");

        return dataBase.getCursor();
    }
}
