package com.example.mailrem.app.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.mailrem.app.Constants;
import com.example.mailrem.app.components.AccountsDataBase;
import com.example.mailrem.app.components.service.NotifyFromDB;
import com.example.mailrem.app.components.service.UpdateData;

public class ProcessesManager {

    private static final String UPDATE_SWITCH = "update_switch";
    private static final String NOTIFY_SWITCH = "notify_switch";
    private static final String UPDATE_INTERVAL = "update_interval";
    private static final String WIFI_ONLY = "use_wifi";
    private static final String ROUMING_USE = "use_roaming";

    public static void start(Context context) {
        Log.d(Constants.LOG_TAG, "ProcessesManager start");

        startUpdate(context);
        startNotify(context);
    }

    public static void restart(Context context) {
        Log.d(Constants.LOG_TAG, "ProcessesManager restart");

        stop();
        start(context);
    }

    public static void stop() {
        Log.d(Constants.LOG_TAG, "ProcessesManager stop");

        stopUpdate();
        stopNotify();
    }

    public static void startUpdate(Context context) {
        Log.d(Constants.LOG_TAG, "ProcessesManager startUpdate");

        if (checkStateUpdate(context)) {
            SharedPreferences sharedPreferences
                    = PreferenceManager.getDefaultSharedPreferences(context);

            int intervalUpdate = sharedPreferences.getInt(UPDATE_INTERVAL, 0);

            UpdateData.startUpdateProcess(context, intervalUpdate * 1000);
        }
    }


    public static void startNotify(Context context) {
        Log.d(Constants.LOG_TAG, "ProcessesManager startNotify");

        if (checkStateNotify(context)) {
            NotifyFromDB.startNotifyProcess(context);
        }
    }

    public static void stopUpdate() {
        Log.d(Constants.LOG_TAG, "ProcessesManager stopUpdate");

        UpdateData.stopUpdate();
    }

    public static void stopNotify() {
        Log.d(Constants.LOG_TAG, "ProcessesManager stopNotify");

        NotifyFromDB.stopNotify();
    }

    private static boolean checkStateUpdate(Context context) {
        Log.d(Constants.LOG_TAG, "ProcessesManager checkStateUpdate");

        AccountsDataBase db = AccountsDataBase.getInstance(context);

        if (db.countAccount() == 0) {
            Log.i(Constants.LOG_TAG, "ProcessesManager checkStateUpdate: " +
                    "start update cancel - no account");
            return false;
        }

        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        if (!sharedPreferences.getBoolean(UPDATE_SWITCH, false)) {
            Log.i(Constants.LOG_TAG, "ProcessesManager checkStateUpdate: " +
                    "start update cancel - update switch off");
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnected();

        if (!isConnected) {
            Log.i(Constants.LOG_TAG, "ProcessesManager checkStateUpdate: " +
                    "start update cancel - no connection");
            return false;
        }

        boolean wifiOnly = sharedPreferences.getBoolean(WIFI_ONLY, false);

        if (wifiOnly) {
            boolean isWifi = activeNetwork.getType()
                    == ConnectivityManager.TYPE_WIFI;

            if (!isWifi) {
                Log.i(Constants.LOG_TAG, "ProcessesManager checkStateUpdate: " +
                        "start update cancel - no wifi connection");
                return false;
            }
        }

        boolean NoRoamingUseOnly = sharedPreferences.getBoolean(ROUMING_USE, true);

        if (NoRoamingUseOnly) {
            boolean isRoaming = activeNetwork.isRoaming();

            if (isRoaming) {
                Log.i(Constants.LOG_TAG, "ProcessesManager checkStateUpdate: " +
                        "start update cancel: roaming");
                return false;
            }
        }

        return true;
    }

    private static boolean checkStateNotify(Context context) {
        Log.d(Constants.LOG_TAG, "ProcessesManager checkStateNotify");

        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(NOTIFY_SWITCH, false);
    }
}
