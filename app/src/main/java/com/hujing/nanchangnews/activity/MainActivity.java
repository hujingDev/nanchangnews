package com.hujing.nanchangnews.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.hujing.nanchangnews.Gson.Weather.Weather;
import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.adapter.MyFragmentPagerAdapter;
import com.hujing.nanchangnews.fragment.BaseFragment;
import com.hujing.nanchangnews.utils.DataBaseUtils;
import com.hujing.nanchangnews.utils.HttpUtils;
import com.hujing.nanchangnews.utils.JSONUtils;
import com.hujing.nanchangnews.utils.SpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int AREA_CODE = 1;
    private static final int FAILED = 404;
    private static final int SUCCESS = 401;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private TabLayout tab;
    private List<Fragment> fragmentList;
    private static final String TAG = "天气";
    private List<String> fragmentTitles;
    private MyFragmentPagerAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            iv_refresh.clearAnimation();
            switch (msg.what){
                case FAILED:
                    tv_now_body_feel_tmp.setText("体感温度:   暂无信息");
                    tv_now_weather.setText("天气:   暂无信息");
                    tv_now_tmp.setText("气温:   暂无信息");
                    tv_now_date.setText("发布时间:   暂无信息");
                    break;
                case SUCCESS:
                    tv_now_body_feel_tmp.setText("体感温度:   "+weather.now.fl);
                    tv_now_weather.setText("天气:   "+weather.now.nowCond.txt);
                    tv_now_tmp.setText("气温:   "+weather.now.tmp);
                    tv_now_date.setText("发布时间:   "+weather.basic.update.loc);
                    break;
                default:
                    break;
            }
        }
    };
    private NavigationView navigationView;
    private TextView tv_city;
    private TextView tv_now_weather;
    private TextView tv_now_body_feel_tmp;
    private TextView tv_now_tmp;
    private Weather weather;
    private TextView tv_now_date;
    private ImageView iv_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initUI();
        initFragment();
        setViewPagerAdapter();
        setNavigationView();
       /* testWeather();*/
    }

    private void setNavigationView() {
        initNavigationViewUI();
        navigationView.setCheckedItem(R.id.collection);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.collection:
                        Intent collectIntent=new Intent(MainActivity.this,CollectionOfNewsActivity.class);
                        startActivity(collectIntent);
                        break;
                    case  R.id.select_area:
                        Intent selectAreaIntent=new Intent(MainActivity.this, ChooseAreaActivity.class);
                        startActivityForResult(selectAreaIntent, AREA_CODE);
                        break;
                    case  R.id.weather_details:
                        Intent weatherDetailsIntent=new Intent(MainActivity.this,DetailWeatherActivity.class);
                        startActivity(weatherDetailsIntent);
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void initNavigationViewUI() {
        View headerView = navigationView.getHeaderView(0);
        tv_city = (TextView) headerView.findViewById(R.id.tv_city);
        tv_city.setText(SpUtils.getString(this,SpUtils.SELECT_CITY,"北京"));
        tv_now_weather = (TextView) headerView.findViewById(R.id.tv_now_weather);
        tv_now_body_feel_tmp = (TextView) headerView.findViewById(R.id.tv_now_body_feel_tmp);
        tv_now_tmp = (TextView) headerView.findViewById(R.id.tv_now_tmp);
        tv_now_date = (TextView) headerView.findViewById(R.id.tv_now_date);
        iv_refresh = (ImageView) headerView.findViewById(R.id.iv_refresh);
        String weatherCode = DataBaseUtils.queryWeatherCode(SpUtils.getString(this, SpUtils.SELECT_CITY, "北京"));
        setWeather(weatherCode);
        setImageViewOnClickListener();
    }
    private void setImageViewOnClickListener(){
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              refresh();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        tv_now_body_feel_tmp.setText("体感温度:   正在查询中");
        tv_now_weather.setText("天气:   正在查询中");
        tv_now_tmp.setText("气温:   正在查询中");
        tv_now_date.setText("发布时间   正在查询中");
        Animation animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate);
        LinearInterpolator lin=new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setFillAfter(true);
        iv_refresh.startAnimation(animation);
        tv_city.setText(SpUtils.getString(MainActivity.this,SpUtils.SELECT_CITY,"北京"));
        setWeather(DataBaseUtils.queryWeatherCode(SpUtils.getString(MainActivity.this,SpUtils.SELECT_CITY,"北京")));
    }

    private void setWeather(String weatherCode) {
        String url="https://free-api.heweather.com/v5/weather?city="+weatherCode+"&key=5420175089474f5e9d8f9ae67ffba86d";
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
                    handler.sendMessageDelayed(message,1000);
                }
            }
        });
    }


    private void initData() {
        String[] titles = new String[]{"头条", "体育", "社会", "国内", "国际", "娱乐", "军事", "科技", "财经", "时尚"};
        fragmentTitles = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            fragmentTitles.add(titles[i]);
        }
    }


    private void initFragment() {
        fragmentList = new ArrayList<>();
        String[] types = new String[]{"top", "tiyu",
                "shehui", "guonei", "guoji", "yule", "junshi", "keji", "caijing", "shishang"};
        for (int i = 0; i < types.length; i++) {
            fragmentList.add(BaseFragment.newInstance(types[i]));
        }
    }

    private void setViewPagerAdapter() {
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitles);
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(fragmentTitles.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(fragmentTitles.get(0));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.fragment_vp);
        tab = (TabLayout) findViewById(R.id.tab);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case AREA_CODE:
                if (resultCode==RESULT_OK){
                    tv_city.setText(SpUtils.getString(this,SpUtils.SELECT_CITY,"北京"));
                    setWeather(data.getStringExtra(ChooseAreaActivity.WEATHER_CODE));
                }
                break;
        }
    }
}
