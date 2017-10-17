package com.m520it.newsreader09;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.m520it.newsreader09.bean.AdsBean;
import com.m520it.newsreader09.bean.AdsListBean;
import com.m520it.newsreader09.util.Constant;
import com.m520it.newsreader09.util.HashCodeUtil;
import com.m520it.newsreader09.util.JsonUtil;
import com.m520it.newsreader09.util.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.m520it.newsreader09.AdDetailActivity.AD_DETAIL_URL;
import static com.m520it.newsreader09.DownloadIntentService.DOWNLOAD_SERVICE_DATA;

public class MainActivity extends AppCompatActivity {

    private ImageView mIvAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvAd = (ImageView) findViewById(R.id.iv_ad);

        String name = Thread.currentThread().getName();
        Log.e(getClass().getSimpleName() + " xmg", "onCreate: " + name);
        initData();
    }

    private void initData() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                reuqestData2();
            }
        }).start();*/
        //如果缓存里面有广告json字符串,并且该字符串没有过期,直接读取缓存,然后展示广告图
        String json = SPUtils.getString(MainActivity.this, AD_JSON);
        long out_date_time = SPUtils.getLong(MainActivity.this, OUT_DATE_TIME);
        long currentTimeMillis = System.currentTimeMillis();
        if (TextUtils.isEmpty(json) || currentTimeMillis > out_date_time) {
            //否则就去请求数据
            requestDataForOKHttp();
            Log.e(getClass().getSimpleName() + " xmg", "initData: " + "没有缓存或者缓存已经过期,开始请求数据");
            return;
        }
        //有数据的话,开始加载广告图进行显示
        Log.e(getClass().getSimpleName() + " xmg", "initData: " + "有缓存,不再去请求网络");
        //图片→有一个图片的路径→图片的下载地址(hashCode)→遍历javaBean→json缓存
