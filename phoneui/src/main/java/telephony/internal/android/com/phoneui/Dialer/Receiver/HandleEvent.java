package telephony.internal.android.com.phoneui.Dialer.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by yangbofeng on 2018/7/10.
 *
 * 不用，备用
 */

public class HandleEvent {


    private String TAG ="HandleEvent";

    private FinishActivityBoardcast finishActivityReceiver;

    public void initBroadcast(Context mContext) {
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.input.action.HANDLE_UP");
        filter.addAction("android.hardware.input.action.HANDLE_DOWN");
        finishActivityReceiver = new FinishActivityBoardcast();
        //注册广播接收
        mContext.registerReceiver(finishActivityReceiver, filter);
    }


    class FinishActivityBoardcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "action=" + action);
            if ("android.hardware.input.action.HANDLE_UP".equals(action)) { //接电话
                Log.e(TAG, "ansewer call");
            } else if ("android.hardware.input.action.HANDLE_DOWN".equals(action)) { //挂电话

            }
        }
    }
    public void unRegeister(Context mContext){
        mContext.unregisterReceiver(finishActivityReceiver);
    }

}
