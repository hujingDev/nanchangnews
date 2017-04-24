package com.hujing.nanchangnews.utils;

import com.google.gson.Gson;
import com.hujing.nanchangnews.Gson.News.News;
import com.hujing.nanchangnews.Gson.News.NewsData;
import com.hujing.nanchangnews.Gson.Weather.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2017/4/13.
 */

public class JSONUtils {
    public static String reason;
    public static String stat;
    public static List<NewsData> parseNewsJSONWithJSONObject(String response) {
        List<NewsData> dataList=new ArrayList<>();
        try {
            JSONObject info = new JSONObject(response);
             reason = info.getString("reason");
            JSONObject result=info.getJSONObject("result");
             stat = result.getString("stat");
            JSONArray data = result.getJSONArray("data");
            for (int i=0;i<data.length();i++){
                NewsData myData = new NewsData();
                List<String> picList=new ArrayList<>();
                JSONObject jsonObject=data.getJSONObject(i);
                String title = jsonObject.getString("title");
                String date = jsonObject.getString("date");
                String category = jsonObject.getString("category");
                String authorName = jsonObject.getString("author_name");
                String url = jsonObject.getString("url");
                String thumbnail_pic_s = jsonObject.getString("thumbnail_pic_s");
                String thumbnail_pic_s02 = jsonObject.getString("thumbnail_pic_s02");
                String thumbnail_pic_s03 = jsonObject.getString("thumbnail_pic_s03");
                myData.setTitle(title);
                myData.setDate(date);
                myData.setCategory(category);
                myData.setAuthor(authorName);
                myData.setUrl(url);
                myData.setPic1(thumbnail_pic_s);
                myData.setPic2(thumbnail_pic_s02);
                myData.setPic2(thumbnail_pic_s03);
                dataList.add(myData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }
    public  static News parseNewsJSONWithGson(String response){
        News news = new Gson().fromJson(response, News.class);

        return news;
    }
    public static Weather parseWeatherJSONWithGson(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather5");
            String string = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(string,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
