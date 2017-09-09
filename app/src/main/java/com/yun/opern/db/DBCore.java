package com.yun.opern.db;

import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import com.yun.opern.Application;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by Yun on 2017/9/9 0009.
 */

public class DBCore {
    private static BriteDatabase briteDatabase;

    private DBCore() {
        DateBaseHelper dateBaseHelper = new DateBaseHelper(Application.getAppContext());
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        briteDatabase = sqlBrite.wrapDatabaseHelper(dateBaseHelper, Schedulers.io());
    }

    public static BriteDatabase getInstance() {
        if (briteDatabase == null) {
            synchronized (DBCore.class) {
                if (briteDatabase == null) {
                    new DBCore();
                }
            }
        }
        return briteDatabase;
    }
}
