package com.example.mailrem.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private final static String LOG_TAG = "log_debug";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MailBase";
    private static final String TABLE_MAILS = "Mails";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FROM = "form";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_SUBJECT = "subject";

    private static final String[] COLUMNS = {COLUMN_FROM, COLUMN_SUBJECT};

    public DataBase(Context contxt) {
        super(contxt, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "create db");
        String createTable = "CREATE TABLE " + TABLE_MAILS + "(" +
                //COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FROM + " TEXT," +
                //COLUMN_DATE + " " +
                COLUMN_SUBJECT + " TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "update db");
        String deleteTable = "DROP TABLE IF EXISTS " + TABLE_MAILS;
        db.execSQL(deleteTable);
        onCreate(db);
    }

    public void addMessage(MessageWrap message) {
        Log.d(LOG_TAG, "add message in db");
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FROM, message.getFrom());
        values.put(COLUMN_SUBJECT, message.getSubject());

        db.insert(TABLE_MAILS, null, values);
        db.close();
    }

    public MessageWrap getMessage(int id) {
        Log.d(LOG_TAG, "get message from db");
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_MAILS, COLUMNS,
                "_id = ?", new String[] {Integer.toString(id)},
                null, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();

        MessageWrap message = new MessageWrap();
        message.setFrom(cursor.getString(0));
        message.setSubject(cursor.getString(1));

        cursor.close();
        db.close();
        return message;
    }

    public List<MessageWrap> getAllMessages() {
        Log.d(LOG_TAG, "get all message from db");
        List<MessageWrap> messages = new LinkedList<>();
        String query = "SELECT * FROM " + TABLE_MAILS;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MessageWrap message = new MessageWrap();
                message.setFrom(cursor.getString(0));
                message.setSubject(cursor.getString(1));
                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messages;
    }
}
