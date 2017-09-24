package com.yun.opern.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Yun on 2017/9/9 0009.
 */

public class DBCore {
    private boolean ENCRYPTED = false;
    private static DaoSession daoSession;

    private DBCore(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getInstance(Context context) {
        if (daoSession == null) {
            synchronized (DBCore.class) {
                if (daoSession == null) {
                    new DBCore(context);
                }
            }
        }
        return daoSession;
    }
}
