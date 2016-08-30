 /*DemoService.java */
package com.android.server;
import android.content.Context;
import android.os.Handler;
import android.os.IDemoService;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class DemoService extends IDemoService.Stub {
    private static final String TAG = "DemoService";
    private DemoWorkerThread mWorker;
    private DemoWorkerHandler mHandler;
    private Context mContext;
    public DemoService(Context context) {
        super();
        mContext = context;
   		mWorker = new DemoWorkerThread("DemoServiceWorker");
        mWorker.start();
        Log.i(TAG, "Spawned worker thread");
    }

    public String setData(int val) {
        Log.i(TAG, "setData " + val);
        Message msg = Message.obtain();
        msg.what = DemoWorkerHandler.MESSAGE_SET;
        msg.arg1 = val;
        mHandler.sendMessage(msg);
        return "Demo string received from service!";
    }

    private class DemoWorkerThread extends Thread {
        public DemoWorkerThread(String name) {
            super(name);
        }
        public void run() {
            Looper.prepare();
            mHandler = new DemoWorkerHandler();
            Looper.loop();
        }
    }

    private class DemoWorkerHandler extends Handler {
        private static final int MESSAGE_SET = 0;
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == MESSAGE_SET) {
          Log.i(TAG,"set message received:"+msg.arg1);
                }
            } catch (Exception e) {
               // Log, don't crash!
          Log.e(TAG, "Exception in handleMessage");
            }
        }
    }
}
