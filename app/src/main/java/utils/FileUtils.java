package utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import fenping.szlt.com.usbhotel.AD_data;

public class FileUtils {

    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    //重点理解，这是一个递归方法，会不断来回调用本身，但是所有获得的数据都会存放在集合files里面
    public static void longErgodic(File file, List files) {

        //.listFiles()方法的使用
        //把文件夹的所有文件（包括文件和文件名）都放在一个文件类的数组里面
        File[] fillArr = file.listFiles();

        //如果是一个空的文件夹
        if (fillArr == null) {
            //后面的不执行，直接返回
            return;
        }

        //如果文件夹有内容,遍历里面的所有文件（包括文件夹和文件），都添加到集合里面
        for (File file2 : fillArr) {

            //如果只是想要里面的文件或者文件夹或者某些固定格式的文件可以判断下再添加
            if (!file2.isDirectory()) {

                files.add(file2.getAbsolutePath());
            }

            //添加到集合后，在来判断是否是文件夹，再遍历里面的所有文件
            //方法的递归
            longErgodic(file2, files);
        }
    }

    public static String getSDPath() {

        File sdDir = null;

        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在

        if (sdCardExist) {

            sdDir = Environment.getExternalStorageDirectory();//获取跟目录

        }

        return sdDir.toString();
    }

    public static String getSDADPath() {

        return getSDPath() + File.separator + "AD";

    }

    public static String getDestPath(String url) {
        String substring = "";
        if (!Tools.isEmpty(url)) {

            substring = url.substring(url.lastIndexOf("/") + 1);
        }
        File file = new File(getSDADPath());
        if (!file.exists()) {
            file.mkdirs();
        }

        return file.getAbsolutePath() + File.separator + substring;
    }

    public static void copyFile(String sourceFile, String targetFile) {
        //创建对象
        FileReader fr = null;
        FileWriter fw = null;
        try {
            fr = new FileReader(sourceFile);
            fw = new FileWriter(targetFile);
        //循环读和循环写
            int len = 0;
            while ((len = fr.read()) != -1) {
                fw.write((char) len);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.i("http", "copy失败");
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }
}
