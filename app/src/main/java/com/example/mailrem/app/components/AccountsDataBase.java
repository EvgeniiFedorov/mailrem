package com.example.mailrem.app.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.pojo.Account;

import java.util.LinkedList;
import java.util.List;

public class AccountsDataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AccountsBase";

    private static final String TABLE_ACCOUNTS = "Accounts";

    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_HOST = "host";
    public static final String COLUMN_PORT = "port";

    private static final int POSITION_LOGIN = 1;
    private static final int POSITION_PASSWORD = 2;
    private static final int POSITION_HOST = 3;
    private static final int POSITION_PORT = 4;

    private static final String[] COLUMNS_ACCOUNTS = {COLUMN_ID, COLUMN_LOGIN,
            COLUMN_PASSWORD, COLUMN_HOST, COLUMN_PORT};

    private static final String CREATE_TABLE_ACCOUNTS =
            "CREATE TABLE " + TABLE_ACCOUNTS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LOGIN + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_HOST + " TEXT, " +
                    COLUMN_PORT + " INTEGER " +
                    ")";

    private static final String DELETE_TABLE_ACCOUNTS =
            "DROP TABLE IF EXISTS " + TABLE_ACCOUNTS;

    private static final Object lock = new Object();

    private static AccountsDataBase instance;
    private static SQLiteDatabase database;
    private static int countOpenConnection = 0;

    public static synchronized AccountsDataBase getInstance(Context context) {
        if (instance == null) {
            instance = new AccountsDataBase(context.getApplicationContext());
        }

        return instance;
    }

    private AccountsDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(Constants.LOG_TAG, "AccountsDataBase constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase onCreate");

        db.execSQL(CREATE_TABLE_ACCOUNTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase onUpgrade");

        db.execSQL(DELETE_TABLE_ACCOUNTS);
        onCreate(db);
    }

    public void open() {
        Log.d(Constants.LOG_TAG, "AccountsDataBase open");

        synchronized (lock) {
            if (countOpenConnection == 0) {
                database = getWritableDatabase();
            }

            countOpenConnection++;
        }
    }

    public void close() {
        Log.d(Constants.LOG_TAG, "AccountsDataBase close");

        synchronized (lock) {
            countOpenConnection--;

            if (countOpenConnection == 0) {
                database.close();
                database = null;
            }
        }
    }

    public Cursor getCursor() {
        Log.d(Constants.LOG_TAG, "AccountsDataBase getCursor");

        String query = "SELECT * FROM " + TABLE_ACCOUNTS;
        return database.rawQuery(query, null);
    }


    public boolean addIfNotExistAccount(Account account) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase addIfNotExistAccount");

        if (getAccount(account.getLogin()) == null) {
            addAccount(account);
            return true;
        } else {
            Log.w(Constants.LOG_TAG, "AccountsDataBase addIfNotExistAccount: " +
                    "account already exists");
            return false;
        }
    }

    public synchronized void addAccount(Account account) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase addAccount");

        ContentValues values = accountToContentValues(account);

        database.insert(TABLE_ACCOUNTS, null, values);
    }

    public Account getAccount(String login) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase getAccount");

        String selection = COLUMN_LOGIN + " = ?";
        String[] selectionArgs = {login};

        Cursor cursor = database.query(
                TABLE_ACCOUNTS,
                COLUMNS_ACCOUNTS,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        Account account = null;

        if (cursor.moveToFirst()) {
            account = extractAccountFromCursor(cursor);
        }

        cursor.close();
        return account;
    }

    public List<Account> getAllAccounts() {
        Log.d(Constants.LOG_TAG, "AccountsDataBase getAllAccounts");

        List<Account> accounts = new LinkedList<Account>();
        String query = "SELECT * FROM " + TABLE_ACCOUNTS;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Account account = extractAccountFromCursor(cursor);
                accounts.add(account);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return accounts;
    }

    public synchronized void deleteAccount(long id) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase deleteAccount");

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        database.delete(TABLE_ACCOUNTS, selection, selectionArgs);
    }

    public synchronized void updateAccountByLogin(Account account) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase updateAccountByLogin");

        ContentValues values = accountToContentValues(account);

        String selection = COLUMN_LOGIN + " = ?";
        String[] selectionArgs = {account.getLogin()};

        database.update(TABLE_ACCOUNTS, values, selection, selectionArgs);
    }

    public int countAccount() {
        Log.d(Constants.LOG_TAG, "AccountsDataBase countAccount");

        String query = "SELECT COUNT(*) FROM " + TABLE_ACCOUNTS;

        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        return count;
    }

    public String getLoginById(long id) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase getLoginById, "
                + String.valueOf(id));

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(
                TABLE_ACCOUNTS,
                new String[] {COLUMN_LOGIN},
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );

        String login = null;
        if (cursor.moveToFirst()) {
            login = cursor.getString(0);
        }

        cursor.close();
        return login;
    }

    private Account extractAccountFromCursor(Cursor cursor) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase extractAccountFromCursor");

        return new Account(cursor.getString(POSITION_LOGIN),
                cursor.getString(POSITION_PASSWORD), cursor.getString(POSITION_HOST),
                cursor.getInt(POSITION_PORT));
    }

    private ContentValues accountToContentValues(Account account) {
        Log.d(Constants.LOG_TAG, "AccountsDataBase accountToContentValues");

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, account.getLogin());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_HOST, account.getHost());
        values.put(COLUMN_PORT, account.getPort());

        return values;
    }
}
