package com.hujing.nanchangnews.Gson.Weather;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by acer on 2017/4/22.
 */

public class Weather implements Serializable {
    @SerializedName("status")
    public String status;
    @SerializedName("basic")
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecastList;
    @SerializedName("hourly_forecast")
    public List<HourlyForecast> hourlyForecastList;
    @SerializedName("now")
    public Now now;
    @SerializedName( "suggestion")
    public Suggestion suggestion;
}
