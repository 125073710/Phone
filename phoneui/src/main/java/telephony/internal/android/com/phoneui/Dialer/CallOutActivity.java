package telephony.internal.android.com.phoneui.Dialer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallManager;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

import telephony.internal.android.com.phoneui.Dialer.Utiles.MyTimerTask;
import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.utiles.CallRecordsUtil;

/**
 * Created by yangbofeng on 2018/7/6.
 * 打电话UI显示作废
 */

public class CallOutActivity extends Activity {
    private String TAG = "CallOutActivity";
    private TextView tv_call_out_times, tv_call_out_title, tv_dialer_call_out_name, tv_dialer_call_out_number;
    private CallManager mCM;
    private static final int PHONE_STATE_CHANGED = 102;
    private MyTimerTask myTimer;
    private Context mContext;
    private String callOutnumber;
    private String name ="";
    private Handler mThreadHandler;
    private FinishActivityBoardcast mFinishActivityBoardcast;


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"msg.what ="+msg.what);
            switch (msg.what) {
                case PHONE_STATE_CHANGED:
                    updatePhoneSateChange();
                    break;
                case 0:
                    tv_dialer_call_out_name.setText(name);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_out);
        mContext = getApplicationContext();
        Log.e(TAG,"onCreat");
        initView();
        initData();
        registerThread();
        initBroadcast();
        if(myTimer == null){
            myTimer = new MyTimerTask(tv_call_out_times);
        }

    }


    private void initView() {
        tv_call_out_times = findViewById(R.id.tv_call_out_times);
        tv_call_out_title = findViewById(R.id.tv_call_out_title);
        tv_dialer_call_out_name = findViewById(R.id.tv_dialer_call_out_name);
        tv_dialer_call_out_number = findViewById(R.id.tv_dialer_call_out_number);
        tv_call_out_times.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        Intent intent = getIntent();
        callOutnumber =intent.getStringExtra("number");
        Log.e(TAG,"callOutnumber"+callOutnumber);
        tv_dialer_call_out_number.setText(callOutnumber);
        initCallOut();
        getSpeakName();
    }

    //初始化监听去电状态
    private void initCallOut() {
        mCM = CallManager.getInstance();
        Phone phone = PhoneFactory.getDefaultPhone();
        mCM.registerPhone(phone);
        mCM.registerForPreciseCallStateChanged(mhandler, PHONE_STATE_CHANGED, null);
    }
    /**
     * 注册子线程
     */
    public void registerThread() {
        HandlerThread mHandlerThread = new HandlerThread("CallThread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
    }
    /**
     * 管理子线程销毁问题
     */
    Runnable getSpeakNames = new Runnable() {
        @Override
        public void run() {
            name = CallRecordsUtil.getInstance().findNames(mContext, callOutnumber);
            mhandler.sendEmptyMessage(0);
        }
    };

    /**
     * 获取通话的人名
     */
    public void getSpeakName() {
        mThreadHandler.post(getSpeakNames);
    }

    //监听去电接通
    private void updatePhoneSateChange() {
        Call fgCall = mCM.getActiveFgCall();
        if (mCM.hasActiveRingingCall()) {
            fgCall = mCM.getFirstActiveRingingCall();
        }
        final Call.State state = fgCall.getState();
        switch (state) {
            case IDLE://空闲状态（未接通）
                Log.e(TAG, "IDLE");
                finish();
                break;
            case ACTIVE://接通状态
                Log.e(TAG, "ACTIVE");
                tv_call_out_title.setText("Takling");
                tv_dialer_call_out_name.setVisibility(View.INVISIBLE);
                tv_call_out_times.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }
    private void initBroadcast() {
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.tricheer.finish.activity");
        mFinishActivityBoardcast = new FinishActivityBoardcast();
        //注册广播接收
        registerReceiver(mFinishActivityBoardcast, filter);
    }

    class FinishActivityBoardcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "action=" + action);
            if (action.equals("com.tricheer.finish.activity")) {
                myTimer.stopTimer();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThreadHandler.removeCallbacks(getSpeakNames);
        unregisterReceiver(mFinishActivityBoardcast);
        Log.e(TAG,"onDestoy");
    }
}
