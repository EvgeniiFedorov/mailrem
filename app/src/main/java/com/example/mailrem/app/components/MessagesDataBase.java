package com.example.mailrem.app.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mailrem.app.Constants;
import com.example.mailrem.app.pojo.MessageWrap;
import com.example.mailrem.app.pojo.ScheduleManager;

import java.util.*;

public class MessagesDataBase extends SQLiteOpenHelper implements Cursorable{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MailBase";

    private static final String TABLE_MAILS = "Mails";

    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_FROM_NAME = "from_name_field";
    public static final String COLUMN_FROM_ADDRESS = "from_address_field";
    public static final String COLUMN_TO = "to_field";
    public static final String COLUMN_DATE = "date_field";
    public static final String COLUMN_SUBJECT = "subject_field";
    public static final String COLUMN_BODY = "body_field";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_SCHEDULE = "schedule";
    private static final String COLUMN_BEGIN_STATUS = "begin_status_time";

    private static final int POSITION_ID = 0;
    private static final int POSITION_FROM_NAME = 1;
    private static final int POSITION_FROM_ADDRESS = 2;
    private static final int POSITION_TO = 3;
    private static final int POSITION_DATE = 4;
    private static final int POSITION_SUBJECT = 5;
    private static final int POSITION_BODY = 6;
    private static final int POSITION_BEGIN_STATUS = 9;

    private static final String[] COLUMNS_MAILS = {COLUMN_ID, COLUMN_FROM_NAME,
            COLUMN_FROM_ADDRESS, COLUMN_TO, COLUMN_DATE, COLUMN_SUBJECT,
            COLUMN_BODY, COLUMN_STATUS, COLUMN_SCHEDULE, COLUMN_BEGIN_STATUS};

    private static final String CREATE_TABLE_MAILS =
            "CREATE TABLE " + TABLE_MAILS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FROM_NAME + " TEXT, " +
                    COLUMN_FROM_ADDRESS + " TEXT, " +
                    COLUMN_TO + " TEXT, " +
                    COLUMN_DATE + " INTEGER, " +
                    COLUMN_SUBJECT + " TEXT, " +
                    COLUMN_BODY + " TEXT, " +
                    COLUMN_STATUS + " INTEGER, " +
                    COLUMN_SCHEDULE + " INTEGER, " +
                    COLUMN_BEGIN_STATUS + " INTEGER " +
                    ")";

    private static final String DELETE_TABLE_MAILS =
            "DROP TABLE IF EXISTS " + TABLE_MAILS;

    private static final int DEFAULT_NEXT_NOTIFY_TIME = 24 * 60 * 60 * 1000;

    private static final Object lock = new Object();

    private static MessagesDataBase instance;
    private final Context context;
    private SQLiteDatabase database;
    private static int countOpenConnection = 0;


    public static synchronized MessagesDataBase getInstance(Context context) {
        if (instance == null) {
            instance = new MessagesDataBase(context.getApplicationContext());
        }

        return instance;
    }

    private MessagesDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(Constants.LOG_TAG, "MessagesDataBase constructor");

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase onCreate");

        db.execSQL(CREATE_TABLE_MAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase onUpgrade");

        db.execSQL(DELETE_TABLE_MAILS);
        onCreate(db);
    }

    @Override
    public void open() {
        Log.d(Constants.LOG_TAG, "MessagesDataBase open");

        synchronized (lock) {
            if (countOpenConnection == 0) {
                database = getWritableDatabase();
            }

            countOpenConnection++;
        }
    }

    @Override
    public void close() {
        Log.d(Constants.LOG_TAG, "MessagesDataBase close");

        synchronized (lock) {
            countOpenConnection--;

            if (countOpenConnection == 0) {
                database.close();
                database = null;
            }
        }
    }

    @Override
    public Cursor getCursor() {
        Log.d(Constants.LOG_TAG, "MessagesDataBase getCursor");

        String query = "SELECT * FROM " + TABLE_MAILS;
        return database.rawQuery(query, null);
    }

    public boolean addIfNotExistMessage(MessageWrap message) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase addIfNotExistMessage");

