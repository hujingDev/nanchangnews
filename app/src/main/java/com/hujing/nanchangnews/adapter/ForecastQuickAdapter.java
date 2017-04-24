package com.hujing.nanchangnews.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hujing.nanchangnews.Gson.Weather.DailyForecast;
import com.hujing.nanchangnews.R;

import java.util.List;

/**
 * Created by acer on 2017/4/23.
 */

public class ForecastQuickAdapter extends BaseQuickAdapter<DailyForecast,BaseViewHolder> {
    public ForecastQuickAdapter(int layoutResId, List<DailyForecast> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DailyForecast item) {
        helper.setText(R.id.tv_item_date,item.date)
                .setText(R.id.tv_item_info,item.dayCond.dayWeather)
                .setText(R.id.tv_item_max,item.tmp.max)
                .setText(R.id.tv_item_min,item.tmp.min);
    }
}
