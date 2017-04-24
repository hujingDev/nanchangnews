package com.hujing.nanchangnews.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by acer on 2017/4/10.
 */

public class SpUtils {
    private final static String CONFIG ="config";
    public final static String IS_FIRST_USE ="is_first_use";
    public final static String SELECT_ITEM ="select_item";
    public final static String SELECT_CITY ="select_city";
    public final static String BEING_PIC ="being_pic";
    private static SharedPreferences sharedPreferences;

    public static void setBoolean(Context context,boolean isFirstUse,String dataType){
        if (sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(dataType,isFirstUse);
        editor.commit();
    }
    public static boolean getBoolean(Context context, boolean defaultValue,String dataType){
        if (sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(dataType,defaultValue);
    }
    public static void setInt(Context context,int num,String string){
        if (sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(string,num);
        editor.commit();
    }
    public static int getInt(Context context, int defaultValue,String string){
        if (sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(string,defaultValue);
    }
    public static void setString(Context context,String json,String tag){
        if (sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(tag,json);
        editor.commit();
    }

    /**
     * @param context
     * @param tag
     * @return 无默认返回值
     */
    public static String getString(Context context ,String tag){
        if (sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(tag,null);
    }

    /**
     * @param context
     * @param tag
     * @param defaultValue
     * @return 有默认返回值
     */
    public static String getString(Context context ,String tag,String defaultValue){
        if (sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(tag,defaultValue);
    }
}