        if (checkMessage(message)) {
            addMessage(message);
            return true;
        } else {
            Log.w(Constants.LOG_TAG, "MessagesDataBase addIfNotExistMessage: " +
                    "message already exists");
            return false;
        }
    }

    public synchronized void addMessage(MessageWrap message) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase addMessage");

        ContentValues values = messageToContentValues(message);

        int status = Constants.START_STAGE;
        int scheduleTime = dateToInt(new Date());
        int beginStatusTime = dateToInt(new Date());

        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_SCHEDULE, scheduleTime);
        values.put(COLUMN_BEGIN_STATUS, beginStatusTime);

        database.insert(TABLE_MAILS, null, values);
    }

    public boolean checkMessage(MessageWrap message) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase checkMessage");

        String selection = COLUMN_FROM_ADDRESS + " = ? AND "
                + COLUMN_DATE + " = ? AND "
                + COLUMN_SUBJECT + " = ?";
        String[] selectionArgs = {message.getFromAddress(),
                String.valueOf(dateToInt(message.getDate())),
                message.getSubject()};

        Cursor cursor = database.query(
                TABLE_MAILS,
                COLUMNS_MAILS,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        boolean exist = cursor.getCount() == 0;

        cursor.close();
        return exist;
    }

    public MessageWrap getMessage(long id) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase getMessage");

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(
                TABLE_MAILS,
                COLUMNS_MAILS,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        MessageWrap message = null;
        if (cursor.moveToFirst()) {
            message = extractMessageFromCursor(cursor);
        }

        cursor.close();
        return message;
    }

    public Map<MessageWrap, Integer> getAndUpdateMessagesForNotify() {
        Log.d(Constants.LOG_TAG, "MessagesDataBase getAndUpdateMessagesForNotify");

        Map<MessageWrap, Integer> messages = new HashMap<MessageWrap, Integer>();
        ScheduleManager scheduleManager = new ScheduleManager(context);

        for (int status = Constants.START_STAGE;
             status < Constants.COUNT_STAGE; ++status) {

            int timeLimit = dateToInt(new Date()) - scheduleManager.frequencyStage(status);

            String selection = COLUMN_SCHEDULE + " <= ? AND " + COLUMN_STATUS + " = ?";
            String[] selectionArgs = {String.valueOf(timeLimit), String.valueOf(status)};

            Cursor cursor = database.query(
                    TABLE_MAILS,
                    COLUMNS_MAILS,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    MessageWrap message = extractMessageFromCursor(cursor);
                    long idMessage = cursor.getLong(POSITION_ID);
                    int beginStatusTime = cursor.getInt(POSITION_BEGIN_STATUS);

                    if (status != Constants.COUNT_STAGE - 1) {
                        messages.put(message, status);
                    }

                    updateMessage(message, idMessage, status, beginStatusTime);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return messages;
    }

    public synchronized void deleteMessage(long id) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase deleteMessage");

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        database.delete(TABLE_MAILS, selection, selectionArgs);
    }

    private synchronized void updateMessage(MessageWrap message, long idMessage,
                                            int status, int beginStatusTime) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase updateMessage");

        ContentValues values = messageToContentValues(message);
        ScheduleManager scheduleManager = new ScheduleManager(context);

        int now = dateToInt(new Date());

        int newStatus = status;
        int newBeginStatusTime = beginStatusTime;

        if (now > newBeginStatusTime + scheduleManager.durationStage(status)) {
            newStatus++;
            newBeginStatusTime = now;
        }

        if (status == Constants.COUNT_STAGE) {
            deleteMessage(idMessage);
            return;
        }

        values.put(COLUMN_STATUS, newStatus);
        values.put(COLUMN_SCHEDULE, now);
        values.put(COLUMN_BEGIN_STATUS, newBeginStatusTime);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(idMessage)};

        database.update(TABLE_MAILS, values, selection, selectionArgs);
    }

    public int nextNotifyTime() {
        Log.d(Constants.LOG_TAG, "MessagesDataBase nextNotifyTime");

        ScheduleManager scheduleManager = new ScheduleManager(context);
        int nextTime = DEFAULT_NEXT_NOTIFY_TIME;

        for (int status = Constants.START_STAGE;
             status < Constants.COUNT_STAGE; ++status) {

            String[] column = {"MIN( " + COLUMN_SCHEDULE + ")"};

            String selection = COLUMN_STATUS + " = ?";
            String[] selectionArgs = {String.valueOf(status)};

            Cursor cursor = database.query(
                    TABLE_MAILS,
                    column,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                if (nextTime > cursor.getInt(0) + scheduleManager.frequencyStage(status)) {
                    nextTime = cursor.getInt(0) + scheduleManager.frequencyStage(status);
                }
            }

            cursor.close();
        }

        return nextTime;
    }

    private MessageWrap extractMessageFromCursor(Cursor cursor) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase extractMessageFromCursor");

        MessageWrap message = new MessageWrap();

        message.setFromName(cursor.getString(POSITION_FROM_NAME));
        message.setFromAddress(cursor.getString(POSITION_FROM_ADDRESS));
        message.setTo(cursor.getString(POSITION_TO));
        message.setDate(intToDate(cursor.getInt(POSITION_DATE)));
        message.setSubject(cursor.getString(POSITION_SUBJECT));
        message.setBody(cursor.getString(POSITION_BODY));

        return message;
    }

    private ContentValues messageToContentValues(MessageWrap message) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase messageToContentValues");

        ContentValues values = new ContentValues();
        values.put(COLUMN_FROM_NAME, message.getFromName());
        values.put(COLUMN_FROM_ADDRESS, message.getFromAddress());
        values.put(COLUMN_TO, message.getTo());
        values.put(COLUMN_DATE, dateToInt(message.getDate()));
        values.put(COLUMN_SUBJECT, message.getSubject());
        values.put(COLUMN_BODY, message.getBody());

        return values;
    }

    private int dateToInt(Date date) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase dateToInt");

        return (int) (date.getTime() / 1000);
    }

    private Date intToDate(int value) {
        Log.d(Constants.LOG_TAG, "MessagesDataBase intToDate");

        return new Date((long) value * 1000);
    }
}
