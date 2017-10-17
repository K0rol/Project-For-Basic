package com.m520it.newsreader09.util;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class HashCodeUtil {
    public static String getHashCodeFileName(String url) {
        String hashCode = "";
        if (!TextUtils.isEmpty(url)) {
            int i = url.hashCode();
            hashCode = "" + i;
        }
        return hashCode;
    }
}
