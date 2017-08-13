package com.yun.opern;

import android.content.Context;

/**
 * Created by Yun on 2017/8/10 0010.
 */

public class Application extends android.app.Application{
    private static Context applictionContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applictionContext = this;
    }

    public static Context getAppContext(){
        return applictionContext;
    }
}
