package View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fenping.szlt.com.usbhotel.AD_data;
import fenping.szlt.com.usbhotel.Const;
import utils.FileUtils;
import utils.SDUtils;
import utils.Tools;

/**
 * Android auto Scroll Text,like TV News,AD devices
 * <p>
 * NEWEST LOG :
 * 1.setText() immediately take effect (v1.3.6)
 * 2.support scroll forever            (v1.3.7)
 * <p>
 * <p>
 * <p>
 * <p>
 * Basic knowledge：https://www.jianshu.com/p/918fec73a24d
 *
 * @author anylife.zlb@gmail.com  2013/09/02
 */
public class ScrollTextView extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "ScrollTextView";
    // surface Handle onto a raw buffer that is being managed by the screen compositor.
    private SurfaceHolder surfaceHolder;   //providing access and control over this SurfaceView's underlying surface.

    private Paint paint = null;
    private Canvas canvas;
    private boolean isSurfaceCreate;
    private boolean isbackdraw;
    private int  currentPosition=0;
    private List<File> adPathList;
    private ArrayList<AD_data.AdBean> datas;

    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        surfaceHolder = this.getHolder();  //get The surface holder
        surfaceHolder.addCallback(this);
        myHandler= new MyHandler(context);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        //setText("欢迎下榻本酒店");
        setZOrderOnTop(true);  //Control whether the surface view's surface is placed on top of its window.
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setFocusable(false);
        prepareData();
    }

    @Override
    public boolean hasFocus() {
        return true;
    }


    private boolean stopScroll = false;          // stop scroll

    //Default value
    private int speed = 3;                  // scroll-speed
    private String text = Const.SCROLL_TYPE_DEL;               // scroll text
    private float textSize = 35f;           // text size


    private int viewWidth = 0;
    private int viewHeight = 0;
    private float textWidth = 0f;
    private float textX = 0f;
    private float textY = 0f;
    private float viewWidth_plus_textLength = 0.0f;

    private ScheduledExecutorService scheduledExecutorService;

    boolean isSetNewText = false;

    /**
     * constructs 1
     *
     * @param context you should know
     */
    public ScrollTextView(Context context) {
        this(context,null);

    }



    /**
     * measure text height width
     *
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mHeight = getFontHeight(textSize);  //实际的视图高
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        // when layout width or height is wrap_content ,should init ScrollTextView Width/Height
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(viewWidth, mHeight);
            viewHeight = mHeight;
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(viewWidth, viewHeight);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(viewWidth, mHeight);
            viewHeight = mHeight;
        }
    }


    /**
     * surfaceChanged
     *
     * @param arg0 arg0
     * @param arg1 arg1
     * @param arg2 arg1
     * @param arg3 arg1
     */
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    /**
     * surfaceCreated,init a new scroll thread.
     * lockCanvas
     * Draw somthing
     * unlockCanvasAndPost
     *
     * @param holder holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        stopScroll = false;
        isSurfaceCreate =true;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTextThread(), 10, 10, TimeUnit.MILLISECONDS);
        //Log.d(TAG, "ScrollTextTextView is created");
    }

    /**
     * surfaceDestroyed
     *
     * @param arg0 SurfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        stopScroll = true;
        isSurfaceCreate=false;
        scheduledExecutorService.shutdownNow();
        //Log.d(TAG, "ScrollTextTextView is destroyed");

    }

    /**
     * text height
     *
     * @param fontSize fontSize
     * @return fontSize`s height
     */
    private int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }


    public String getText() {
        return text;
    }

    /**
     * set scroll text
     *
     * @param newText scroll text
     */
    public void setText(String newText) {
        isSetNewText = true;
        stopScroll = false;
        this.text = newText;
        measureVarious();
    }


    /**
     * set scroll speed
     *
     * @param speed SCROLL SPEED [0,10] ///// 0?
     */
    public void setSpeed(int speed) {
        if (speed > 10 || speed < 0) {
            throw new IllegalArgumentException("Speed was invalid integer, it must between 0 and 10");
        } else {
            this.speed = speed;
        }
    }

