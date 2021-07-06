package utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;

import fenping.szlt.com.usbhotel.Const;

public class SettingUtils {
    public static String getOldData(Context mContext) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Const.OLD_DATA, "");
    }
    public static void setOldData(Context mContext,String  data) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(Const.OLD_DATA,data).commit();
    }
    //live
    public static String getLive(Context mContext) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Const.live,"");
    }
    public static void setLive(Context mContext,String  data) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(Const.live,data).commit();
    }
    //vod
    public static String getVod(Context mContext) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Const.vod, "");
    }
    public static void setVod(Context mContext,String  data) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(Const.vod,data).commit();
    }
    //music
    //vod
    public static String getMusic(Context mContext) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Const.music, "");
    }
    public static void setMusic(Context mContext,String  data) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(Const.music,data).commit();
    }

    public static void setText_bottom_upload(Context mContext, List<String> list) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        String s = "";
        if (!Tools.isEmpty(list) && list.size() != 0) {
            s = JSON.toJSONString(list);
        }
        mSharedPreferences.edit().putString("text_bottom_upload", s).commit();
    }
    public static List<String>  getText_bottom_upload(Context context) {
        List<String> list=new ArrayList<String >();
        SharedPreferences mSharedPreferences = context.getSharedPreferences(
                "Config", Context.MODE_PRIVATE);
        String pop_picture_path = mSharedPreferences.getString("text_bottom_upload", "");
        if(!Tools.isEmpty(pop_picture_path)){
            list = JSON.parseObject(pop_picture_path, new TypeReference<List<String>>() {});
        }
        return list;
    }
}
