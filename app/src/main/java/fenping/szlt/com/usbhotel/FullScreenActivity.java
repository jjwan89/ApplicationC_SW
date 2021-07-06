package fenping.szlt.com.usbhotel;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import View.VPView;
import utils.SettingUtils;
import utils.Tools;

public class FullScreenActivity extends Activity {

    private VPView picAndVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        picAndVideo = findViewById(R.id.ad_full);
        initData();
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
            }
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://通知界面更新
                    //
                    initData();
                    break;
                case 2:
                    //
                    break;
                case 10000://通知界面更新

                    if(!Tools.isEmpty(picAndVideo)){
                        picAndVideo.fileDown((AD_data.AdBean)msg.obj);
                    }
                    break;

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        picAndVideo.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        picAndVideo.stop();
    }
}