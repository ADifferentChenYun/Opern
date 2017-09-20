package com.yun.opern;

import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Yun on 2017/8/10 0010.
 */

public class Application extends android.app.Application{
    private static Context applictionContext;
    private static final String BUGLY_APP_ID = "b94c18144d";

    @Override
    public void onCreate() {
        super.onCreate();
        applictionContext = this;
        Bugly.init(getApplicationContext(), BUGLY_APP_ID, BuildConfig.DEBUG);
        Beta.autoInit = true;
        Beta.autoCheckUpgrade = true;
        Beta.enableHotfix = false;
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static Context getAppContext(){
        return applictionContext;
    }
}
