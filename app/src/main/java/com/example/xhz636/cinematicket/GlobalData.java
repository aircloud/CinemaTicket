package com.example.xhz636.cinematicket;

import android.app.Application;
import android.app.LocalActivityManager;

public class GlobalData extends Application {

    private String userid;
    private String cookie;
    private LocalActivityManager manager;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        userid = null;
        cookie = null;
        manager = null;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public LocalActivityManager getManager() {
        return manager;
    }

    public void setManager(LocalActivityManager manager) {
        this.manager = manager;
    }
}
