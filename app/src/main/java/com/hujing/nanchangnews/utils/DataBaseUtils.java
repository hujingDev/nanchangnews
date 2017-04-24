package com.hujing.nanchangnews.utils;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2017/4/22.
 */

public class DataBaseUtils {

    static String path = "data/data/com.hujing.nanchangnews/files/AllCities.db";
    static SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

    public static List<String> queryProvince() {
        Cursor cursor = database.query("province", new String[]{"provincename"}, null, null, null, null, null);
        List<String> provinceName=new ArrayList<>();
        while (cursor.moveToNext()){
            provinceName.add(cursor.getString(cursor.getColumnIndex("provincename")));
        }
        return provinceName;
    }
    public static String queryProvinceCode(String provinceName){
        Cursor cursor = database.query("province",
                new String[]{"provincecode"}, "provincename=?", new String[]{provinceName}, null, null, null);
        while (cursor.moveToNext()){
           return cursor.getString(cursor.getColumnIndex("provincecode"));
        }
        return null;
    }
    public static List<String> queryCityName(String provinceCode){
        Cursor cursor = database.query("city", new String[]{"cityname"}, "provincecode=?",
                new String[]{provinceCode}, null, null, null);
        List<String> cityName=new ArrayList<>();
        while (cursor.moveToNext()){
            cityName.add(cursor.getString(cursor.getColumnIndex("cityname")));
        }
        return cityName;
    }
    public static String queryCityCode(String cityName){
        Cursor cursor = database.query("city", new String[]{"citycode"}, "cityname=?", new String[]{cityName}, null, null, null);
        while (cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex("citycode"));
        }
        return null;
    }
    public static List<String> queryDistrictName(String cityCode) {
        Cursor cursor = database.query("district", new String[]{"districtname"}, "citycode=?", new String[]{cityCode}, null, null, null);
        List<String> district = new ArrayList<>();
        while (cursor.moveToNext()) {
            district.add(cursor.getString(cursor.getColumnIndex("districtname")));
        }
        return district;
    }
    public static String queryWeatherCode(String districtName){
        Cursor cursor = database.query("district", new String[]{"weathercode"}, "districtname=?", new String[]{districtName}, null, null, null);
        while (cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex("weathercode"));
        }
        return null;
    }
}
