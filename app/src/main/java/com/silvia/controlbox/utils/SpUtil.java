package com.silvia.controlbox.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;


public class SpUtil {
    /**
     * 保存的文件名
     */
    private static SharedPreferences sp;

    /**
     * 保存boolean
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        if (sp == null) {
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }


    //保存数组
    public static boolean saveArray(Context context,String key, List<String> dataList) {
        if (sp==null){
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor mEdit = sp.edit();
        //先移除旧的数组
        int oldSize=sp.getInt(key,0);
        for(int i = 0; i < oldSize; i++) {
            mEdit.remove(key+ i);
        }
        //再增加新的数组
        mEdit.putInt(key, dataList.size());
        for(int i = 0; i < dataList.size(); i++) {
            mEdit.putString(key+ i, dataList.get(i));
        }
        return mEdit.commit();
    }

    //删除数组
    public static ArrayList<String> deleteArray(Context context,String key){
        if (sp==null){
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor mEdit = sp.edit();
        ArrayList<String> dataList=new ArrayList<>();
        int size=sp.getInt(key,0);
        for (int i=0;i<size;i++){
            mEdit.remove(key+i);
            dataList.remove(sp.getString(key+i,null));
        }
        return dataList;
    }
    //读取数组
    public static ArrayList<String> readArray(Context context,String key) {
        if (sp==null){
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        ArrayList<String> dataList=new ArrayList<>();
        dataList.clear();
        int size = sp.getInt(key, 0);
        for(int i = 0; i < size; i++) {
            dataList.add(sp.getString(key+ i, null));
        }
        return dataList;
    }



    public static ArrayList<String> getArray(Context context,String key){
        if (sp==null){
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        ArrayList<String> dataList=new ArrayList<>();
        dataList.clear();
        int size = sp.getInt(key, 0);
        for(int i = 0; i < size; i++) {
            dataList.add(sp.getString("Silvia_"+ i, null));
        }
        return dataList;
    }

    /**
     * 取出boolean
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * 保存String
     */
    public static void saveString(Context context, String key, String value) {
        if (sp == null) {
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    /**
     * 取出String
     */
    public static String getString(Context context, String key, String defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defaultValue);
    }

    /**
     * 保存int
     */
    public static void saveInt(Context context, String key, int value) {
        if (sp == null) {
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }

    /**
     * 取出int
     */
    public static int getInt(Context context, String key, int defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("spinfo", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defaultValue);
    }
}
