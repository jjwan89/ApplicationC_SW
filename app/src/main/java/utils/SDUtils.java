package utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SDUtils {

    public static  String getSDPath(){

        File sdDir = null;

        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在

        if(sdCardExist) {

            sdDir = Environment.getExternalStorageDirectory();//获取跟目录

        }

        return sdDir.toString();
    }

    /**
     *
     * @param type  1: 左边第一个 2 大框 3：酒店
     * @return
     */
    public static  String getADPath(int type){

        String path ="";

        String sdPath = getSDPath();

        if(!Tools.isEmpty(sdPath)){

            File file;

            if(type==2){
                file = new File(sdPath + File.separator + "AD/ad2");
            }else if(type==3){
                file = new File(sdPath + File.separator + "AD/ad3");
            }else{
                file = new File(sdPath + File.separator + "AD/ad1");
            }
            if(file.isDirectory()){
                return file.getAbsolutePath();
            }

        }

        return path;
    }
    /**
     *
     * @param type  1: 滚动字幕 2 logo 3：中间框ad,4:酒店
     * @return
     */
    public static List<File> getADPathList(int type) {
        Log.i("ad","type=="+type);

        List<File> files1 = new ArrayList<>();

        String sdPath = getSDPath();

        if(!Tools.isEmpty(sdPath)){

            File file;

            if(type==2){
                file = new File(sdPath + File.separator + "AD/logo");
            }else if(type==3){
                file = new File(sdPath + File.separator + "AD/ad");
            }else if(type==4){
                file = new File(sdPath + File.separator + "AD/hotel");
            }else{
                file = new File(sdPath + File.separator + "AD/scroll");
            }
            Log.i("kil","file=="+file.getAbsolutePath());
            if(file.isDirectory()){
                
                File[] files = file.listFiles();
                
                if(!Tools.isEmpty(files)){

                    for (File fle: files) {

                        files1.add(fle);
                    }

                }

            }

        }
        return files1;
        
    }
}
