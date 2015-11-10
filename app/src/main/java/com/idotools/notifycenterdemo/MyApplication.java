package com.idotools.notifycenterdemo;

import android.app.Application;
import android.content.Context;


/**
 * Created by LvWind on 15/11/10.
 */
public class MyApplication extends Application{
    private static Context context;
    public void onCreate() {
        super.onCreate();
        MyApplication.context=getApplicationContext();
    }

    public static Context getAppContext(){
       return MyApplication.context;
    }

    public static String getLanguageInfo() {
        return context.getResources().getConfiguration().locale.toString();
    }

}
