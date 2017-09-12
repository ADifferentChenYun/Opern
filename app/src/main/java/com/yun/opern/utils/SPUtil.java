package com.yun.opern.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yun.opern.Application;

/**
 * Created by Yun on 2017/9/12 0012.
 */

public class SPUtil {
    public static final String Update_No_Longer_Reminded_Key = "Update_No_Longer_Reminded_Key";

    public static int getInt(String key, int defValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.getAppContext());
        return sharedPreferences.getInt(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.getAppContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putInt(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.getAppContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
