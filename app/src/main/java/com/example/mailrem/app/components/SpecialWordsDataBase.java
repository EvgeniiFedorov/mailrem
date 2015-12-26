package com.example.mailrem.app.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.pojo.LoadSpecialWords;

import java.util.LinkedList;
import java.util.List;

public class SpecialWordsDataBase extends SQLiteOpenHelper implements Cursorable {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SpecialWordsBase";

    private static final String TABLE_WORDS = "SpecialWords";

    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";

    private static final int POSITION_WORD = 1;

    private static final String[] COLUMNS_WORDS = {COLUMN_ID, COLUMN_WORD};

    private static final String CREATE_TABLE_WORDS =
            "CREATE TABLE " + TABLE_WORDS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WORD + " TEXT UNIQUE " +
                    ")";

    private static final String DELETE_TABLE_WORDS =
            "DROP TABLE IF EXISTS " + TABLE_WORDS;

    private static final Object lock = new Object();
    private final Context context;

    private static SpecialWordsDataBase instance;
    private static SQLiteDatabase database;
    private static int countOpenConnection = 0;

    public static synchronized SpecialWordsDataBase getInstance(Context context) {
        if (instance == null) {
            instance = new SpecialWordsDataBase(context.getApplicationContext());
        }

        return instance;
    }

    private SpecialWordsDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase constructor");

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase onCreate");

        db.execSQL(CREATE_TABLE_WORDS);

        LoadSpecialWords reader = new LoadSpecialWords(context);
        List<String> specialWords = reader.getSpecialWords();

        ContentValues values = new ContentValues();

        for (String word : specialWords) {
            values.put(COLUMN_WORD, word);
            db.insert(TABLE_WORDS, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase onUpgrade");

        db.execSQL(DELETE_TABLE_WORDS);
        onCreate(db);
    }

    @Override
    public void open() {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase open");

        synchronized (lock) {
            if (countOpenConnection == 0) {
                database = getWritableDatabase();
            }

            countOpenConnection++;
        }
    }

    @Override
    public void close() {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase close");

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
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase getCursor");

        String query = "SELECT * FROM " + TABLE_WORDS;
        return database.rawQuery(query, null);
    }

    public boolean addIfNotExistWord(String word) {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase addIfNotExistWord");

        if (checkWord(word)) {
            addWord(word);
            return true;
        } else {
            Log.w(Constants.LOG_TAG, "SpecialWordsDataBase addIfNotExistWord: " +
                    "word already exists");
            return false;
        }
    }

    public synchronized void addWord(String word) {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase addWord");

        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);

        database.insert(TABLE_WORDS, null, values);
    }

    public boolean checkWord(String word) {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase checkWord");

        String selection = COLUMN_WORD + " = ? COLLATE NOCASE";
        String[] selectionArgs = {word};

        Cursor cursor = database.query(
                TABLE_WORDS,
                COLUMNS_WORDS,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        boolean check = cursor.getCount() == 0;

        cursor.close();
        return check;
    }

    public List<String> getAllWords() {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase getAllWords");

        List<String> words = new LinkedList<String>();
        String query = "SELECT * FROM " + TABLE_WORDS;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(POSITION_WORD);
                words.add(word.toLowerCase());
            } while (cursor.moveToNext());
        }

        cursor.close();
        return words;
    }

    public synchronized void deleteWord(long id) {
        Log.d(Constants.LOG_TAG, "SpecialWordsDataBase deleteWord");

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        database.delete(TABLE_WORDS, selection, selectionArgs);
    }
}
