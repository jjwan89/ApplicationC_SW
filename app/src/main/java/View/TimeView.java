package View;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.lang.ref.WeakReference;

import utils.TimeUtils;


public class TimeView extends AppCompatTextView {

    private  Context mcontext;
    private MyHandler myHandler;

    public TimeView(Context context, AttributeSet attrs) {
       this(context,attrs,0);

    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mcontext =context;

        init(context);
    }

    public TimeView(Context context) {
        this(context,null);

    }

    private void init(Context context) {
       /* setTextColor(Color.GREEN);
        setTextSize(28);*/
        myHandler = new MyHandler(context);
        myHandler.sendEmptyMessage(1);
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        setText(TimeUtils.getTime(mcontext, flag).replace("=","\n"));
        this.flag = flag;
    }

    private boolean flag=true;
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
                    setText(TimeUtils.getTime(mcontext, flag).replace("=","\n"));
                    removeMessages(1);
                    sendEmptyMessageDelayed(1, 10000);
                }
            }
        }
    }
}
