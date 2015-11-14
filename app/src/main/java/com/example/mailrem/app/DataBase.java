package com.example.mailrem.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private final static String LOG_TAG = "mailrem_log";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MailBase";

    private static final String TABLE_MAILS = "Mails";
    private static final String COLUMN_UID = "'uid'";
    private static final String COLUMN_FROM = "'from'";
    private static final String COLUMN_TO = "'to'";
    private static final String COLUMN_DATE = "'date'";
    private static final String COLUMN_SUBJECT = "'subject'";
    private static final String COLUMN_BODY = "'body'";
    private static final String COLUMN_STATUS = "'status'";
    private static final String COLUMN_SCHEDULE = "'schedule'";

    private static final String[] COLUMNS_MAILS = {COLUMN_UID, COLUMN_FROM,
            COLUMN_TO, COLUMN_DATE, COLUMN_SUBJECT, COLUMN_BODY,
            COLUMN_STATUS, COLUMN_SCHEDULE};

    private static final String CREATE_TABLE_MAILS =
            "CREATE TABLE " + TABLE_MAILS + "(" +
                    COLUMN_UID + " INTEGER PRIMARY KEY, " +
                    COLUMN_FROM + " TEXT, " +
                    COLUMN_TO + " TEXT, " +
                    COLUMN_DATE + " INTEGER, " +
                    COLUMN_SUBJECT + " TEXT, " +
                    COLUMN_BODY + " TEXT, " +
                    COLUMN_STATUS + " INTEGER, " +
                    COLUMN_SCHEDULE + " INTEGER " +
                    ")";

    private static final String DELETE_TABLE_MAILS =
            "DROP TABLE IF EXISTS " + TABLE_MAILS;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "create db");
        db.execSQL(CREATE_TABLE_MAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "update db");
        db.execSQL(DELETE_TABLE_MAILS);
        onCreate(db);
    }

    public void addIfNotExistMessage(MessageWrap message) {
        if (getMessage(message.getUID()) == null) {
            addMessage(message);
        }
        else {
            Log.i(LOG_TAG, "message already exists");
        }
        //String query = "SELECT * FROM " + TABLE_MAILS +
        //        " WHERE " + COLUMN_UID + " = " + String.valueOf(message.getUID());
    //    SQLiteDatabase db = getReadableDatabase();

        //String selection = COLUMN_UID + "= ?";
    //    String selection = "'uid' = ?";
    //    String[] selectionArgs = {String.valueOf(message.getUID())};

    //    Cursor cursor = db.query(
    //            TABLE_MAILS,
    //            COLUMNS_MAILS,
    //            selection,
    //            selectionArgs,
    //            null,
    //            null,
    //            null
    //    );

        //Cursor cursor = db.rawQuery(query, null);
    //    int count = cursor.getCount();

    //    cursor.close();
    //    db.close();

    //    if (count == 0) {
    //        addMessage(message);
    //    }
    //    else {
    //        Log.i(LOG_TAG, "message already exists");
    //    }
    }

    public void addMessage(MessageWrap message) {
        Log.d(LOG_TAG, "add message in db");
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = messageToContentValues(message);

        int schedule = dateToInt(new Date()) + 3 * 60;
        values.put(COLUMN_STATUS, 1);
        values.put(COLUMN_SCHEDULE, schedule);

        db.insert(TABLE_MAILS, null, values);
        db.close();
    }

    public MessageWrap getMessage(long uid) {
        Log.d(LOG_TAG, "get message from db");
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_UID + " = ?";
        String[] selectionArgs = {String.valueOf(uid)};

        Cursor cursor = db.query(
                TABLE_MAILS,
                COLUMNS_MAILS,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        Log.w(LOG_TAG, "count ******** " + String.valueOf(cursor.getCount()));

        if (!cursor.moveToFirst()) {
            return null;
        }

        MessageWrap message = extractMessageFromCursor(cursor);

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

        if (!cursor.moveToFirst()) {
            return messages;
        }

        do {
            MessageWrap message = extractMessageFromCursor(cursor);
            messages.add(message);
        } while (cursor.moveToNext());

        cursor.close();
        db.close();
        return messages;
    }

    public List<MessageWrap> getMessagesForNotify() {
        Log.d(LOG_TAG, "get message from db for notify");

        List<MessageWrap> messages = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();

        int currentDate = dateToInt(new Date());
        String selection = COLUMN_DATE + " <= ?";
        String[] selectionArgs = {String.valueOf(currentDate)};

        Cursor cursor = db.query(
                TABLE_MAILS,
                COLUMNS_MAILS,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );


        if (!cursor.moveToFirst()) {
            return messages;
        }

        do {
            MessageWrap message = extractMessageFromCursor(cursor);
            messages.add(message);
        } while (cursor.moveToNext());

        cursor.close();
        db.close();
        return messages;
    }

    public void deleteMessage(long uid) {
        Log.d(LOG_TAG, "delete message from db");
        SQLiteDatabase db = getReadableDatabase();

        String selection = COLUMN_UID + " = ?";
        String[] selectionArgs = {String.valueOf(uid)};

        db.delete(TABLE_MAILS, selection, selectionArgs);
        db.close();
    }

    public void updateMessage(MessageWrap message) {
        Log.d(LOG_TAG, "update message from db");
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = messageToContentValues(message);

        String selection = COLUMN_UID + " = ?";
        String[] selectionArgs = {String.valueOf(message.getUID())};

        db.update(TABLE_MAILS, values, selection, selectionArgs);
        db.close();
    }

    private MessageWrap extractMessageFromCursor(Cursor cursor) {
        MessageWrap message = new MessageWrap();

        message.setUID(cursor.getLong(0));
        message.setFrom(cursor.getString(1));
        message.setTo(cursor.getString(2));
        message.setDate(intToDate(cursor.getInt(3)));
        message.setSubject(cursor.getString(4));
        message.setBody(cursor.getString(5));

        return message;
    }

    private ContentValues messageToContentValues(MessageWrap message) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, message.getUID());
        values.put(COLUMN_FROM, message.getFrom());
        values.put(COLUMN_TO, message.getTo());
        values.put(COLUMN_DATE, dateToInt(message.getDate()));
        values.put(COLUMN_SUBJECT, message.getSubject());
        values.put(COLUMN_BODY, message.getBody());

        return values;
    }

    private int dateToInt(Date date) {
        return (int) (date.getTime() / 1000);
    }

    private Date intToDate(int value) {
        return new Date((long) value * 1000);
    }
}
