package com.example.mastermind.testapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by mastermind on 26/4/2018.
 */

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();


    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
