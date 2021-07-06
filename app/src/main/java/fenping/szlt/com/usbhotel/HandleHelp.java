package fenping.szlt.com.usbhotel;

import android.os.Message;


public class HandleHelp {


    
    public static void sendEmptyMessage(int what, int delay,Object object) {

        Message message = new Message();
        message.what=what;
        message.obj =object;
        MainActivity.mMainActivityHandler.removeMessages(what);
        MainActivity.mMainActivityHandler.sendMessageDelayed(message,delay);

        Message message2 = new Message();
        message2.what=what;
        message2.obj =object;

    }
}
