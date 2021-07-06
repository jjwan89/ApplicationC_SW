package utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class Tools {
    public static boolean IsGetNetWork(Context context){

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService( Service.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //判断状态
        if (networkInfo != null){
            return networkInfo.isAvailable();
        }

        return false;
    }

    /**
     * 打开apk
     * @param context
     * @param packagename
     * @return
     */
    public static boolean openAPk(Context context,String packagename) {

        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packagename);
        if (intent == null) {
            return false;
        }else{
            context.startActivity(intent);
        }
        return true;
    }
    /**
     * 判断字符串是否为空
     *
     * @param obj 要判断的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (null == obj || "".equals(obj)) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isEmpty(List obj) {
        if (null == obj || "".equals(obj)||obj.size()==0) {
            return true;
        } else {
            return false;
        }
    }
    public static String[] img ={"jpg","png","jpeg","bmp","JPG","PNG","JPEG","BMP"};

    public static String[] text ={"txt"};

    public static String[] video ={"mp4","MP4","mkv","avi","mov","mp4","ts","asf","flv","pmp","rmvb","mpg","vob","wmv"};
    public static boolean isVideo(String path) {
        String substring = path.substring(path.lastIndexOf(".") + 1);
        return contain(video,substring);
    }
    public static boolean isPicture(String path) {
        String substring = path.substring(path.lastIndexOf(".") + 1);
        return contain(img,substring);
    }
    public static boolean contain(String[] s,String  ss){
        if(!isEmpty(s)){
            for (int i = 0; i <s.length ; i++) {
                if(s[i].equalsIgnoreCase(ss)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取xml数据
     *
     * @return result xml数据
     */
    public static String  getMac() {
        File file = new File("/sys/class/net/eth0/address");
        if(file.exists()){
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getPath()));
                StringBuffer stringBuffer = new StringBuffer();
                String s="";
                while(s!=null) {
                    if (!"".equals(s)) {

                        stringBuffer.append(s);

                    }
                    s=bufferedReader.readLine();
                }
                bufferedReader.close();
                return stringBuffer.toString().replace(":","");

            } catch (Exception e) {
                return null;
            }

        }
        return null;
    }

    /**
     * 通过apk文件得到apk包名
     * @param context
     * @param FilePath
     * @return
     */
    public static String getPackageName(Context context, String FilePath) {
        //String FilePath="*.apk";//输入APK地址
        PackageManager pm =context.getPackageManager();
        Log.i("ad", "pm="+pm);
        Log.i("ad", "FilePath="+FilePath);
        File file = new File(FilePath);
        if(!file.exists()){
            return "";
        }
        PackageInfo info = pm.getPackageArchiveInfo(FilePath, 0);
        if(!Tools.isEmpty(info)){
            Log.i("ad", "info不为null");

        }else{
            Log.i("ad", "info为null");
            if(file.exists())
                file.delete();
        }
        String packageName="";
        if(info != null){
            ApplicationInfo appInfo = info.applicationInfo;
            Log.i("ad", "appInfo=="+appInfo);
            packageName = appInfo.packageName;  //获取安装包名称
            Log.i("ad", "packageName=="+packageName);
            Log.i("Abel_Test", "包名是："+packageName);
            String version=info.versionName; //获取版本信息
            Log.i("Abel_Tes", "版本信息："+version);
        }else{
            Log.i("ad", "packageName=空字符串");
        }

        return packageName;
    }
    /**
     * 通过apk文件得到apk包名
     * @param context
     * @param FilePath
     * @return
     */
    public static int getApkVersion(Context context,String FilePath) {
        //String FilePath="*.apk";//输入APK地址

        PackageManager pm =context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(FilePath, PackageManager.GET_ACTIVITIES);
        String packageName="";
        if(info != null){
            ApplicationInfo appInfo = info.applicationInfo;
            packageName = appInfo.packageName;  //获取安装包名称
            Log.i("Abel_Test", "包名是："+packageName);
            String version=info.versionName; //获取版本信息
            Log.i("Abel_Tes", "版本信息："+version);
            int versionCode = info.versionCode;
            Log.i("Abel_Tes", "版本号："+versionCode);
            return versionCode;

        }
        return -1;
    }
    /**
     * 判断应用是否需要更新
     *
     * @return allApp 所有应用集合
     */
    public static boolean isNeedInstall(Context mContext,String destPath) {
        PackageManager packageManager = mContext.getPackageManager();
        String packageName = getPackageName(mContext, destPath);
        int apkVersion = getApkVersion(mContext, destPath);
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        Log.i("http","packageName=="+packageName);
        for (PackageInfo pInfo : installedPackages) {
            if(pInfo.packageName.equals(packageName)){
                if(pInfo.versionCode>=apkVersion){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 判断应用是否需要更新
     *
     * @return allApp 所有应用集合
     */
    public static boolean isNeedInstall(Context mContext,String filename,List<String> uninstall) {
        String packageName = Tools.getPackageName(mContext, filename);
        int apkVersion = Tools.getApkVersion(mContext, filename);
        if(Tools.isEmpty(uninstall)){
            if(uninstall.contains(packageName)){
                return false;
            }
        }

        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        Log.i("http","packageName=="+packageName);
        for (PackageInfo pInfo : installedPackages) {
            if(pInfo.packageName.equals(packageName)){

                if(pInfo.versionCode>=apkVersion){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断包名是否存在
     *
     * @return allApp 所有应用集合
     */
    public static boolean isAppExit(Context mContext,String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        Log.i("http","packageName=="+packageName);
        for (PackageInfo pInfo : installedPackages) {
            if(pInfo.packageName.equals(packageName)){
                return true;
            }
        }
        return false;
    }
}
