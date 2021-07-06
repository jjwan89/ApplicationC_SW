package View;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.baidu.mapapi.common.AppTools;

import java.lang.ref.WeakReference;

import fenping.szlt.com.usbhotel.R;
import utils.Tools;

import static android.content.Context.WIFI_SERVICE;


@SuppressLint("AppCompatCustomView")
public class NetView extends ImageView {


    private MyHandler myHandler;
    private Context mcontext;

    public NetView(Context context) {
        this(context,null);

    }

    public NetView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public NetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
    }

    private void initAttr(Context context,AttributeSet attrs) {
        mcontext =context;
        init(context);
    }


    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    CallBack callBack;
    public interface CallBack{
        void connect();
        void disconnect();

    }
    private void unRegister() {
        mcontext.unregisterReceiver(receiver);
        myHandler.removeMessages(UPDATE_WIFI_RSSI);
    }

    private void init(Context context) {
        myHandler = new MyHandler(context);
        IntentFilter intentFilter1 = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter1.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter1.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(receiver, intentFilter1);
        initNet(context);
    }

    private int UPDATE_WIFI_RSSI = 1;

    public BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("debug", "action============" + action);
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                myHandler.removeMessages(UPDATE_WIFI_RSSI);
                initNet(context);

            }

        }
    };
    public  void onDestroy(Context context) {
        if(!Tools.isEmpty(receiver)){
            context.unregisterReceiver(receiver);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initNet(Context context) {
        switch (getNetType(context)) {
            case 1:
                Log.i("zj", "有线");
                if(!Tools.isEmpty(this.callBack)){
                    this.callBack.connect();
                }
                NetView.this.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.net_you));
                break;
            case 2:
                System.out.println("-----------networktest---------无线");
                Log.i("zj", "wifi");
                myHandler.sendEmptyMessage(UPDATE_WIFI_RSSI);
                if(!Tools.isEmpty(this.callBack)){
                    this.callBack.connect();
                }
                break;
            case 0:
                System.out.println("-----------networktest---------无网络");
                Log.i("zj", "无网络");
                if(!Tools.isEmpty(this.callBack)){
                    this.callBack.disconnect();
                }
                NetView.this.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wifi_empty));
                break;
            default:
                break;
        }
    }

    public static int NET_ETHERNET = 1;
    public static int NET_WIFI = 2;
    public static int NET_NOCONNECT = 0;

    public static int getNetType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            return NET_ETHERNET;
        } else if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
            return NET_WIFI;
        } else {
            return NET_NOCONNECT;
        }
    }
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
                if(msg.what==UPDATE_WIFI_RSSI){
                    if (isWifiConnected(context)) {
                        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
                        WifiInfo info = wifiManager.getConnectionInfo();
                        if (info.getBSSID() != null) {
                            int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
                           // Log.i("zj","strength=="+strength);
                            switch (strength) {
                                case 0:
                                    NetView.this.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wifi_empty));
                                    break;
                                case 1:
                                    NetView.this.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wifi_1));
                                    break;
                                case 2:
                                    NetView.this.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wifi_2));
                                    break;
                                case 3:
                                    NetView.this.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wifi_3));
                                    break;
                                case 4:
                                    NetView.this.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wifi_4));
                                    break;
                            }
                        }
                    } else {
                        NetView.this.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wifi_empty));
                    }
                    myHandler.removeMessages(UPDATE_WIFI_RSSI);
                    myHandler.sendEmptyMessageDelayed(UPDATE_WIFI_RSSI, 1000);
                }
            }
        }
    }
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            NetworkInfo.State state = wifiInfo.getState();
            if (NetworkInfo.State.CONNECTED == state) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


}




