package com.hujing.nanchangnews.Gson.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2017/4/22.
 */

public class HourlyForecast {
    @SerializedName("cond")
    public HourlyCond hourlyCond;
    public String pop;
    public String tmp;
    public String date;
    public class HourlyCond{
        public String txt;
    }
}
