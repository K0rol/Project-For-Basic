package com.m520it.newsreader09.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class AdsListBean implements Serializable{
    List<AdsBean> ads;//属性字段的名字要和键名一致
    int next_req;//代表下次请求的过期时间 比如 600分钟

    public List<AdsBean> getAds() {
        return ads;
    }

    public void setAds(List<AdsBean> ads) {
        this.ads = ads;
    }

    public int getNext_req() {
        return next_req;
    }

    public void setNext_req(int next_req) {
        this.next_req = next_req;
    }
}
