package com.voler.theirprivacy.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/12.
 */

public class HttpManager {
    private  static ApiManager apiManager;
    private static final long DEFAULT_TIMEOUT=4;
    private static final String BASE_URL="http://apis.baidu.com/heweather/weather/";



    //获取单例
    public static ApiManager getHttpService() {
        if (apiManager == null) {
            synchronized (HttpManager.class) {
                if (apiManager == null) {
                    //手动创建一个OkHttpClient并设置超时时间
//                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor
                            (new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                            .build();
//                    builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    Retrofit retrofit = new Retrofit.Builder()
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .baseUrl(BASE_URL)
                            .build();
                    apiManager = retrofit.create(ApiManager.class);
                }
            }
        }
        return apiManager;
    }
}
