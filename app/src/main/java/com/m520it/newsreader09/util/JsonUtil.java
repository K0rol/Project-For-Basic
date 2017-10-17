package com.m520it.newsreader09.util;

import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class JsonUtil {
    static Gson gson = new Gson();
//    AdsListBean listBean = gson.fromJson(json, AdsListBean.class);

   /* public static Object parseJson(String json,Class clazz){
        if(TextUtils.isEmpty(json)){
            return null;
        }
        Object obj = gson.fromJson(json, clazz);
        return obj;
    }*/

    public static <T> T parseJson(String json,Class<T> clazz){
        if(TextUtils.isEmpty(json)){
            return null;
        }
        T listBean = gson.fromJson(json, clazz);
        return listBean;
    }


}
