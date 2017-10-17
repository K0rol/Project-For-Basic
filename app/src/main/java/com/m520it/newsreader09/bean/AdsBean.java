package com.m520it.newsreader09.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class AdsBean implements Serializable{

    ActionParams action_params;
    String[] res_url;

    public ActionParams getAction_params() {
        return action_params;
    }

    public void setAction_params(ActionParams action_params) {
        this.action_params = action_params;
    }

    public String[] getRes_url() {
        return res_url;
    }

    public void setRes_url(String[] res_url) {
        this.res_url = res_url;
    }
}
