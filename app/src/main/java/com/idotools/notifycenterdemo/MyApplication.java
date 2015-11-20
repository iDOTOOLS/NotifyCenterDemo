package com.idotools.notifycenterdemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


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
    public  static String getUserId(){
        //TODO SDK
        return "123456";
    }
    public static long getLastTimestamp(){
        SharedPreferences sp = context.getSharedPreferences("updateStrategy", Context.MODE_PRIVATE);
        long timestamp = sp.getLong("lastTimeStamp", 0);
        return timestamp;
    }
    public static boolean getShowPicFlag(){
        PreferenceManager.setDefaultValues(context, R.xml.preference, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("pic_auto_load_switch",false);
    }

    public static int getFontSizeLevel(){
        PreferenceManager.setDefaultValues(context, R.xml.preference, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString("fontSizeLevel", "2");

        return Integer.parseInt(value);

    }

}
