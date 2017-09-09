package com.yun.opern.db;

/**
 * Created by Yun on 2017/9/9 0009.
 */

public class FeedbackMessageInfo {
    private String message;
    private String datatime;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    @Override
    public String toString() {
        return "FeedbackMessageInfo{" +
                "message='" + message + '\'' +
                ", datatime='" + datatime + '\'' +
                '}';
    }
}
