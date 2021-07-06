package fenping.szlt.com.usbhotel;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.evilbinary.tv.widget.BorderEffect;
import org.evilbinary.tv.widget.BorderView;

import java.util.ArrayList;
import java.util.List;

import View.NetView;
import View.VPView;
import View.TimeView;
import View.ScrollTextView;
import fenping.szlt.com.usbhotel.recyc.DemoRecyclerViewActivity;
import utils.SettingUtils;
import utils.Tools;

public class MainActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "MainActivity";
    private BorderView border;
    private RelativeLayout main;
    public  static  MainActivityHandler mMainActivityHandler = null;
    private VPView picAndVideo;
    private ScrollTextView scroll;
//    private TimeView time;
    private TextClock time;
    private boolean isstart;
    private String temp="";
    private ViewGroup.LayoutParams layoutParams1;
    boolean isStop;
    ImageView app_1;
    ImageView app_2;
    ImageView app_3;
    ImageView app_4;
    ImageView app_5;
    ImageView app_6;
    ImageView app_7;
    ImageView app_8;
    NetView net;
    ArrayList<ImageView> imageViews;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getWindow() == null)    return true;
        if(keyCode==23){
            //showMac();
        }else if (keyCode == 8 || keyCode == 9 || keyCode == 10|| keyCode == 13) {
            Log.i("key", "keyCode==" + keyCode);
            mMainActivityHandler.removeMessages(2);
            mMainActivityHandler.sendEmptyMessageDelayed(2, 2000);
            temp += (keyCode - 7);
            Log.i("key", "temp==" + temp);
            Log.i("key", "temp.length()==" + temp.length());
            if (temp.length() >= 3 && "123".equals(temp)) {
                temp = "";
                showMac();
            }else if(temp.length() >= 3 && "163".equals(temp)){
                Log.i("key", "发送消息");
                DataService.mHander.removeMessages(Const.show);
                DataService.mHander.sendEmptyMessage(Const.show);
            }else if(temp.length() >= 3 && "361".equals(temp)){
                Log.i("key", "发送消息");
                DataService.mHander.removeMessages(Const.disshow);
                DataService.mHander.sendEmptyMessage(Const.disshow);
            }else if(temp.length() >= 4 && "1221".equals(temp)){
                Log.i("key", "发送消息");
                startActivity(new Intent(this, DemoRecyclerViewActivity.class));
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private void showMac() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();//创建对话框
        dialog.setIcon(R.mipmap.ic_launcher);//设置对话框icon
        dialog.setTitle("MAC");//设置对话框标题
        dialog.setMessage("当前机器MAC为" + Tools.getMac());//设置文字显示内容
        //分别设置三个button
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//关闭对话框
            }
        });
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this,DataService.class));
        isstart =true;
        initView();
        initBorder();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (getWindow() == null)    return ;
        if(isstart && hasFocus){
           isstart =false;
           picAndVideo.requestFocus();
           border.getEffect().onFocusChanged(picAndVideo,picAndVideo,picAndVideo);
           border.getEffect().addOnFocusChanged(new BorderEffect.FocusListener() {
                @Override
                public void onFocusChanged(View oldFocus, View newFocus) {
                    if(newFocus.getId() == R.id.set || newFocus.getId() == R.id.clean){
                        border.setBackgroundResource(R.color.transparent);
                    }else{
                        border.setBackgroundResource(R.drawable.shape_app_focus);
                    }
                }});
        }
        super.onWindowFocusChanged(hasFocus);
    }
    boolean isjiaoyan = false;
    ImageView clean ;
    ImageView set ;
    private void initView() {
        //处理主页

        mMainActivityHandler = new MainActivityHandler();
        time = (TextClock)findViewById(R.id.time);
        net = (NetView)findViewById(R.id.net);
        picAndVideo = (VPView)findViewById(R.id.ad);
        picAndVideo.setOnClickListener(this);
        scroll = (ScrollTextView)findViewById(R.id.scroll);
        app_1 = (ImageView)findViewById(R.id.app_1);
        app_2 = (ImageView)findViewById(R.id.app_2);
        app_3 = (ImageView)findViewById(R.id.app_3);
        app_4 = (ImageView)findViewById(R.id.app_4);
        app_5 = (ImageView)findViewById(R.id.app_5);
        app_6 = (ImageView)findViewById(R.id.app_6);
        app_7 = (ImageView)findViewById(R.id.app_7);
        app_8 = (ImageView)findViewById(R.id.app_8);
        clean = (ImageView)findViewById(R.id.clean);
        clean.setOnClickListener(this);
        set = (ImageView)findViewById(R.id.set);
        set.setOnClickListener(this);
        imageViews = new ArrayList<>();
        imageViews.add(app_1);
        imageViews.add(app_2);
        imageViews.add(app_3);
        imageViews.add(app_4);
        imageViews.add(app_5);
        imageViews.add(app_6);
        imageViews.add(app_7);
        imageViews.add(app_8);
        //设置图片圆角角度
//        RoundedCorners roundedCorners= new RoundedCorners(10);
//        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
//        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
        for (int i = 0; i <imageViews.size() ; i++){
            imageViews.get(i).setOnClickListener(this);
            //Glide.with(this).load(R.drawable.app2).apply(options).into(imageViews.get(i));
        }
        //判断是否有网
        if(Tools.IsGetNetWork(this)){
            initData();
            isjiaoyan=true;
        }
        net.setCallBack(new NetView.CallBack() {
            @Override
            public void connect() {
                if(!isjiaoyan){
                    mMainActivityHandler.sendEmptyMessageDelayed(1,5000);
                }
            }

            @Override
            public void disconnect() {

            }
        });

    }

    private void initData() {
        String oldData = SettingUtils.getOldData(getApplicationContext());
        Log.d("ytest","oldData="+oldData);
        if(!Tools.isEmpty(oldData)){
            Log.d("ytest","oldData222="+oldData);
            AD_data ad_data;
            try{
                ad_data = JSON.parseObject(oldData, AD_data.class);
            }catch(Exception e){
                return;
            }
            if(!Tools.isEmpty(ad_data)){
                List<AD_data.AdBean> ad = ad_data.getAd();
                ArrayList<AD_data.AdBean> addata = new ArrayList<>();
                ArrayList<AD_data.AdBean> scrolltext = new ArrayList<>();
                if(Tools.isEmpty(ad)){
                    return;
                }
                for (int i = 0; i <ad.size() ; i++) {
                    AD_data.AdBean adBean = ad.get(i);
                    String type = adBean.getType();
                    if(Const.PIC_AND_VIDEO.equals(type)){
                        if(!addata.contains(adBean)){
                            addata.add(adBean);
                        }
                    }else if(Const.TEXT_TYPE.equals(type)){
                        if(!scrolltext.contains(adBean)){
                            scrolltext.add(adBean);
                        }
                    }
                }
                picAndVideo.changeDatas(addata);
                scroll.setDatas(scrolltext);
            }
        }
    }

    class MainActivityHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://通知界面更新
                    //
                    initData();
                    break;
                case 2:
                    //
                    temp="";
                    break;
                case 10000://通知界面更新

                   if(!Tools.isEmpty(picAndVideo)){
                       picAndVideo.fileDown((AD_data.AdBean)msg.obj);
                   }
                   break;

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.app_1:
               // myStartActivity(Const.apps[0]);
                startWeb("http://m.szseva.com.cn");