//        Gson gson = new Gson();
//        AdsListBean listBean = gson.fromJson(json, AdsListBean.class);
        AdsListBean listBean = JsonUtil.parseJson(json, AdsListBean.class);
        List<AdsBean> ads = listBean.getAds();
        int index = SPUtils.getInt(MainActivity.this, AD_PIC_INDEX);
        //先展示第一张
        //使用前,先使用%取余,这样就可以数组越界
        index = index % ads.size();
        AdsBean adsBean = ads.get(index);
        String picUrl = adsBean.getRes_url()[0];
        String fileName = getExternalCacheDir() + "/" + HashCodeUtil.getHashCodeFileName(picUrl) + ".jpg";
        //有可能文件不存在(被某些不知名的原因导致图片删掉了)
        File file = new File(fileName);
        if (file.exists() && file.length() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            mIvAd.setImageBitmap(bitmap);

            index++;
            SPUtils.setInt(MainActivity.this, AD_PIC_INDEX, index);

            final String link_url = adsBean.getAction_params().getLink_url();
            //如果广告地址为空,就不做点击设置了
            if (TextUtils.isEmpty(link_url)) {
                return;
            }
            mIvAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AdDetailActivity.class);
                    intent.putExtra(AD_DETAIL_URL,link_url);
                    Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
                    Intent[] intents = {intent1, intent};
                    //通过startActivities 开启多个页面Activity
                    startActivities(intents);
                    finish();
                }
            });
        }

    }

    public static final String AD_PIC_INDEX = "AD_PIC_INDEX";

    private void requestDataForOKHttp() {
        //1 OKHttpCLient
        OkHttpClient okHttpClient = new OkHttpClient();
        //2 准备一个请求
        Request request = new Request.Builder().url(Constant.AD_URL).build();
        //3 准备一个Call对象
        Call call = okHttpClient.newCall(request);
        //4 异步 enqueue
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(getClass().getSimpleName() + " xmg", "onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean successful = response.isSuccessful();
                if (!successful) {
                    onFailure(call, new IOException("响应未成功"));
                    return;
                }
                //成功拿到数据    响应体,数据都在它里面
                ResponseBody body = response.body();
//              注意!!!!! 不是  body.toString()
                String result = body.string();
                //用的fastJson    gson    原生的
//                parseJsonForNeture(result);
//                Gson gson = new Gson();
//                AdsListBean adsListBean = gson.fromJson(result, AdsListBean.class);
                AdsListBean adsListBean = JsonUtil.parseJson(result, AdsListBean.class);
                Log.e(getClass().getSimpleName() + " xmg", "onResponse: " + adsListBean);
                startDownLoadInBackground(adsListBean);
            }
        });
    }

    public static final String AD_JSON = "AD_JSON";
    public static final String OUT_DATE_TIME = "OUT_DATE_TIME";

    //开始后台下载图片
    private void startDownLoadInBackground(AdsListBean adsListBean) {
        //先把javaBean通过gson转为一个json字符串,将其保存到缓存   数据的持久化有这么写方案:文件/数据库/SharedPerference/网络/ContentProvider
        Gson gson = new Gson();
        String json = gson.toJson(adsListBean);
        //通过SP将其保存
        SPUtils.setString(MainActivity.this, AD_JSON, json);
        //保存一下过期的时间
        long outDateTime = System.currentTimeMillis() + adsListBean.getNext_req() * 60 * 1000;
        SPUtils.setLong(MainActivity.this, OUT_DATE_TIME, outDateTime);

        //后台的进行下载   使用service来下载
        //这里使用IntentService 和普通Service的区别:
        //1 构造方法不一样,需要设置传递一个String作为它内部的后台线程的线程名
        //2 可以直接执行一些后台任务,执行完任务后,会stopSelf,自杀
        //注意!!! 传递的JavaBean要能够序列化 JavaBean内部中所嵌套的JavaBean也要能够实现序列化
        Intent intent = new Intent(getApplicationContext(), DownloadIntentService.class);
        intent.putExtra(DOWNLOAD_SERVICE_DATA, adsListBean);
        startService(intent);
    }


    //使用原生的方式解析json
    private void parseJsonForNeture(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            //opt 和 get区别 前者即使拿不到数据会返回一个默认值给你 后者会直接抛异常
            JSONArray ads = jsonObject.getJSONArray("ads");
            for (int i = 0; i < ads.length(); i++) {
                //遍历
                JSONObject adJsonObject = ads.getJSONObject(i);
                //取出action_params
                JSONObject action_params = adJsonObject.getJSONObject("action_params");
                //取出action_params中的字段link_url
                String linkUrl = action_params.optString("link_url");
                //拿resUrl
                JSONArray res_url = adJsonObject.getJSONArray("res_url");
                String picUrl = res_url.getString(0);
                Log.e(getClass().getSimpleName() + " xmg", "onResponse:  linkUrl " + linkUrl
                        + " picUrl " + picUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*public int getInt(String name) throws JSONException {
        Object object = get(name);
        Integer result = JSON.toInteger(object);
        if (result == null) {
            throw JSON.typeMismatch(name, object, "int");
        }
        return result;
    }*/

    /*public int optInt(String name, int fallback) {
        Object object = opt(name);
        Integer result = JSON.toInteger(object);
        return result != null ? result : fallback;
    }*/

    private void reuqestData2() {
        //httpUrlConnection
        //HttpClient            6.0版本 已经被删除了
        //Volley            网络请求库 google  内部集成了httpUrlConnection HttpClient
        try {
            URL url = new URL(Constant.AD_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                //有数据返回
                InputStream inputStream =
                        urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String str = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuilder.append(str);
                }
                bufferedReader.close();
                String result = stringBuilder.toString();
                Log.e(getClass().getSimpleName() + " xmg", "initData: ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/*//以下为Synchronous Get 同步的get请求
    //1 OkHttpClient
    *//*private final OkHttpClient client = new OkHttpClient();

    public void run() throws Exception {

        //2 准备一个Request对象(请求)
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        //3 调用client的newCall方法生成了Call对象,方法参数就是第二步中的request对象,
        Call call = client.newCall(request);
        //4 调用Call对象的execute方法来得到一个response响应对象     execute请求方法就是一个同步的方法
        Response response = call.execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        System.out.println(response.body().string());
    }*//*

//Asynchronous Get 异步的get请求

    //1 OkHttpClient 跟先前一样准备一个OkHttpClient
private final OkHttpClient client = new OkHttpClient();

    public void run() throws Exception {
        //2 准备一个Request对象(请求)
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        //3 调用client的newCall方法生成了Call对象,方法参数就是第二步中的request对象,
        Call call = client.newCall(request);
        //4 调用Call对象的enqueue方法来得到一个response响应对象
        call.enqueue(new Callback() {
            //onFailure 在请求失败时会被回调,被执行
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            //onResponse 在请求有响应时会被回调,被执行
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println(response.body().string());
            }
        });
        //enqueue方法是异步的方法,我写在enqueue方法后面的代码可以马上被执行,不会被阻塞
//        Toast.makeText()
    }*/
