package com.yun.opern.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Yun on 2017/8/12 0012.
 */

public class CacheFileUtil {
    public static final String cacheFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/opern";

    public static void init(){
        File file = new File(cacheFilePath);
        if(!file.exists()){
            boolean ok = file.mkdir();
            if(!ok){
                T.showShort("创建缓存目录失败");
            }
        }
    }

    public static boolean clear(){
        File file = new File(cacheFilePath);
        return file.delete();
    }

    public static long size(){
        File file = new File(cacheFilePath);
        return file.getTotalSpace();
    }
}
