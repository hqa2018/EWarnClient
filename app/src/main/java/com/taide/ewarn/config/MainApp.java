package com.taide.ewarn.config;

import android.app.Application;

import org.litepal.LitePal;

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        LitePal.initialize(this);
    }
}