/*



    /**
     * Draw text
     *
     * @param X X
     * @param Y Y
     */
    private synchronized void draw(float X, float Y) {
        if(!isSurfaceCreate){
            return;
        }
        try{
            if(surfaceHolder!=null){
                canvas = surfaceHolder.lockCanvas(null);
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                canvas.drawText(text, X, Y, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * measure tex
     */
    private void measureVarious() {
        textWidth = paint.measureText(text);
        viewWidth_plus_textLength = viewWidth + textWidth;
        FontMetrics fontMetrics = paint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        textY = viewHeight / 2 + distance;
    }

    public void setDatas(ArrayList<AD_data.AdBean> datas) {
        handleList(datas);
    }

    /**
     * Scroll thread
     */
    class ScrollTextThread implements Runnable {
        @Override
      public void run() {

            while (!stopScroll) {
                measureVarious();
                draw(viewWidth - textX, textY);
                textX += speed;
                if (textX > viewWidth_plus_textLength) {
                    textX = 0;
                    String currentText = getCurrentText();
                    if (Tools.isEmpty(currentText)) {
                       // Log.i("scroll","播放默认字幕");
                        setText(Const.SCROLL_TYPE_DEL);

                    } else {
                        //getCurrentText();
                       // Log.i("scroll","播放推送字幕");
                        setText(currentText);
                    }
                }
            }
        }
    }

    private String getCurrentText() {
        if(Tools.isEmpty(playlist)){
            return "";
        }
        if(currentPosition >=playlist.size()){
            currentPosition =0;
        }
        AD_data.AdBean urldataBean = playlist.get(currentPosition);
        String url = urldataBean.getUrl();
        currentPosition++;
        return url;

    }



    private void prepareData() {

        currentPosition =0;

    }

    private boolean showPlay(AD_data.AdBean urldataBean) {
        Calendar instance = Calendar.getInstance();
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int minute = instance.get(Calendar.MINUTE);
        long curtime = hour*60+minute;
        // Log.i("scroll","当前时分和=="+curtime);
        long timeInMillis = instance.getTimeInMillis();
        // Log.i("scroll","当前时间戳=="+timeInMillis);
        String starttime =urldataBean.getStarttime();
        String stoptime = urldataBean.getStoptime();
        long starttotal,stoptotal,startdian,stopdian;
        boolean isdian = false;

        if(starttime.contains("=")){
            Log.i("scroll","时间段播放");
            String[] start = starttime.split("=");
            starttotal = Long.parseLong(start[0]);
            String[] split = start[1].split(":");
            startdian = Long.parseLong(split[0])*60+Long.parseLong(split[1]);
            String[] stop = stoptime.split("=");
            stoptotal= Long.parseLong(stop[0]);
            String[] split1 = stop[1].split(":");
            stopdian= Long.parseLong(split1[0])*60+Long.parseLong(split1[1]);
            isdian = true;
        }else{
            Log.i("scroll","循环播放");
            starttotal = Long.parseLong(starttime);
            stoptotal = Long.parseLong(stoptime);
            startdian= 0;
            stopdian=0;
        }
        Log.i("scroll","starttotal=="+starttotal);
        Log.i("scroll","stoptotal=="+stoptotal);
        Log.i("scroll","startdian=="+startdian);
        Log.i("scroll","stopdian=="+stopdian);
        if(timeInMillis>=starttotal&& timeInMillis<stoptotal){
            Log.i("scroll","处在当前时间段");
            //在当前时间段
            if(!isdian){
                return true;
            }
            if(startdian>stopdian){
                if(curtime>=startdian || curtime<stopdian){
                    return true;
                }else{
                    return false;
                }
            }else{
                if(curtime>=startdian && curtime<stopdian){
                    return true;
                }else{
                    return false;
                }
            }
        }else{
            //不在当前时间段
            Log.i("scroll","不在当前时间段");
            return false;
        }
    }
    List<AD_data.AdBean> playlist  = new ArrayList<>();
    MyHandler myHandler;
    private static int ADD_DATA = 1000; //表示插入数据
    private static int REMOVE_DATA = 1001; //表示插入数据
    private void handleList(List<AD_data.AdBean> data) {
        playlist.clear();
        Calendar instance = Calendar.getInstance();
        long curStamp = instance.getTimeInMillis();
        for (int i = 0; i < data.size(); i++) {
            //拿到当前时间戳
            String starttime = data.get(i).getStarttime();
            String stoptime = data.get(i).getStoptime();
            if (starttime.contains("=")) {
                String[] startArr = starttime.split("=");
                String[] stopArr = stoptime.split("=");
                long startStamp = Long.parseLong(startArr[0]);
                long stopStamp = Long.parseLong(stopArr[0]);
                if (curStamp < stopStamp) {
                    //时间戳对了
                    long day = 24 * 60 * 60 * 1000;
                    if (curStamp > startStamp) {
                        Message obtain = new Message();
                        obtain.obj = data.get(i);
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
                        Message obtain2 = new Message();
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
               if (msg.what == REMOVE_DATA) {
                    //处理移除数据
                    AD_data.AdBean obj = (AD_data.AdBean) msg.obj;
                    if (playlist.contains(obj)) {
                        playlist.remove(obj);
                    }
                    for (int i = playlist.size() - 1; i >= 0; i--) {
                        if (playlist.get(i).toString().equals(obj.toString())) {
                            playlist.remove(i);
                        }
                    }
                } else if (msg.what == ADD_DATA) {
                    //处理添加数据
                    AD_data.AdBean obj = (AD_data.AdBean) msg.obj;
                    if (!playlist.contains(obj)) {
                        playlist.add(obj);
                    }
                }
            }
        }
    }

}
