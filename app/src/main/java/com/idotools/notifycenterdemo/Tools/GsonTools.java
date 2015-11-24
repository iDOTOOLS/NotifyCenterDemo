package com.idotools.notifycenterdemo.Tools;

import com.google.gson.Gson;

/**
 * Created by LvWind on 15/10/28.
 * Tools of getting object from json
 */
public class GsonTools {
    private static Gson gson = new Gson();

    public GsonTools() {
    }

    public static <T> T getResult(String jsonString, Class<T> cls) {
        T t = null;
        try {
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return t;
    }

}
