package com.yun.opern.utils;


import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Yun on 2017/10/24 0024.
 */

public class ErrorMessageUtil {

    public static void showErrorByToast(String errorMsg) {
        T.showShort(errorMsg + "...QAQ");
    }

    public static void showErrorByToast(Throwable t) {
        if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
            T.showShort("网络超时...QAQ");
        } else if (t instanceof ConnectException) {
            T.showShort("网络连接错误...QAQ");
        } else {
            T.showShort("意料之外的错误...QAQ");
        }
    }
}
