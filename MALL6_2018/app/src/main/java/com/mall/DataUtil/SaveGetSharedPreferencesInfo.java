package com.mall.DataUtil;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.mall.MapUtil.LocationMode;

public class SaveGetSharedPreferencesInfo {


    final static String SAVECITYKEY = "savecity";
    private static String defaultcity = "";

    public static void saveCity(Context context, LocationMode locationMode) {
        Log.e("存储的对象json", "json" + beanToJSONString(locationMode));
        SharedPreferencesUtils.setParam(context, SAVECITYKEY, beanToJSONString(locationMode));
    }

    public static LocationMode getCity(Context context) {
        try {
            Gson gson = new Gson();
            String json = (String) SharedPreferencesUtils.getParam(context, SAVECITYKEY, defaultcity);
            Log.e("取出来的对象json", "json" + json);
            LocationMode res = gson.fromJson(json, LocationMode.class);
            return res;
        } catch (Exception e) {
            Log.e("取出来的对象json", "异常" + e.getMessage());
            return null;
        }

    }

    public static void getCity(Context context, String cityName) {
        SharedPreferencesUtils.getParam(context, SAVECITYKEY, cityName);
    }

    public static String beanToJSONString(Object bean) {
        return new Gson().toJson(bean);
    }
}
