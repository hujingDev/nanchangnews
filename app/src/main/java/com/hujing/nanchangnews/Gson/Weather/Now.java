package com.hujing.nanchangnews.Gson.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2017/4/22.
 */

public class Now {
    @SerializedName("cond")
    public NowCond nowCond;
    public String tmp;
    public String fl;
    public class NowCond{
        public String txt;
    }
}
