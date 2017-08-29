package com.yun.opern.model;

/**
 * Created by Yun on 2017/8/29 0029.
 */

public class UserLoginRequestInfo {
    private String userId;
    private String userName;
    private String userGender;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }
}
