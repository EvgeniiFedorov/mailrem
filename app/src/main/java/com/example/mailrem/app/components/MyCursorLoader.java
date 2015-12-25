package com.example.mailrem.app.components;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.util.Log;
import com.example.mailrem.app.Constants;

class MyCursorLoader<TDataBase extends Cursorable> extends CursorLoader {

    private final TDataBase dataBase;

    public MyCursorLoader(Context context, TDataBase dataBase) {
        super(context);
        Log.d(Constants.LOG_TAG, "MyCursorLoader constructor");

        this.dataBase = dataBase;
    }

    @Override
    public Cursor loadInBackground() {
        Log.d(Constants.LOG_TAG, "MyCursorLoader loadInBackground");

        return dataBase.getCursor();
    }
}
