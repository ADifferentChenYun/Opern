package com.yun.opern.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yun on 2017/9/9 0009.
 */

public class DateBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "opern.db";
    private static final int DATABASE_VERSION = 1;

    public static class Tables {
        public static final String TBL_FEEDBACK_MESSAGE_INFO = "tbl_feedbackmessageinfo";
    }


    public DateBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tbl_feedbackmessageinfo ( message varch(255), datatime datatime)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
