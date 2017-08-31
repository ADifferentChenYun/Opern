package com.yun.opern.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yun on 2017/8/10 0010.
 */

public class HttpCore {
    private static final String baseUrl = "http://106.14.183.240:80/opern/";
    //private static final String baseUrl = "http://192.168.0.115:8080/opern/";
    private static HttpCore httpCore;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private Api api;

    private HttpCore() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addNetworkInterceptor(new HeaderInterceptor())
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    private class HeaderInterceptor implements Interceptor{

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
                    .addHeader("Accept","application/json")
                    .addHeader("Content-Type","application/json")
                    .build();
            return chain.proceed(request);
        }
    }

    public static HttpCore getInstance(){
        if(httpCore == null){
            synchronized (HttpCore.class){
                if(httpCore == null){
                    httpCore = new HttpCore();
                }
            }
        }
        return httpCore;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public Api getApi() {
        return api;
    }
}
