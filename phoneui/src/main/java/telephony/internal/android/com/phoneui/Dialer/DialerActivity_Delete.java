package telephony.internal.android.com.phoneui.Dialer;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.telephony.CallManager;


import telephony.internal.android.com.phoneui.Dialer.Utiles.CallUtiles;
import telephony.internal.android.com.phoneui.Dialer.Utiles.MyTimerTask;
import telephony.internal.android.com.phoneui.Dialer.Utiles.Utile;
import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.permission.Permissions;
import telephony.internal.android.com.phoneui.utiles.CallRecordsUtil;
import telephony.internal.android.com.phoneui.view.MyToast;

/**
 * Created by yangbofeng on 2018/7/5.
 * 电话来电和去电界面
 */

public class DialerActivity_Delete extends Activity implements  Utile.OpenActivity {
    private String TAG = "DialerActivity";

    private TextView tv_title, tv_dialer_name, tv_dialer_number, tv_times;
    private ImageView img_title;
    private TelephonyManager mTelephonyManager;
    private Handler mThreadHandler;
    private String name = "";
    private Runnable getSpeakNames;//获取来电时名字
    private MyTimerTask myTimerTask;
    private CallManager mCM;
    private static final int PHONE_STATE_CHANGED = 102;
    //响铃
    private static final int PHONE_BELLING = 0;
    //挂断
    private int END_CALL_PHONE = 1;
    //接听
    private int ANSWER_THE_PHONE = 2;
    //拨打
    private static final int ON_CALLING = 3;
    private String number = "";
    private Context mContext;
    private String callStates = "";

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PHONE_BELLING://响铃来电
                    tv_dialer_name.setText(name);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        mContext = getApplicationContext();
        initView();
        initGetState();
        /**
         * 注册话筒状态监听
         */

        //初始化权限
        Permissions.requestPermissionAll(this);
        //注册暗码打开测试项监听
        Utile.getInstance().setOpenActivityListener(this);
        //注册电话状态监听
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
        mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        //初始化计时器
        if(myTimerTask == null){
            myTimerTask = new MyTimerTask(tv_times);
        }
        registerThread();
        initByThreadOpen();
    }



    /**
     * 获取打开页面状态
     * CallOUT  去电
     * CallIN  来电
     * IDLE 空闲
     */
    private void initGetState() {
        Intent intent = getIntent();
        callStates = intent.getStringExtra("tag");
        Log.e(TAG, "[call states]=" + callStates);
    }

    /**
     * 第三方打开此界面时进行初始化 电话号码
     */
    private void initByThreadOpen() {
    }

    /**
     * 注册子线程
     */
    public void registerThread() {
        HandlerThread mHandlerThread = new HandlerThread("DialerThread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
    }


    /**
     * 初始化动态权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Permissions.changePermissionState(this, permissions[0], true);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 初始化界面信息
     */
    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        img_title = findViewById(R.id.img_title);
        tv_times = findViewById(R.id.tv_times);
        tv_dialer_name = findViewById(R.id.tv_dialer_name);
        tv_dialer_number = findViewById(R.id.tv_dialer_number);
        tv_times.setVisibility(View.INVISIBLE);

    }

    /*adb shell input keyevent 8*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "[onkeyDown]=" + keyCode);
        if ("CallOUT".equals(callStates)) { // 接通以后可以继续按键

        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                Utile.getInstance().change(tv_dialer_number, "1");
                break;
            case KeyEvent.KEYCODE_2:
                Utile.getInstance().change(tv_dialer_number, "2");
                break;
            case KeyEvent.KEYCODE_3:
                Utile.getInstance().change(tv_dialer_number, "3");
                break;
            case KeyEvent.KEYCODE_4:
                Utile.getInstance().change(tv_dialer_number, "4");
                break;
            case KeyEvent.KEYCODE_5:
                Utile.getInstance().change(tv_dialer_number, "5");
                break;
            case KeyEvent.KEYCODE_6:
                Utile.getInstance().change(tv_dialer_number, "6");
                break;
            case KeyEvent.KEYCODE_7:
                Utile.getInstance().change(tv_dialer_number, "7");
                break;
            case KeyEvent.KEYCODE_8:
                Utile.getInstance().change(tv_dialer_number, "8");
                break;
            case KeyEvent.KEYCODE_9:
                Utile.getInstance().change(tv_dialer_number, "9");
                break;
            case KeyEvent.KEYCODE_0:
                Utile.getInstance().change(tv_dialer_number, "0");
                break;
            case KeyEvent.KEYCODE_STAR:
                Utile.getInstance().change(tv_dialer_number, "*");
                break;
            case KeyEvent.KEYCODE_POUND:
                Utile.getInstance().change(tv_dialer_number, "#");
                break;
            case KeyEvent.KEYCODE_DEL: //消去
                Utile.getInstance().delete(tv_dialer_number);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER: //去电
               CallUtiles.getInstance().call(mContext, "17629193325");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 电话状态监听
     */
    PhoneStateListener listener = new PhoneStateListener() {
        String incomingNumber = "";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Intent intent3 = new Intent();
                    intent3.setAction("com.tricheer.finish.activity");
                    sendBroadcast(intent3);
                    Log.e(TAG, "挂断-----------------");
                    if ("CallIN".equals(callStates)) {
                        Log.e(TAG, "挂断");
                        MyToast.showToast(mContext, "Call ended");
                        finish();
                    } else if ("IDLE".equals(callStates)) {
                    } else if ("CallOUT".equals(callStates)) {
                        Log.e(TAG, "挂断 out");
                        MyToast.showToast(mContext, "Call ended");
                        finish();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //如果是来电
                    if ("CallIN".equals(callStates)) {
                        Log.e(TAG, "接听");
                        tv_times.setVisibility(View.VISIBLE);
                        tv_title.setText("Talking");
                        myTimerTask.startTimer();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(TAG, "响铃:来电号码" + incomingNumber);
                    //输出来电号码
                    this.incomingNumber = incomingNumber;
                    tv_title.setText("Incoming Call");
                    tv_dialer_number.setText(incomingNumber);
                    getSpeakName();
                    break;
            }
        }

        /**
         * 管理子线程销毁问题
         */
        Runnable getSpeakNames = new Runnable() {
            @Override
            public void run() {
                name = CallRecordsUtil.getInstance().findNames(mContext, incomingNumber);
                mhandler.sendEmptyMessage(PHONE_BELLING);
            }
        };

        /**
         * 获取通话的人名
         */
        public void getSpeakName() {
            mThreadHandler.post(getSpeakNames);
        }
    };


    /**
     * 接电话
     */
    Runnable answerCall = new Runnable() {
        @Override
        public void run() {
            CallUtiles.getInstance().answerCall();
        }
    };

    /**
     * 接电话
     */
    public void answerCall() {
        mThreadHandler.post(answerCall);
    }

    /**
     * 挂电话
     */

    public void endCall() {
        CallUtiles.getInstance().end(mContext);

    }





    /**
     * 暗码打开A
     */
    @Override
    public void OpenA() {
        Log.e(TAG, "[open test A]");
    }

    /**
     * 暗码打开B
     */
    @Override
    public void OpenB() {
        Log.e(TAG, "[open test B]");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestory");
        myTimerTask.stopTimer();
        mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        mThreadHandler.removeCallbacks(getSpeakNames);
        mThreadHandler.removeCallbacks(answerCall);

    }


}
