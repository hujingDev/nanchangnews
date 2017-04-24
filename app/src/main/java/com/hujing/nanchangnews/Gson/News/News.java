package com.hujing.nanchangnews.Gson.News;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2017/4/14.
 */

public class News {
    @SerializedName("reason")
    public String reason;
    @SerializedName("result")
    public Result result;
}
