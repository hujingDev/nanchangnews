package com.hujing.nanchangnews.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hujing.nanchangnews.Gson.Weather.DailyForecast;
import com.hujing.nanchangnews.Gson.Weather.Weather;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.adapter.ForecastQuickAdapter;
import com.hujing.nanchangnews.utils.HttpUtils;
import com.hujing.nanchangnews.utils.JSONUtils;
import com.hujing.nanchangnews.utils.SpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetailWeatherActivity extends AppCompatActivity {
    private static final int SUCCESS=1;
    private static final int FAILED=2;
    private TextView tv_city_name;
    private TextView tv_update_time;
    private RecyclerView forecast_rv;
    private TextView tv_wind_direction;
    private TextView tv_wind_size;
    private TextView air_suggestion;
    private TextView sport_suggestion;
    private TextView travel_suggestion;
    private String url;
    private  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FAILED:
                    weather_srl.setRefreshing(false);
                    Toast.makeText(DetailWeatherActivity.this, "获取天气失败！！！", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    weather_srl.setRefreshing(false);
                    tv_degree.setText(weather.now.tmp+"℃");
                    tv_weather_info.setText(weather.now.nowCond.txt);
                    tv_update_time.setText("更新时间:"+ weather.basic.update.loc.split(" ")[1]);
                    tv_wind_direction.setText(weather.dailyForecastList.get(0).wind.dir);
                    tv_wind_size.setText(weather.dailyForecastList.get(0).wind.sc);
                    travel_suggestion.setText("出行建议:  "+weather.suggestion.travel.txt);
                    air_suggestion.setText("空气质量:  "+weather.suggestion.air.txt);
                    sport_suggestion.setText("运动建议:  "+weather.suggestion.sport.txt);
                    forecastList.clear();
                    forecastList.addAll(weather.dailyForecastList);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
    private Weather weather;
    private TextView tv_degree;
    private TextView tv_weather_info;
    private List<DailyForecast> forecastList;
    private ForecastQuickAdapter adapter;
    private SwipeRefreshLayout weather_srl;
    private ImageView iv_select_city;
    private ImageView iv_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_weather);
        initUI();
        requestData();
        initAdapter();
        setRefresh();
        chooseCity();
        if (TextUtils.isEmpty(SpUtils.getString(this,SpUtils.BEING_PIC))) {
            requestImageResource();
        }else {
            Glide.with(DetailWeatherActivity.this).load(SpUtils.getString(this,SpUtils.BEING_PIC)).centerCrop().into(iv_background);
            requestImageResource();
        }
        requestWindowTransparent();
    }

    private void requestWindowTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void requestImageResource() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                if (bingPic.contains("cn.bing.com")){
                    SpUtils.setString(DetailWeatherActivity.this,bingPic,SpUtils.BEING_PIC);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(DetailWeatherActivity.this).load(bingPic).centerCrop().into(iv_background);
                        }
                    });
                }
            }
        });
    }

    private void chooseCity() {
        iv_select_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DetailWeatherActivity.this,ChooseAreaActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    private void setRefresh() {
        weather_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
    }

    private void initAdapter() {
        adapter = new ForecastQuickAdapter(R.layout.forecast_item,forecastList);
        forecast_rv.setLayoutManager(new LinearLayoutManager(this));
        forecast_rv.setAdapter(adapter);
    }

    private void requestData() {
        forecastList = new ArrayList<>();
        url="https://free-api.heweather.com/v5/weather?city="
                +SpUtils.getString(this,SpUtils.SELECT_CITY,"北京")+
                "&key=5420175089474f5e9d8f9ae67ffba86d";
        HttpUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what=FAILED;
                handler.sendMessageDelayed(message,1000);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Message message = Message.obtain();
                if (json.contains("HeWeather5")){
                    weather = JSONUtils.parseWeatherJSONWithGson(json);
                    message.what=SUCCESS;
                    handler.sendMessageDelayed(message,1000);
                }else {
                    message.what=FAILED;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void initUI() {
        iv_background = (ImageView) findViewById(R.id.iv_background);
        iv_select_city = (ImageView) findViewById(R.id.iv_select_city);
        weather_srl = (SwipeRefreshLayout) findViewById(R.id.weather_srl);
        weather_srl.setRefreshing(true);
        tv_degree = (TextView) findViewById(R.id.tv_degree);
        tv_weather_info = (TextView) findViewById(R.id.tv_weather_info);
        tv_city_name = (TextView) findViewById(R.id.tv_city_name);
        tv_city_name.setText(SpUtils.getString(this,SpUtils.SELECT_CITY,"北京"));
        tv_update_time = (TextView) findViewById(R.id.tv_update_time);
        forecast_rv = (RecyclerView) findViewById(R.id.forecast_rv);
        tv_wind_direction = (TextView) findViewById(R.id.tv_wind_direction);
        tv_wind_size = (TextView) findViewById(R.id.tv_wind_size);
        air_suggestion = (TextView) findViewById(R.id.air_suggestion);
        sport_suggestion = (TextView) findViewById(R.id.sport_suggestion);
        travel_suggestion = (TextView) findViewById(R.id.travel_suggestion);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    weather_srl.setRefreshing(true);
                    tv_city_name.setText(SpUtils.getString(this,SpUtils.SELECT_CITY,"北京"));
                    requestData();
                }
                break;
            default:
                break;
        }
    }
}