//                Toast.makeText(this,"尚为",Toast.LENGTH_SHORT).show();
                break;
            case R.id.app_2:
                myStartActivity(Const.apps[1]);
                break;
            case R.id.app_3:
               // myStartActivity(Const.apps[2]);
                startWeb("https://shop43230812.m.youzan.com/v2/showcase/homepage?alias=ZvH6rxClKD&redirect_count=1&sl=undefined&st=1&sf=wx_sm&is_share=1&share_cmpt=native_wechat&from_uuid=7c2db6cb-5c22-f531-3b83-b9ab4118683f&from=groupmessage");
                break;
            case R.id.app_4:
                myStartActivity(Const.apps[3]);
                break;
            case R.id.app_5:
                myStartActivity(Const.apps[4]);
                break;
            case R.id.app_6:
                myStartActivity(Const.apps[5]);
                break;
            case R.id.app_7:
                myStartActivity(Const.apps[6]);
                break;
            case R.id.app_8:
                myStartActivity(Const.apps[7]);
                break;
            case R.id.set:
                myStartActivity(Const.apps[8]);
                break;
            case R.id.clean:
                myStartActivity(Const.apps[9]);
                break;
            case R.id.ad:
                Intent intent = new Intent(MainActivity.this,FullScreenActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void startWeb(String url){
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    public void myStartActivity(String packname) {
        Intent intent = null;

        if ("com.android.tv.settings".equals(packname) || "com.android.settings".equals(packname)) {

            boolean b =openAPk(this, "com.android.tv.settings");
            if (!b) {
                openAPk(this, "com.android.settings");
            }
        } else if("allapp".equals(packname) ){
            startActivity(new Intent(this,DemoRecyclerViewActivity.class));
        }else{
            intent = getPackageManager().getLaunchIntentForPackage(packname);
            if (intent != null)
                startActivity(intent);
        }
    }
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


    @Override
    public void onBackPressed() {

    }


    private void initBorder() {
        border = new BorderView(this);
        border.setBackgroundResource(R.drawable.shape_app_focus);
        border.getEffect().setScale(1.04f);
        border.getEffect().setMargin(0);
        main = (RelativeLayout) findViewById(R.id.main);
        border.attachTo(main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        picAndVideo.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        picAndVideo.stop();
        isStop = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
