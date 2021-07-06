package utils;

import android.content.Context;

import java.util.Calendar;


public class TimeUtils {
    /**
     * 获取时分
     * @return
     */
    public  static  String  getTime(Context context,boolean flag){
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH)+1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //获取系统时间
        //小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);
        //秒
        int second = calendar.get(Calendar.SECOND);
        //星期
        int wek=calendar.get(Calendar.DAY_OF_WEEK);
        if(!flag){
            //20:28  2011 年 1 月 1日
            //return hour+":"+(minute<=9?"0"+minute:minute)+"="+getMonth(month,context,flag)+" "+day+","+year;
            return hour+":"+(minute<=9?"0"+minute:minute);
        }else{
            //
            //return hour+":"+(minute<=9?"0"+minute:minute)+"="+year+"年"+getMonth(month,context,flag)+day;
            return hour+":"+(minute<=9?"0"+minute:minute);
        }
    }

    public static String getMonth(int month,Context context,boolean type) {
        String mth="";
        switch (month){
            case 1:
                if(type){
                    mth ="1月";
                }else{
                    mth ="January";
                }
                break;
            case 2:
                if(type){
                    mth ="2月";
                }else{
                    mth ="February";
                }
                break;
            case 3:
                if(type){
                    mth ="3月";
                }else{
                    mth ="March";
                }
                break;
            case 4:
                if(type){
                    mth ="4月";
                }else{
                    mth ="April";
                }
                break;
            case 5:
                if(type){
                    mth ="5月";
                }else{
                    mth ="May";
                }
                break;
            case 6:
                if(type){
                    mth ="6月";
                }else{
                    mth ="June";
                }
                break;
            case 7:
                if(type){
                    mth ="7月";
                }else{
                    mth ="July";
                }
                break;
            case 8:
                if(type){
                    mth ="8月";
                }else{
                    mth ="August";
                }
                break;
            case 9:
                if(type){
                    mth ="9月";
                }else{
                    mth ="Sep";//September
                }
                break;
            case 10:
                if(type){
                    mth ="10月";
                }else{
                    mth ="Oct";//October
                }
                break;
            case 11:
                if(type){
                    mth ="11月";
                }else{
                    mth ="Nov";//November
                }
                break;
            case 12:
                if(type){
                    mth ="12月";
                }else{
                    mth ="Dec";//December
                }
                break;

        }
        return mth;
    }

    public static int getMinus() {
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);
        return  hour*60+minute;
    }
    //
    //2月：February
    //
    //3月：March
    //
    //4月：April
    //
    //5月：May
    //
    //6月：June
    //
    //7月：July
    //
    //8月：August
    //
    //9月：September
    //
    //10月：October
    //
    //11月：November
    //
    //12月：December
    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     * @return
     *//*
    public static String getWeek(int wek,Context context) {
        String Week="";
        if (wek == 1) {
            Week = context.getResources().getString(R.string.sun);
        }
        if (wek == 2) {
            Week = context.getResources().getString(R.string.mon);
        }
        if (wek == 3) {
            Week = context.getResources().getString(R.string.tues);
        }
        if (wek == 4) {
            Week = context.getResources().getString(R.string.wed);
        }
        if (wek == 5) {
            Week = context.getResources().getString(R.string.thur);
        }
        if (wek == 6) {
            Week = context.getResources().getString(R.string.fri);
        }
        if (wek == 7) {
            Week = context.getResources().getString(R.string.satu);
        }
        return Week;
    }*/

}
