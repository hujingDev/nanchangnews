package com.hujing.nanchangnews.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by acer on 2017/4/13.
 */

public class HttpUtils {
    public static void sendOkHttpRequest(String url, Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
}
