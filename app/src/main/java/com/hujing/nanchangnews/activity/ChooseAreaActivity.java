package com.hujing.nanchangnews.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hujing.nanchangnews.R;
import com.hujing.nanchangnews.utils.DataBaseUtils;
import com.hujing.nanchangnews.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {

    public static final String WEATHER_CODE = "weather_code";
    private TextView tv_area;
    private ImageView iv_area;
    private ListView lv_area;
    private List<String> dataList=new ArrayList<>();
    private int currentLevel;
    private final static int PROVINCE=0;
    private final static int CITY=1;
    private final static int DISTRICT=2;
    private ArrayAdapter<String> adapter;
    private String provinceName;
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        initUI();
        setListView();
        setImageViewOnClickListener();
    }

    private void setImageViewOnClickListener() {
        iv_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_area.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (currentLevel){
                            case DISTRICT:
                                queryCity();
                                break;
                            case CITY:
                                queryProvince();
                                break;
                            case PROVINCE:
                                showAlertDialog();
                        }
                    }
                });
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告")
                .setMessage("城市尚未选择请问是否确定退出?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void initUI() {
        tv_area = (TextView) findViewById(R.id.tv_area);
        iv_area = (ImageView) findViewById(R.id.iv_area);
        lv_area = (ListView) findViewById(R.id.lv_area);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        lv_area.setAdapter(adapter);
    }
    private void setListView(){
        queryProvince();
        lv_area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==PROVINCE){
                    provinceName = dataList.get(position);
                    queryCity();
                }else if (currentLevel==CITY){
                    cityName = dataList.get(position);
                    queryDistrict();
                }else  if(currentLevel==DISTRICT){
                    String districtName=dataList.get(position);
                    String weatherCode = DataBaseUtils.queryWeatherCode(districtName);
                    Intent intent=new Intent();
                    intent.putExtra(WEATHER_CODE,weatherCode);
                    SpUtils.setString(ChooseAreaActivity.this,districtName,SpUtils.SELECT_CITY);
                    setResult(RESULT_OK,intent);
                    finish();
                }

            }
        });
    }
    private void queryProvince(){
        dataList.clear();
        tv_area.setText("中国");
        currentLevel=PROVINCE;
        List<String> provinceList = DataBaseUtils.queryProvince();
        dataList.addAll(provinceList);
        adapter.notifyDataSetChanged();
    }
    private void queryCity(){
        dataList.clear();
        tv_area.setText(provinceName);
        currentLevel=CITY;
        List<String> cityList = DataBaseUtils.queryCityName(DataBaseUtils.queryProvinceCode(provinceName));
        dataList.addAll(cityList);
        adapter.notifyDataSetChanged();
    }
    private void queryDistrict(){
        currentLevel=DISTRICT;
        dataList.clear();
        tv_area.setText(cityName);
        List<String> districtList=DataBaseUtils.queryDistrictName(DataBaseUtils.queryCityCode(cityName));
        dataList.addAll(districtList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                switch (currentLevel){
                    case DISTRICT:
                        queryCity();
                        break;
                    case CITY:
                        queryProvince();
                        break;
                    case PROVINCE:
                        showAlertDialog();
                }
                return  true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
