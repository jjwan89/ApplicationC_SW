package fenping.szlt.com.usbhotel;

import com.baidu.mapapi.common.AppTools;

import java.util.List;

import utils.ApkUtils;
import utils.Tools;

public class Const {//sw123456
    public static  String  path = "/system/usr/config_apk.txt";
    public static List<String> pkgs = ApkUtils.readFileByLines(path);
    public static int PIC1_TIME=5000;
    public static int DATA_REFRESH=1;
    //public static  String  Url = "http://192.168.2.185:8080/split/issue/getlsyJson?STB_mac="+ 21;
   // public static  String  Url = "http://sztripsky.com/lsyTEMP/issue/getlsyJson?STB_mac="+ Tools.getMac();
    public static String Url="http://www.sevazs.com/split/issue/getlsyJson?STB_mac="+Tools.getMac();
    public static String  TEXT_TYPE="text";
    public static String  OPEN_VIDEO_TYPE="openvideo";
    public static String  PIC_AND_VIDEO="picandvideo";
    public static String  APK0="apk0";
    public static String SCROLL_TYPE_DEL="有光就有希望，有尚为就有照明";
    //sp 里面的标识
    public static String  OLD_DATA="olddata";
    public static String  live="live";
    public static String  vod="vod";
    public static String  music="music";
    public static int   AD1_IMG= R.drawable.ad_def;
    public static int   FILE_DOWN= 10000;
    public static int  show = 2;
    public static int  disshow = 3;

    public static  int[] color={
            R.color.item_1,R.color.item_2,R.color.item_3,R.color.item_4,R.color.item_5,R.color.item_6,
            R.color.item_7,R.color.item_8,R.color.item_9,R.color.item_10,R.color.item_11,R.color.item_12,
            R.color.item_13,R.color.item_14,R.color.item_15,R.color.item_16,R.color.item_17,R.color.item_18
    };

    public static String[]  apps = {
            "allapp",//尚为风采
            "com.ktcp.video",//影视
            "allapp",//尚为商城
            "com.boll.edu",//教育
            "com.tencent.qqmusictv",//音乐
            "com.tencent.karaoketv",//欢唱k歌
            "com.zxpad.smarthic.app",//智慧家
            "allapp",//所有应用
            "com.android.tv.settings",//设置
            "com.yunos.tv.defensor"//清理
    };
    public static String openVideoPath="system/media/bootanimation.ts";
    public static String openPath="/data/local/bootanimation.ts";
}
