package com.m520it.newsreader09.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SP的工具类,里面保存或者获取所有这个项目的值
 */

public class SPUtils {

    public static boolean getBoolean(Context context,String key){
        // 先获得Sp 参数1 保存的xml的文件名config,config.xml
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        // 得到Sp的boolean
        //参数1 保存的key 参数二 默认值,没有这个key的时候,给默认值
       return sp.getBoolean(key,false);
        // 返回这个boolean
    }
    public static void setBoolean(Activity activity,String key,boolean value){
        // 先获得Sp 参数1 保存的xml的文件名config,config.xml
        SharedPreferences sp = activity.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();

    }

    public static String getString(Context activity,String key){
        // 先获得Sp 参数1 保存的xml的文件名config,config.xml
        SharedPreferences sp = activity.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        // 得到Sp的boolean
        //参数1 保存的key 参数二 默认值,没有这个key的时候,给默认值
        return sp.getString(key,"");
        // 返回这个boolean
    }
    public static void setString(Activity activity,String key,String value){
        // 先获得Sp 参数1 保存的xml的文件名config,config.xml
        SharedPreferences sp = activity.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();

    }

    public static void setInt(Context  context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }
    public static int  getInt(Context  context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
       return sp.getInt(key,0);
    }

    public static void setLong(Context  context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(key,value).commit();
    }
    public static long  getLong(Context  context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key,0);
    }


}
