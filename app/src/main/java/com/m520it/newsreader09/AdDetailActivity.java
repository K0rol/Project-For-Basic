package com.m520it.newsreader09;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AdDetailActivity extends AppCompatActivity {


    public static final String AD_DETAIL_URL = "AD_DETAIL_URL";

    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_detail);
        mWebView = (WebView) findViewById(R.id.webView);

        Intent intent = getIntent();
        String linkUrl = "";
        if(intent!=null){
            linkUrl = intent.getStringExtra(AD_DETAIL_URL);
            Log.e(getClass().getSimpleName()+" xmg", "onCreate: "+linkUrl);
        }
        //如果网页加载时,出现了重定向(自动跳到另一个网页),默认就会去调起其他的应用来展示网页
        mWebView.setWebViewClient(new WebViewClient());
        //打开javascript的使用,不再禁止
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(linkUrl);
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            //如果webView能够后退到上一个页面
            //就后退呗
            mWebView.goBack();
        }else{
            //否则
            super.onBackPressed();
        }



    }
}
