package fenping.szlt.com.usbhotel;

import android.animation.Animator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import utils.FileUtils;
import utils.SettingUtils;
import utils.TimeUtils;
import utils.Tools;

public class DataService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    private RequestParams requestParams;
    
    private Context appContext;
    private boolean isdown;
    private boolean isHand=true;
    private AD_data mAd;
    private Callback.Cancelable download;
    private WindowManager windowManager;
    private View inflate;
    private RecyclerView down_recycle;
    private WindowManager.LayoutParams layoutParams;
    private ArrayList<DownItem> dataset;
    private MyAdapter adapter;
    private ArrayList<String> uninstallApk= new ArrayList<String>();
    private boolean isUnInstallApk=true;
    private ArrayList<String> installApk = new ArrayList<String>();
    private boolean isIntsallApk=true;

    public DataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ytest","DataService ----onCreate");
        appContext =getApplicationContext();
        dataset =new ArrayList<DownItem>();
        mHander=new MyHandler(appContext);
        startRefreshData();
        showPopPicture();

    }

    private void showPopPicture() {
        if(!Tools.isEmpty(windowManager)){
            if(inflate.isAttachedToWindow()){
                windowManager.removeView(inflate);
            }
            windowManager = null;
            inflate=null;
        }
        windowManager = (WindowManager) appContext.getSystemService(WINDOW_SERVICE);
        inflate = View.inflate(appContext, R.layout.down_list, null);
        inflate.animate().scaleX(0f).setDuration(0).start();
        down_recycle = (RecyclerView)inflate.findViewById(R.id.down_recycle);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        down_recycle.setLayoutManager(layoutManager);
        createData(down_recycle,R.layout.itme_down);

        layoutParams = new WindowManager.LayoutParams();
        DisplayMetrics displayMetrics = appContext.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        layoutParams.gravity = Gravity.CENTER_VERTICAL| Gravity.RIGHT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.width = widthPixels*3/10;
        layoutParams.height = heightPixels;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//TYPE_SYSTEM_OVERLAY
        windowManager.addView(inflate, layoutParams);
    }
    private void createData(RecyclerView recyclerView,int id) {
        // 创建Adapter，并指定数据集
        adapter = new MyAdapter(this, dataset,id);
        // 设置Adapter
        recyclerView.setAdapter(adapter);
        
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public static Handler mHander =null;
    class MyHandler extends Handler {
        // 弱引用 ，防止内存泄露
        private WeakReference<Context> weakReference;
        public MyHandler(Context context){
            weakReference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 通过  软引用  看能否得到activity示例
            Context context = weakReference.get();
            // 防止内存泄露
            if (context != null) {
                // 如果当前Activity，进行UI的更新
                if(msg.what==1){
                    Bundle data = msg.getData();
                    String load = data.getString("load");
                    String url = data.getString("url");
                    changeDownData(url,load);
                }else if(msg.what==2){
                    Log.i("key", "接受消息");
                    inflate.setPivotX(inflate.getWidth());
                    inflate.animate().scaleX(1f).setDuration(500).start();
                }else if(msg.what==3){
                    inflate.animate().scaleX(0f).setDuration(1000).start();
                }
            }
        }
    }
    private void startRefreshData() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("ytest","isHand=="+isHand);
                if(isHand){
                    Log.i("ytest","访问数据");
                    isHand=false;
                    getSourceData();
                }
            }
        };
        timer.schedule(timerTask, 5000, 10 * 1000);
    }

    private void getSourceData() {
        requestParams = new RequestParams(Const.Url);
        Log.i("ytest", "getSourceData=="+Const.Url );
        requestParams.setConnectTimeout(20 * 1000);
        x.http().request(HttpMethod.GET, requestParams, new Callback.CommonCallback<String>() {
            
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(String result) {

                Log.d("ytest", "请求成功2222result onSuccess" );
                //拿到数据
                String oldData = SettingUtils.getOldData(appContext);
                //对比数据
                if(Tools.isEmpty(oldData) || !result.equals(oldData)){
                    Log.d("ytest", " onSuccess 01" );
                    //需要更新 判断下载状态
                   // Log.i("http", "数据需要更新" );
                    //Log.i("http", "isdown=="+isdown);
                    //保存数据
                    SettingUtils.setOldData(appContext,result);
                    //1:通知UI更新数据
                   // Log.i("http", "通知界面更新数据" );
                    HandleHelp.sendEmptyMessage(Const.DATA_REFRESH,0,null);
                    if(isdown){
                        Log.d("ytest", " onSuccess 01-1" );
                        //Log.i("inapk","更新数据");
                        download.cancel();
                        isHand =true;
                    }else{
                        //2:校验数据
                       // Log.i("http", "校验数据下载情况" );
                        Log.d("ytest", " onSuccess 01-2" );
                        handDatas();
                    }
                }else{
                    Log.d("ytest", " onSuccess 02" );
                   // Log.i("http", "数据不需要更新" );
                    if(!isdown){
                       // Log.i("http", "数据校验" );
                        Log.d("ytest", " onSuccess 02-1" );
                        handDatas();
                    }else{
                        Log.d("ytest", " onSuccess 02-2" );
                        isHand =true;
                      //  Log.i("http", "数据正在校验，无需继续校验" );
                    }

                }
                
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("http", "请求错误ex==" + ex);
                ex.printStackTrace();
                isHand =true;
               // Log.i("inapk","下载错误");
                isIntsallApk=true;
                isUnInstallApk=true;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("http", "请求取消");
            }

            @Override
            public void onFinished() {
                Log.i("http", "请求完成");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handDatas() {

        //清除目录
        //Log.i("http", "处理数据");
        
        String oldData = SettingUtils.getOldData(appContext);

        mAd = JSON.parseObject(oldData, AD_data.class);
        
        List<AD_data.AdBean> ad = mAd.getAd();
        Log.d("ytest","handDatas ad"+ad);

        //Log.i("http","原始大小=="+ad.size());

        //过滤文字
        for (int i = ad.size()-1; i >=0 ; i--) {
            String type = ad.get(i).getType();
            if(type.equals(Const.TEXT_TYPE)){
                ad.remove(i);
            }
        }

//        Log.i("http","过滤之后大小=="+ad.size());
//        Log.i("down","过滤之后大小=="+dataset.size());
//        Log.i("down","处理之后下载=="+dataset.size());
//        Log.i("http","处理之后下载url大小=="+ad.size());

        removeDuplicate(ad);
        Log.d("ytest","handDatas ad.size"+ad.size());
        //排序
        ad.sort(new Comparator<AD_data.AdBean>() {
            @Override
            public int compare(AD_data.AdBean o1, AD_data.AdBean o2) {
                return Long.parseLong(o1.getSize())-Long.parseLong(o2.getSize())>0?1:-1;
            }
        });
        //删除sd目录中过时的数据
        List<String> strings = new ArrayList<>();

        FileUtils.longErgodic(new File(FileUtils.getSDADPath()),strings);
        Log.d("ytest","handDatas ad.size222"+ad.size());
        for(int i =strings.size()-1; i >=0 ; i--) {
            String s = strings.get(i);
            String realname = s.substring(s.lastIndexOf("/") + 1);
            boolean isdel=true;
            for (int j = ad.size()-1; j >=0 ; j--) {
                String url = ad.get(j).getUrl();
                String filename = url.substring(url.lastIndexOf("/") + 1);
                String tempname = filename + ".tmp";
                if(realname.equals(filename)||realname.equals(tempname)){
                   isdel =false;
                }
            }
            if(isdel){
                File file = new File(s);
                if(file.exists()){
                    file.delete();
                }
            }
        }
        Log.d("ytest","handDatas ad.size223333332");
        //检测下载进度
        dataset.clear();
        for (int i = 0; i <ad.size() ; i++) {
            DownItem downItem = new DownItem();
            downItem.setUrl(ad.get(i).getUrl());
            downItem.setLoad("0%");
            dataset.add(downItem);
        }
        adapter.setData(dataset);
        adapter.notifyDataSetChanged();

        if(Tools.isEmpty(ad)){
            Log.d("ytest","handDatas()  en2222222222d");
            isHand = true;
            
            isdown = false;
            
            return;
        }

        isdown =true;
        isHand =true;
        Log.d("ytest","handDatas()  end");
        DownList(ad);
    }



    public   static   void   removeDuplicate(List<AD_data.AdBean> list)  {
        for  ( int  i  =   0 ; i  <  list.size()  -   1 ; i ++ )  {
            for  ( int  j  =  list.size()  -   1 ; j  >  i; j -- )  {
                if  (list.get(j).getUrl().equals(list.get(i).getUrl()))  {
                    list.remove(j);
                }
            }
        }

    }

    private void DownList(final List<AD_data.AdBean> ad) {
        Log.d("ytest","DownList---------");
        if(Tools.isEmpty(ad)){
            isdown = false;
            return;
        }
        final String url = ad.get(0).getUrl();
        final String type = ad.get(0).getType();
        final String destPath = type.equals(Const.OPEN_VIDEO_TYPE)?Const.openPath:FileUtils.getDestPath(url);

        if(type.equals(Const.OPEN_VIDEO_TYPE)){
            changePermission("data/local");
            changePermission(destPath);
        }
        if(new File(destPath).exists() && new File(destPath).length()==Long.parseLong(ad.get(0).getSize())){
            for (int i = 0; i <dataset.size() ; i++) {
                if(dataset.get(i).getUrl().equals(url)){
                    dataset.get(i).setLoad("100%");
                }
            }
            adapter.notifyDataSetChanged();
            if(ad.get(0).getType().equals(Const.APK0) ){
                //String destPath = FileUtils.getDestPath(url);
                Log.i("http", "检测是否需要安装");
                if(Tools.isNeedInstall(appContext,destPath)){
                    Log.i("http", "需要安装");
                    CmdUtils.installSliceApk(destPath);
                }
            }
            ad.remove(0);
            DownList(ad);
            return;
        }
        RequestParams requestParams = new RequestParams();
        requestParams.setUri(url);
        requestParams.setAutoRename(false);
        requestParams.setAutoResume(true);
        Log.d("ytest","destPath:"+destPath);
        requestParams.setSaveFilePath(destPath);
        download = x.http().request(HttpMethod.GET, requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
                //Log.i("http", "onWaiting");
            }

            @Override
            public void onStarted() {
                //Log.i("http", "onStarted");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //Log.i("http", "onLoading");
                String s = current * 100 / total + "%";
               // Log.i("http", url+"\t"+s);
                Message obtain = Message.obtain();
                obtain.what=1;
                Bundle bundle = new Bundle();
                bundle.putString("load",s);
                bundle.putString("url",url);
                obtain.setData(bundle);
                mHander.sendMessage(obtain);
               
            }

            @Override
            public void onSuccess(File result) {
                //Log.i("http", "onSuccess=="+ad.get(0).getType());
                AD_data.AdBean tmp = ad.get(0);
                if(tmp.getType().equals(Const.OPEN_VIDEO_TYPE)){
                    Log.i("zyl", "zyl=="+result.getAbsolutePath());
                   changePermission(result.getAbsolutePath());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("http", "onError  ex=="+ex);

            }

            @Override
            public void onCancelled(CancelledException cex) {
               // Log.i("http", "onCancelled");
                isdown =false;
            }

            @Override
            public void onFinished()
            {
               // Log.i("http", "onFinished");
                handownComplate(ad);
            }
        });

    }

    private void changePermission(String path) {
        try {
            String command = "chmod 777 " + path;
            Process p = Runtime.getRuntime().exec("su");
            Log.i("zyl", "command = " + command);
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
            Log.i("zyl", "proc = " + proc);
        } catch (IOException e) {
            Log.i("zyl","chmod fail!!!!");
            e.printStackTrace();
        }
    }

    private void changeDownData(String url, String s) {
        for (int i = 0; i <dataset.size() ; i++) {
            if(dataset.get(i).getUrl().equals(url)){
                dataset.get(i).setLoad(s);
            }
        }
        adapter.setData(dataset);
    }


    private void handownComplate(List<AD_data.AdBean> ad) {
        AD_data.AdBean tmp = ad.get(0);
        if(tmp.getType().equals(Const.APK0)){
            String destPath = FileUtils.getDestPath(tmp.getUrl());
            CmdUtils.installSliceApk(destPath);
        }else if(tmp.getType().equals(Const.PIC_AND_VIDEO)){
            Message obtain = Message.obtain();
            obtain.what = Const.FILE_DOWN;
            obtain.obj = tmp;
            MainActivity.mMainActivityHandler.removeMessages(Const.FILE_DOWN);
            MainActivity.mMainActivityHandler.sendMessage(obtain);
        }
        ad.remove(0);
        if(!Tools.isEmpty(ad)){
            DownList(ad);
        }else{
            //清除目录
            isdown=false;
        }
    }
}
