package com.yun.opern.utils;

/**
 * Created by 允儿 on 2015/10/27.
 * 自定义log，show控制是否打印log
 */
public class L {
    private static final boolean show = false;

    public static void v(String tag, String msg) {
        if (show) android.util.Log.v(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (show) android.util.Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (show) android.util.Log.d(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (show) android.util.Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (show) android.util.Log.e(tag, msg);
    }
}
