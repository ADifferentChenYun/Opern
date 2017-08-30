package com.yun.opern.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.ref.SoftReference;

/**
 * Created by Yun on 2017/8/29 0029.
 */

public class WeiBoUserInfoKeeper {
    private static final String WEIBO_USER_INFO = "weibo_user_info";
    private static SoftReference<WeiBoUserInfo> weiBoUserInfoSoftReference;

    public static void write(Context context, WeiBoUserInfo weiBoUserInfo) {
        setObject(context, WEIBO_USER_INFO, weiBoUserInfo);
        weiBoUserInfoSoftReference = null;
        weiBoUserInfoSoftReference = new SoftReference<>(weiBoUserInfo);
    }

    public static WeiBoUserInfo read(Context context) {
        if(weiBoUserInfoSoftReference != null && weiBoUserInfoSoftReference.get() != null){
            return weiBoUserInfoSoftReference.get();
        }
        WeiBoUserInfo weiBoUserInfo = getObject(context, WEIBO_USER_INFO, WeiBoUserInfo.class);
        weiBoUserInfoSoftReference = null;
        weiBoUserInfoSoftReference = new SoftReference<>(weiBoUserInfo);
        return weiBoUserInfoSoftReference.get();
    }

    public static void clear(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(WEIBO_USER_INFO);
        editor.apply();
        weiBoUserInfoSoftReference = null;
    }

    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     * @param object
     */
    private static void setObject(Context context, String key, Object object) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //创建字节对象输出流
        ObjectOutputStream out = null;
        try {
            //然后通过将字对象进行64转码，写入key值为key的sp中
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getObject(Context context, String key, Class<T> clazz) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            //一样通过读取字节流，创建字节流输入流，写入对象并作强制转换
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
