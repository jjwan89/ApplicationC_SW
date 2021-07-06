package utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static utils.Tools.img;
import static utils.Tools.video;

public class Utils {

    private static boolean flag =true;

    public static void showToast(String s, Context mContext){
        Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
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
     * 删除文件夹
     * @return
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除指定文件夹下面所有文件
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }



    // 复制文件
    public static boolean copyFile(File oldfile,File targetFile) {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = null;
        try {
            input = new FileInputStream(oldfile);

            BufferedInputStream inBuff=new BufferedInputStream(input);

            // 新建文件输出流并对它进行缓冲
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff=new BufferedOutputStream(output);

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len =inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
            //关闭流
            inBuff.close();
            outBuff.close();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("zh","e=="+e);
            Utils.showLog("e=="+e);
            return false;
        }
        return true;

    }



    public static File getSD() {
        File sdDir= null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist){
            //获取根节点
             sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir;
    }

    /**
     * 拿到Sd卡总数据
     */
    public  static ArrayList<File> getSDAllData(){
        ArrayList<File> files1 = new ArrayList<>();
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist) {
            //获取根节点
            File sdDir = Environment.getExternalStorageDirectory();
            String absolutePath = sdDir.getAbsolutePath();
            //检查 对应文件mp3 video picture
            String[] list = sdDir.list();
            for (int i = 0; i < list.length; i++) {

                if (list[i].equals("mp3") || list[i].equals("video") || list[i].equals("picture")) {
                    File file = new File(absolutePath + "/" + list[i]);
                    if (file.exists()) {
                        //拿到所有的文件
                        File[] files = file.listFiles();
                        for (File fi : files) {
                            files1.add(fi);
                        }
                    }
                }
            }
        }
        return files1;
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

    public static void  showLog(String  str) {
        if(flag){
            Log.i("service",str);
        }
    }
    /**
     * 读取U 盘路径，返回路径集合
     * @param con
     * @return
     */
    public static List<String> getUSBPaths(Context con) {//反射获取路径
        String[] paths = null;
        List data = new ArrayList<String>();    // include sd and usb devices
        StorageManager storageManager = (StorageManager) con.getSystemService(Context.STORAGE_SERVICE);
   /*     try {
            paths=(String[])StorageManager.class.getMethod("getVolumePaths",null).invoke(storageManager,null);
            for (String path : paths) {
                String state = (String) StorageManager.class.getMethod("getVolumeState", String.class).invoke(storageManager, path);
                if (state.equals(Environment.MEDIA_MOUNTED) && !path.contains("emulated")) {
                    data.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return data;
    }


    /**
     * 难道文件夹的大小
     * @param file
     * @return
     */
    public static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    //重点理解，这是一个递归方法，会不断来回调用本身，但是所有获得的数据都会存放在集合files里面
    public static void longErgodic(File file, List files) {

        //.listFiles()方法的使用
        //把文件夹的所有文件（包括文件和文件名）都放在一个文件类的数组里面
        File[] fillArr=file.listFiles();

        //如果是一个空的文件夹
        if (fillArr==null) {
            //后面的不执行，直接返回
            return;
        }

        //如果文件夹有内容,遍历里面的所有文件（包括文件夹和文件），都添加到集合里面
        for (File file2 : fillArr) {

            //如果只是想要里面的文件或者文件夹或者某些固定格式的文件可以判断下再添加
            if(!file2.isDirectory()){

                files.add(file2.getAbsolutePath());
            }

            //添加到集合后，在来判断是否是文件夹，再遍历里面的所有文件
            //方法的递归
            longErgodic(file2, files);
        }
    }

    //重点理解，这是一个递归方法，会不断来回调用本身，但是所有获得的数据都会存放在集合files里面
    public static void deleteFile(File file) {
        ArrayList<String> files = new ArrayList<>();
        longErgodic(file,files);
        boolean isContinu = false;
        for (String fe:files) {

            if(fe.endsWith(".new")){
                isContinu =true ;
            }
        }
        if(!isContinu){
            return;
        }
        Log.i("del","isContinu"+isContinu);
        //.listFiles()方法的使用
        //把文件夹的所有文件（包括文件和文件名）都放在一个文件类的数组里面
        File[] fillArr=file.listFiles();

        //如果是一个空的文件夹
        if (fillArr==null) {
            //后面的不执行，直接返回
            return;
        }

        //如果文件夹有内容,遍历里面的所有文件（包括文件夹和文件），都添加到集合里面
        for (File file2 : fillArr) {

            //如果只是想要里面的文件或者文件夹或者某些固定格式的文件可以判断下再添加
            if(!file2.isDirectory()){

                String absolutePath = file2.getAbsolutePath();

                Log.i("del",absolutePath);
                boolean b = !absolutePath.endsWith(".new");


                Log.i("del","b"+b);

                if(b){

                    Log.i("del",absolutePath);

                    file2.delete();

                }else{

                    String substring = absolutePath.substring(0, absolutePath.lastIndexOf(".new"));

                    File file1 = new File(substring);
                    String absolutePath1 = file1.getAbsolutePath();
                    String substring1 = absolutePath1.substring(absolutePath1.lastIndexOf(".") + 1);
                    file2.renameTo(file1);
                    Log.i("del",substring1);
                    boolean contain = contain(video, substring1);
                    Log.i("del","是视频=="+contain);
                    boolean imgs = contain(img, substring1);
                    Log.i("del","是图片=="+imgs);
                    boolean text = contain(Tools.text, substring1);
                    Log.i("del","是图片=="+imgs);
                    if(!contain &&!imgs&&!text){
                        file1.delete();
                    }
                }

            }

            //添加到集合后，在来判断是否是文件夹，再遍历里面的所有文件
            //方法的递归
            deleteFile(file2);
        }
    }

    public interface callBack{
        void start(long total);
        void copy(long curtotal);
        void complate(boolean result, long total);
        void erro(long total);
    }

    // 复制文件
    public static boolean copyFile(File oldfile,File targetFile,callBack mcallback) {
        // 新建文件输入流并对它进行缓冲
        String absolutePath = targetFile.getAbsolutePath();
        String substring = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        File file = new File(substring);
        if(!file.exists()){
            file.mkdir();
        }
        FileInputStream input = null;
        long length = oldfile.length();
        try {
            mcallback.start(length);
            input = new FileInputStream(oldfile);

            BufferedInputStream inBuff=new BufferedInputStream(input);

            // 新建文件输出流并对它进行缓冲
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff=new BufferedOutputStream(output);

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len =inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
                mcallback.copy(len);
            }
            outBuff.flush();
            //关闭流
            inBuff.close();
            outBuff.close();
            output.close();
            input.close();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.i("zh","e=="+e);
            Utils.showLog("e=="+e);
            mcallback.erro(length);
            return false;
        }
        mcallback.complate(true,length);
        return true;

    }

