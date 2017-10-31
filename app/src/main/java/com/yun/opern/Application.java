package com.yun.opern;

import android.content.Context;
import android.os.Build;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.bugly.beta.Beta;
import com.yun.opern.ui.activitys.MainActivity;

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
        Beta.autoInit = true;  //自动初始化
        Beta.initDelay = 500;  //延迟0.5s初始化，避免影响启动速度
        Beta.autoCheckUpgrade = true;  //自动检查更新开关
        Beta.enableHotfix = false;  //关闭热更新能力
        Beta.canShowUpgradeActs.add(MainActivity.class);  //更新提示只能在首页显示,不限制手动调用检测更新
        BuglyStrategy buglyStrategy = new BuglyStrategy();
        buglyStrategy.setAppChannel(BuildConfig.FLAVOR);  //设置渠道
        if (!BuildConfig.DEBUG) {
            Bugly.init(getApplicationContext(), BUGLY_APP_ID, BuildConfig.DEBUG, buglyStrategy);
        }
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static Context getAppContext(){
        return applictionContext;
    }
}
