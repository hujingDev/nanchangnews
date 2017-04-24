package com.hujing.nanchangnews.Gson.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2017/4/22.
 */

public class Suggestion {
    @SerializedName("air")
    public Air air;
    @SerializedName("sport")
    public Sport sport;
    @SerializedName("trav")
    public Travel travel;
    public class Air{
        public String txt;
    }
    public class Sport{
        public String txt;
    }
    public class Travel{
        public String txt;
    }
}