    //判断当前界面显示的是哪个Activity
    public static String getTopActivity(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        Log.d("Chunna.zheng", "pkg:"+cn.getPackageName());//包名
        Log.d("Chunna.zheng", "cls:"+cn.getClassName());//包名加类名
        return cn.getClassName();
    }

    //读写
    /**
     * 将内容写入sd卡中
     * @param content  待写入的内容
     * @throws IOException
     */
    public static void writeConfig(Context context, String content) throws IOException {

        //判断是否存在可用的的SD Card
        String filename =  "data/data/"+context.getPackageName()+"/config.txt";
        File file = new File(filename);
        if(file.exists())
            file.delete();
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(filename);

        outputStream.write(content.getBytes());
        FileDescriptor fd = outputStream.getFD();
        fd.sync();
        outputStream.close();
    }

    public static String readConfig(Context context) throws IOException {
        StringBuilder sb = new StringBuilder("");
        String filename ="data/data/"+context.getPackageName()+"/config.txt";
        if(!new File(filename).exists()){
            return "";
        }
            //打开文件输入流
            FileInputStream inputStream = new FileInputStream(filename);

            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while(len > 0){
                sb.append(new String(buffer,0,len));

                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            inputStream.close();

        return sb.toString();
    }
    //Hardware: Rockchip RK3229
    public static String getRKVersion() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {
            //读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    //查找到序列号所在行
                    if (str.indexOf("Hardware") > -1) {
                        //提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1,
                                str.length());
                        //去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    //文件结尾
                    break;
                }
            }
        } catch (Exception ex) {
            //赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }

    public static boolean readConfig(String str) {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            //Method[] method =  classType.getDeclaredMethods();
            //for(int i = 0 ;i<method.length;i++){
            //　　Log.i(TAG, "method="+method[i].getName());
            //}
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            String value = (String) getMethod.invoke(classType, new Object[]{str});
            Log.i("zj", value);
            return value!=null?true:false;
        } catch (Exception e) {
            Log.e("zj", e.getMessage(),e);
        }
        return false;
    }
}
