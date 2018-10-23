package com.lzw.easyhttp.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Author: lzw
 * Date: 2018/10/23
 * Description: This is OKHttpProcessor
 */

public class OKHttpProcessor implements IHttpProcessor {

    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    public OKHttpProcessor() {
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpCacheInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        mDelivery = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(String url, Map<String, Object> params, final ICallBack callBack) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonString = JSONObject.toJSONString(params);
        RequestBody requestBody = RequestBody.create(JSON, jsonString);
        final Request request = new Request.Builder().url(url)
                .post(requestBody).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFail(e.getMessage().toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String str = response.body().string();
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(str);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void get(String url, ICallBack callBack) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
            callBack.onSuccess(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static class HttpCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
//            String token = SpUtils.getString("token");
            Request request = chain.request().newBuilder()
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Accept", "application/json")
//                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            //打印请求信息
            Log.d("RetrofitLog:", "url:" + request.url());
            Log.d("RetrofitLog:", "headers:" + request.headers().toString());
            Log.d("RetrofitLog:", "method:" + request.method());

            //记录请求耗时
            long startNs = System.nanoTime();
            Response response = null;
            try {
                //发送请求，获得相应，
                response = chain.proceed(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            //打印请求耗时
            Log.d("RetrofitLog:", "耗时:" + tookMs + "ms");
            //使用response获得headers(),可以更新本地Cookie。
            Log.d("RetrofitLog:", "headers==========");
            Headers headers = response.headers();
            Log.d("RetrofitLog:", headers.toString());

            //获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
            ResponseBody responseBody = response.body();

            //为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            //获得返回的数据
            Buffer buffer = source.buffer();
            //使用前clone()下，避免直接消耗

            String s = buffer.clone().readString(Charset.forName("UTF-8"));
            Log.d("RetrofitLog:", "response:" + buffer.clone().readString(Charset.forName("UTF-8")));

            return response;
        }
    }
}
