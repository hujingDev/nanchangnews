package com.hujing.nanchangnews.Gson.News;

import com.google.gson.annotations.SerializedName;
import com.hujing.nanchangnews.Gson.News.NewsData;

import java.util.List;

/**
 * Created by acer on 2017/4/14.
 */

public class Result {
    @SerializedName("stat")
    public  int stat;
    @SerializedName("data")
    public List<NewsData> dataList;
}
