package com.example.mailrem.app.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mailrem.app.pojo.MessageWrap;
import com.example.mailrem.app.pojo.ScheduleManager;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MessagesDataBase extends SQLiteOpenHelper {

    private final static String LOG_TAG = "mailrem_log";

    private final static int START_STAGE = 0;
    private final static int COUNT_STAGE = 4;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MailBase";

    private static final String TABLE_MAILS = "Mails";
    private static final String COLUMN_UID = "uid";
    private static final String COLUMN_FROM = "from_field";
    private static final String COLUMN_TO = "to_field";
    private static final String COLUMN_DATE = "date_field";
    private static final String COLUMN_SUBJECT = "subject_field";
    private static final String COLUMN_BODY = "body_field";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_SCHEDULE = "schedule";
    private static final String COLUMN_END_STATUS = "end_status";

    private static final String[] COLUMNS_MAILS = {COLUMN_UID, COLUMN_FROM,
            COLUMN_TO, COLUMN_DATE, COLUMN_SUBJECT, COLUMN_BODY,
            COLUMN_STATUS, COLUMN_SCHEDULE, COLUMN_END_STATUS};

    private static final String CREATE_TABLE_MAILS =
            "CREATE TABLE " + TABLE_MAILS + "(" +
                    COLUMN_UID + " INTEGER PRIMARY KEY, " +
                    COLUMN_FROM + " TEXT, " +
                    COLUMN_TO + " TEXT, " +
                    COLUMN_DATE + " INTEGER, " +
                    COLUMN_SUBJECT + " TEXT, " +
                    COLUMN_BODY + " TEXT, " +
                    COLUMN_STATUS + " INTEGER, " +
                    COLUMN_SCHEDULE + " INTEGER, " +
                    COLUMN_END_STATUS + " INTEGER " +
                    ")";

    private static final String DELETE_TABLE_MAILS =
            "DROP TABLE IF EXISTS " + TABLE_MAILS;

    public MessagesDataBase(Context context) {
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
        Log.d(LOG_TAG, "add message if not exist db");
        if (getMessage(message.getUID()) == null) {
            addMessage(message);
        }
        else {
            Log.i(LOG_TAG, "message already exists");
        }
    }

    public void addMessage(MessageWrap message) {
        Log.d(LOG_TAG, "add message in db");

        ContentValues values = messageToContentValues(message);

        int status = START_STAGE;
        int scheduleTime = dateToInt(new Date()) + ScheduleManager.frequencyStage(status);
        int endStatusTime = dateToInt(new Date()) + ScheduleManager.durationStage(status);

        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_SCHEDULE, scheduleTime);
        values.put(COLUMN_END_STATUS, endStatusTime);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MAILS, null, values);
        db.close();
    }

    public MessageWrap getMessage(long uid) {
        Log.d(LOG_TAG, "get message from db");

        String selection = COLUMN_UID + " = ?";
        String[] selectionArgs = {String.valueOf(uid)};

        SQLiteDatabase db = getReadableDatabase();
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
            return null;
        }

        MessageWrap message = extractMessageFromCursor(cursor);

        cursor.close();
        db.close();
        return message;
    }

    public List<MessageWrap> getAllMessages() {
        Log.d(LOG_TAG, "get all message from db");
        List<MessageWrap> messages = new LinkedList<MessageWrap>();
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

    public List<MessageWrap> getAndUpdateMessagesForNotify() {
        Log.d(LOG_TAG, "get message from db for notify");

        List<MessageWrap> messages = new LinkedList<MessageWrap>();

        int currentDate = dateToInt(new Date());
        String selection = COLUMN_SCHEDULE + " <= ?";
        String[] selectionArgs = {String.valueOf(currentDate)};

        SQLiteDatabase db = getReadableDatabase();
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
            int status = cursor.getInt(6);
            int endStatusTime = cursor.getInt(8);

            if (status < COUNT_STAGE - 1) {
                messages.add(message);
            }

            updateMessage(message, status, endStatusTime);
        } while (cursor.moveToNext());

        cursor.close();
        db.close();
        return messages;
    }

    private void deleteMessage(long uid) {
        Log.d(LOG_TAG, "delete message from db");

        String selection = COLUMN_UID + " = ?";
        String[] selectionArgs = {String.valueOf(uid)};

        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABLE_MAILS, selection, selectionArgs);
        db.close();
    }

    private void updateMessage(MessageWrap message, int status, int endStatusTime) {

        Log.d(LOG_TAG, "update message from db");

        ContentValues values = messageToContentValues(message);

        int now = dateToInt(new Date());
        int newStatus = status;
        int newEndStatusTime = endStatusTime;
        int newScheduleTime;

        if (now > newEndStatusTime) {
            status++;
            newEndStatusTime = now + ScheduleManager.durationStage(status);
        }

        if (status < COUNT_STAGE - 1) {
            newScheduleTime = now + ScheduleManager.frequencyStage(status);
        } else if (status == COUNT_STAGE - 1) {
            newScheduleTime = newEndStatusTime;
        } else {
            deleteMessage(message.getUID());
            return;
        }

        values.put(COLUMN_STATUS, newStatus);
        values.put(COLUMN_SCHEDULE, newScheduleTime);
        values.put(COLUMN_END_STATUS, newEndStatusTime);

        String selection = COLUMN_UID + " = ?";
        String[] selectionArgs = {String.valueOf(message.getUID())};

        SQLiteDatabase db = getReadableDatabase();
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
