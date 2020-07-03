package com.taide.ewarn;

import android.app.Application;
import android.content.Context;

import com.xuexiang.xui.XUI;

import org.litepal.LitePal;

public class MyApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);   //初始化数据库框架
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
    }

    //返回
    public static Context getContextObject() {
        return context;
    }

}
