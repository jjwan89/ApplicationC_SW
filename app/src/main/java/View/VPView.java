package View;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import anim.Anim;
import anim.AnimBaiYeChuang;
import anim.AnimCaChu;
import anim.AnimHeZhuang;
import anim.AnimJieTi;
import anim.AnimLingXing;
import anim.AnimLunZi;
import anim.AnimPiLie;
import anim.AnimQiPan;
import anim.AnimQieRu;
import anim.AnimShanXingZhanKai;
import anim.AnimShiZiXingKuoZhan;
import anim.AnimSuiJiXianTiao;
import anim.AnimXiangNeiRongJie;
import anim.AnimYuanXingKuoZhan;
import fenping.szlt.com.usbhotel.AD_data;
import fenping.szlt.com.usbhotel.Const;
import fenping.szlt.com.usbhotel.R;
import utils.FileUtils;
import utils.SDUtils;
import utils.Tools;


public class VPView extends RelativeLayout implements SurfaceHolder.Callback {

    public final MyHandler myHandler;
    public final String  tag= "kji";
    private final Context mcontext;
    private View vp;
    private ImageView iv;
    private SurfaceView sv;
    private EnterAnimLayout anim_layout;
    private int currentPosition = 0;
    private MediaPlayer mediaPlayer;
    private AD_data.AdBean curplaydata = null;
    //标记数据的what
    private static int ADD_DATA = 1000; //表示插入数据
    private static int REMOVE_DATA = 1001; //表示插入数据
    private static int REPLAY = 1002; //表示插入数据
    private static int PLAY = 1003; //表示插入数据
    boolean isPlay = false;
    private List<AD_data.AdBean> shouldPlaylist = new ArrayList<AD_data.AdBean>();
    private List<AD_data.AdBean> playlist = new ArrayList<AD_data.AdBean>();
    private String curplayString = "";
    long hisposition = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VPView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VPView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VPView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VPView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        //Log.i(tag, "创建View");
        vp = LayoutInflater.from(context).inflate(R.layout.vp, this);
        anim_layout = vp.findViewById(R.id.anim_layout);
        iv = vp.findViewById(R.id.iv);
        sv = vp.findViewById(R.id.sv);
        sv.getHolder().addCallback(this);
        myHandler = new MyHandler(context);
        mcontext = context.getApplicationContext();
        Glide.with(mcontext)
                .load(Const.AD1_IMG)
                .placeholder(Const.AD1_IMG)
                .error(Const.AD1_IMG)
                .dontAnimate()
                .into(iv);
    }

    private void initMediaPlayer() {
       // Log.i(tag,"initMediaPlayer");
        if (Tools.isEmpty(mediaPlayer)) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            //Log.i(tag,"setOnPreparedListener");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if(hisposition!=0){
                        mediaPlayer.seekTo((int) hisposition);
                    }
                    mediaPlayer.start();
                    isPlay = true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    myHandler.removeMessages(PLAY);
                    myHandler.sendEmptyMessage(PLAY);
                    isPlay = false;
                    hisposition=0;
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                   // Log.e(tag,"setOnErrorListener");
                    myHandler.removeMessages(PLAY);
                    myHandler.sendEmptyMessage(PLAY);
                    isPlay = false;
                    return true;
                }
            });
        } else {
            if (mediaPlayer.isPlaying()) {
                hisposition = mediaPlayer.getCurrentPosition();
                //Log.i(tag,"hisposition=="+hisposition);
                mediaPlayer.pause();
                mediaPlayer.stop();
                mediaPlayer.reset();
            }else {
                mediaPlayer.reset();
            }
        }
        isPlay = false;
        mediaPlayer.setDisplay(null);
    }

    public void changeDatas(List<AD_data.AdBean> data) {
        //处理新数据
        //Log.e(tag,"changeDatas=="+data);
        Log.d("ytest","changeDatas="+data);
        handleList(data);
    }

    private void handleList(List<AD_data.AdBean> data) {
        shouldPlaylist.clear();
        playlist.clear();
        Calendar instance = Calendar.getInstance();
        long curStamp = instance.getTimeInMillis();
        for (int i = 0; i < data.size(); i++) {
            //拿到当前时间戳
            String starttime = data.get(i).getStarttime();
            String stoptime = data.get(i).getStoptime();
            Log.d("ytest","starttime="+starttime+",stoptime="+stoptime);
            if (starttime.contains("=")) {
                String[] startArr = starttime.split("=");
                String[] stopArr = stoptime.split("=");
                long startStamp = Long.parseLong(startArr[0]);
                long stopStamp = Long.parseLong(stopArr[0]);
                if (curStamp < stopStamp) {
                    Log.d("ytest","time01");
                    //时间戳对了
                    long day = 24 * 60 * 60 * 1000;
                    if (curStamp > startStamp) {

                        Message obtain = new Message();
                        obtain.obj = data.get(i);
                        Log.d("ytest","time02"+data.get(i));
                        obtain.what = ADD_DATA;
                        myHandler.sendMessage(obtain);
                        long newstart = startStamp;
                        while (newstart < stopStamp) {
                            if (newstart > curStamp) {
                                Message obtain1 = new Message();
                                obtain1.obj = data.get(i);
                                obtain1.what = ADD_DATA;
                                myHandler.sendMessageDelayed(obtain1, startStamp - curStamp);
                            }
                            newstart = newstart + day;
                        }
                    } else {
                        Log.d("ytest","time03");
                        long newstart = startStamp;
                        while (newstart < stopStamp) {
                            Message obtain1 = new Message();
                            obtain1.obj = data.get(i);
                            obtain1.what = ADD_DATA;
                            myHandler.sendMessageDelayed(obtain1, startStamp - curStamp);
                            newstart = newstart + day;
                        }
                    }
                    long newstop = stopStamp;
                    while (newstop > startStamp && newstop >= curStamp) {
                        Message obtain2 =new Message();
                        obtain2.obj = data.get(i);
                        obtain2.what = REMOVE_DATA;
                        myHandler.sendMessageDelayed(obtain2, stopStamp - curStamp);
                        newstop = newstop - day;
                    }
                } else {
                }
            } else {
                long startStamp = Long.parseLong(starttime);
                long stopStamp = Long.parseLong(stoptime);
                if (curStamp >= startStamp && curStamp < stopStamp) {
                    Message obtain = new Message();
                    obtain.obj = data.get(i);
                    obtain.what = ADD_DATA;
                    myHandler.sendMessage(obtain);
                    obtain = new Message();
                    obtain.obj = data.get(i);
                    obtain.what = REMOVE_DATA;
                    myHandler.sendMessageDelayed(obtain, stopStamp - curStamp);
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Log.e(tag,"surfaceCreated  isPlay=="+isPlay);
        if (!Tools.isEmpty(mediaPlayer) && !isPlay) {
            try {
                //Log.i(tag,"setDataSource  curplayString=="+curplayString);
                mediaPlayer.setDataSource(curplayString);
                mediaPlayer.setDisplay(sv.getHolder());
                mediaPlayer.prepareAsync();
               // Log.i(tag,"prepareAsync");
            } catch (Exception e) {
               // Log.i(tag,"e=="+e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
       // Log.i(tag,"surfaceDestroyed");
        initMediaPlayer();

    }

    public void fileDown(AD_data.AdBean adu) {
        for (int i = shouldPlaylist.size()-1; i >=0 ; i--) {
            if(shouldPlaylist.get(i).toString().equals(adu.toString())){
                if(!playlist.contains(shouldPlaylist.get(i))){
                    playlist.add(adu);
                    if(!isPlay){
                        replay();
                    }
                    break;
                }
            }
        }
    }

    class MyHandler extends Handler {
        // 弱引用 ，防止内存泄露
        private WeakReference<Context> weakReference;

        public MyHandler(Context context) {
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
                if (msg.what == REPLAY) {
                   // Log.i("play","REPLAY");
                    replay();
                } else if (msg.what == PLAY) {
                    playData();
                } else if (msg.what == REMOVE_DATA) {
                    //处理移除数据
                    AD_data.AdBean obj = (AD_data.AdBean) msg.obj;
                    if (shouldPlaylist.contains(obj)) {
                        shouldPlaylist.remove(obj);
                    }
                    for (int i = playlist.size() - 1; i >= 0; i--) {
                        if (playlist.get(i).toString().equals(obj.toString())) {
                            playlist.remove(i);
                        }
                    }
                } else if (msg.what == ADD_DATA) {
                    //处理添加数据
                    Log.d("ytest","add data");
                    AD_data.AdBean obj = (AD_data.AdBean) msg.obj;
                    if (!shouldPlaylist.contains(obj)) {
                        shouldPlaylist.add(obj);
                        Log.d("ytest","add data01");
                    }
                    String filePathFormUrl = FileUtils.getDestPath(obj.getUrl());
                    Log.d("ytest","filePathFormUrl="+filePathFormUrl);
                    if (new File(filePathFormUrl).exists() && !playlist.contains(obj)) {
                        playlist.add(obj);
                        Log.d("ytest","add data02");
                    }
                    //Log.i("play","isPlay=="+isPlay);
                    if (!Tools.isEmpty(playlist) && !isPlay) {
                        Log.d("ytest","add data03");
                        myHandler.removeMessages(REPLAY);
                        myHandler.sendEmptyMessage(REPLAY);
                    }
                }
            }
        }
    }

    private void replay() {
        currentPosition = 0;
        myHandler.removeMessages(PLAY);
        myHandler.sendEmptyMessage(PLAY);
    }

    private void playData() {
        curplayString = getPlayPath();
        //Log.i("play","curplayString=="+curplayString);
        if (Tools.isEmpty(curplayString)) {
            sv.setVisibility(GONE);
            iv.setVisibility(VISIBLE);
            Glide.with(mcontext).load(Const.AD1_IMG)
                    .placeholder(Const.AD1_IMG)
                    .error(Const.AD1_IMG)
                    .dontAnimate()
                    .into(iv);
            return;
        }
        if (Tools.isVideo(curplayString)) {
            playVideo();
        } else {
            playPicture();
        }
        startMyAnimation();
    }

    public void startMyAnimation() {
        int abs = (int) Math.abs(Math.floor(Math.random() * 15));
        Anim anim;
        if (abs == 0) {
            anim = new AnimBaiYeChuang(anim_layout);
        } else if (abs == 1) {
            anim = new AnimCaChu(anim_layout);
        } else if (abs == 2) {
            anim = new AnimHeZhuang(anim_layout);
        } else if (abs == 3) {
            anim = new AnimJieTi(anim_layout);
        } else if (abs == 4) {
            anim = new AnimLingXing(anim_layout);
        } else if (abs == 5) {
            anim = new AnimLunZi(anim_layout);
        } else if (abs == 6) {
            anim = new AnimPiLie(anim_layout);
        } else if (abs == 7) {
            anim = new AnimYuanXingKuoZhan(anim_layout);
        } else if (abs == 8) {
            anim = new AnimQieRu(anim_layout);
        } else if (abs == 9) {
            anim = new AnimShanXingZhanKai(anim_layout);
        } else if (abs == 10) {
            anim = new AnimShiZiXingKuoZhan(anim_layout);
        } else if (abs == 11) {
            anim = new AnimSuiJiXianTiao(anim_layout);
        } else {
            anim = new AnimXiangNeiRongJie(anim_layout);
        }
        anim.startAnimation();
    }

    private void playPicture() {
        sv.setVisibility(GONE);
        iv.setVisibility(VISIBLE);
        Glide.with(mcontext).load(curplayString).into(iv);
        myHandler.removeMessages(PLAY);
        myHandler.sendEmptyMessageDelayed(PLAY, Const.PIC1_TIME);
    }

    private void playVideo() {
        iv.setVisibility(GONE);
        sv.setVisibility(GONE);
        initMediaPlayer();
        sv.setVisibility(VISIBLE);
    }

    private String getPlayPath() {
        if (Tools.isEmpty(playlist)) {
            return "";
        }
        if (currentPosition >= playlist.size()) {
            currentPosition = 0;
        }
        AD_data.AdBean urldataBean = playlist.get(currentPosition);
        curplaydata = urldataBean;
        String s = FileUtils.getDestPath(urldataBean.getUrl());
        currentPosition++;
        //判断后续是否有要播放的资源，
        return s;
    }

    public void stop() {
        if(Tools.isPicture(curplayString)){
            myHandler.removeMessages(PLAY);
        }
    }

    public void start() {
        if(Tools.isPicture(curplayString)){
            myHandler.removeMessages(PLAY);
            myHandler.sendEmptyMessageDelayed(PLAY,Const.PIC1_TIME);
        }
    }
}
