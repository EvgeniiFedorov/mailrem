package com.example.mailrem.app.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.mailrem.app.pojo.Account;

import java.util.LinkedList;
import java.util.List;

public class AccountsDataBase extends SQLiteOpenHelper {

    private static final String LOG_TAG = "mailrem_log";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AccountsBase";

    private static final String TABLE_ACCOUNTS = "Accounts";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_HOST = "host";
    private static final String COLUMN_PORT = "port";

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

    private SQLiteDatabase databaseForCursor;

    public AccountsDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "create db");
        db.execSQL(CREATE_TABLE_ACCOUNTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "update db");
        db.execSQL(DELETE_TABLE_ACCOUNTS);
        onCreate(db);
    }

    public void open() {
        databaseForCursor = getWritableDatabase();
    }

    public void close() {
        databaseForCursor.close();
    }

    public Cursor getCursor() {
        Log.d(LOG_TAG, "get cursor accounts from db");
        String query = "SELECT * FROM " + TABLE_ACCOUNTS;

        return databaseForCursor.rawQuery(query, null);
    }

    public void deleteAccount(long id) {
        Log.d(LOG_TAG, "delete account from db");

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        databaseForCursor.delete(TABLE_ACCOUNTS, selection, selectionArgs);
    }

    public String getLoginById(long id) {

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = databaseForCursor.query(
                TABLE_ACCOUNTS,
                new String[] {COLUMN_LOGIN},
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

        String login = cursor.getString(0);

        cursor.close();
        return login;
    }

    public void addIfNotExistAccount(Account account) {
        Log.d(LOG_TAG, "add account if not exist db");
        if (getAccount(account.getLogin()) == null) {
            addAccount(account);
        } else {
            Log.i(LOG_TAG, "account already exists");
        }
    }

    public void addAccount(Account account) {
        Log.d(LOG_TAG, "add account in db");

        ContentValues values = accountToContentValues(account);

        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_ACCOUNTS, null, values);
        db.close();
    }

    public Account getAccount(String login) {
        Log.d(LOG_TAG, "get account from db");

        String selection = COLUMN_LOGIN + " = ?";
        String[] selectionArgs = {login};

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_ACCOUNTS,
                COLUMNS_ACCOUNTS,
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

        Account account = extractAccountFromCursor(cursor);

        cursor.close();
        db.close();
        return account;
    }

    public void updateAccountByLogin(Account account) {
        Log.d(LOG_TAG, "update account by login from db");

        ContentValues values = accountToContentValues(account);

        String selection = COLUMN_LOGIN + " = ?";
        String[] selectionArgs = {account.getLogin()};

        SQLiteDatabase db = getWritableDatabase();

        db.update(TABLE_ACCOUNTS, values, selection, selectionArgs);
        db.close();
    }

    public List<Account> getAllAccounts() {
        Log.d(LOG_TAG, "get all accounts from db");
        List<Account> accounts = new LinkedList<Account>();
        String query = "SELECT * FROM " + TABLE_ACCOUNTS;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            return accounts;
        }

        do {
            Account account = extractAccountFromCursor(cursor);
            accounts.add(account);
        } while (cursor.moveToNext());

        cursor.close();
        db.close();
        return accounts;
    }

    private Account extractAccountFromCursor(Cursor cursor) {

        return new Account(cursor.getString(1),
                cursor.getString(2), cursor.getString(3),
                cursor.getInt(4));
    }

    private ContentValues accountToContentValues(Account account) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, account.getLogin());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_HOST, account.getHost());
        values.put(COLUMN_PORT, account.getPort());

        return values;
    }
}
