package com.example.igor.utils;

import android.app.Application;


public class DataBaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataBaseHelper.init(getApplicationContext());
    }

}
