package com.m520it.newsreader09;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.m520it.newsreader09.bean.AdsBean;
import com.m520it.newsreader09.bean.AdsListBean;
import com.m520it.newsreader09.util.HashCodeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadIntentService extends IntentService {

    public static final String DOWNLOAD_SERVICE_DATA = "DOWNLOAD_SERVICE_DATA";

    public DownloadIntentService() {
        //1 需要写构造方法
        super("DownloadIntentService");
    }

    //2 onHandleIntent 该方法中的代码就会被执行在后台线程中,这里面的代码被执行完了,这个Service就会自杀
    @Override
    protected void onHandleIntent(Intent intent) {
        String name = Thread.currentThread().getName();
        Log.e(getClass().getSimpleName()+" xmg", "onHandleIntent: "+name);
        if (intent != null) {
            //下载图片
            AdsListBean listBean = (AdsListBean) intent.getSerializableExtra(DOWNLOAD_SERVICE_DATA);
            List<AdsBean> ads = listBean.getAds();
            for (int i = 0; i < ads.size(); i++) {
                AdsBean adsBean = ads.get(i);
                String picUrl = adsBean.getRes_url()[0];
                //判断一下是否已经下载
                String fileName = getExternalCacheDir()+"/"+ HashCodeUtil.getHashCodeFileName(picUrl)+".jpg";
                File file = new File(fileName);
                if(file.exists()&&file.length()>0){
                    //不用再下载了,已经有图片了 使用continue 避免循环直接被打断,保证后续的图片的判断和下载能够正常执行
                    Log.e(getClass().getSimpleName()+" xmg", "onHandleIntent: "+"图片已经下载,不再去重复下载了");
                    continue;
                }
                downloadPic(picUrl);
            }
        }
    }

    //对图片进行下载
    private void downloadPic(final String picUrl) {
        //来一次网络请求,只不过这一次inputStream不再是转为String而是转为bitmap图片
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(picUrl).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(getClass().getSimpleName()+" xmg", "onFailure: "+"下载失败了");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    Log.e(getClass().getSimpleName()+" xmg", "onResponse: "+"下载失败");
                    return;
                }
                ResponseBody body = response.body();
                InputStream inputStream = body.byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //还需要将bitmap保存成本地图片文件
                //getExternalCacheDir() 获得一个SD卡上的缓存目录
                // 没有SD卡时 getExternalCacheDir()==getCacheDir()
                //这里使用图片对应的下载地址来命名文件,但是下载地址太长,格式不好,所以使用地址的hashCode来写
                String fileName = getExternalCacheDir()+"/"+ HashCodeUtil.getHashCodeFileName(picUrl)+".jpg";
                File file = new File(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                Log.e(getClass().getSimpleName()+" xmg", "onResponse: "+"下载并保存成功");
            }
        });
    }

}
