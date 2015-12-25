package com.example.mailrem.app.components;

import android.database.Cursor;

public interface Cursorable {

    void open();
    void close();

    Cursor getCursor();
}
