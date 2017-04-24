package com.hujing.nanchangnews.Gson.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2017/4/22.
 */

public class DailyForecast {
    @SerializedName("cond")
    public DayCond dayCond;
    public String date;
    public String uv;
    public String vis;
    public String hum;//相对湿度
    public String pop;//降水概率
    @SerializedName("tmp")
    public Tmp tmp;
    @SerializedName("wind")
    public Wind wind;
    public class DayCond {
        @SerializedName("txt_d")
        public String dayWeather;
    }
    public class Tmp {
        public  String max;
        public  String min;
    }
    public class Wind{
        public String dir;//风向
        public String sc;//风力等级
    }
}
