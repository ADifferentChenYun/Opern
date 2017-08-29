package com.yun.opern.model.event;

/**
 * Created by Yun on 2017/8/29 0029.
 */

public class UserLoginOrLogoutEvent {
    private boolean login = true;

    public UserLoginOrLogoutEvent(boolean login) {
        this.login = login;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}
