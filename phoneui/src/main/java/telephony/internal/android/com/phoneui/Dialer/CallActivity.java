package telephony.internal.android.com.phoneui.Dialer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallManager;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.orhanobut.logger.Logger;

import telephony.internal.android.com.phoneui.Dialer.Utiles.CallUtiles;
import telephony.internal.android.com.phoneui.Dialer.Utiles.MyTimerTask;
import telephony.internal.android.com.phoneui.Dialer.Utiles.Utile;
import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.permission.Permissions;
import telephony.internal.android.com.phoneui.utiles.CallRecordsUtil;
import telephony.internal.android.com.phoneui.view.MyDialog;
import telephony.internal.android.com.phoneui.view.MyToast;


/**
 * Created by yangbofeng on 2018/7/9.
 */

public class CallActivity extends Activity {
    private String TAG = "CallActivity";

    private TextView tv_title, tv_dialer_name, tv_dialer_number, tv_times;
    private ImageView img_title;
    private LinearLayout ly_emergency;
    private Context mContext;
    private String tag = "";//传递过来参数
    private String callOutNumber = "";//来去电号码
    private String incomingName = ""; //来电名字
    private MyTimerTask myTimerTask;
    private Handler mThreadHandler;
    private String name = "";
    private CallManager mCM;
    private static final int PHONE_STATE_CHANGED = 102;
    private static final int PHONE_CALL_NAME = 0;//来去电姓名和号码
    private static final int PHONE_CALL_OUT_ANSWER = 1;//去电接通
    private static final int PHONE_CALL_IN_ANSWER = 2;//去电接通
    private boolean isfrist = true;
    private FinishActivityBoardcast finishActivityReceiver;
    private boolean isAnswering = false;
    private Phone phone;
    private MyDialog emergency;
    private AudioManager audioManager;
    private int   currVolume;
    private boolean openSpeak =true;
    private boolean isopenMic=true;


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"msg.what="+msg.what);
            switch (msg.what) {
                case PHONE_STATE_CHANGED:
                    updatePhoneSateChange();
                    break;
                case PHONE_CALL_NAME: //填写去电信息
                    Log.e(TAG, "PHONE_CALL_NAME");
                    tv_dialer_name.setText(name);
                    if ("118".equals(callOutNumber)) {
                        ly_emergency.setBackground(getResources().getDrawable(R.drawable.sanjiao));
                    }
                    break;
                case PHONE_CALL_OUT_ANSWER://去电接通
                    Log.e(TAG, "PHONE_CALL_OUT_ANSWER");
                    tv_times.setVisibility(View.VISIBLE);
                    tv_title.setText("Talking");
                    myTimerTask.startTimer();
                    tv_dialer_number.setText("");
                    tv_dialer_name.setText("");
                    isAnswering = true;
                    break;
                case PHONE_CALL_IN_ANSWER: //来电接通
                    Log.e(TAG, "PHONE_CALL_IN_ANSWER");
                    tv_times.setVisibility(View.VISIBLE);
                    myTimerTask.startTimer();
                    tv_dialer_number.setText("");
                    tv_dialer_name.setText("");
                    isAnswering = true;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acll);
        mContext = getApplicationContext();
        registerThread();
        initView();
        initData();
        initCallOut();
        //初始化权限
        Permissions.requestPermissionAll(this);
        initBroadcast();
        //初始化计时器
        if (myTimerTask == null) {
            myTimerTask = new MyTimerTask(tv_times);
        }
        //获取音频服务
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 显示紧急号码提示框
     */
    public void showEmergency() {
        emergency = new MyDialog(this);
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_call_energency, null);
        emergency.setLayoutView(dialog);
        Window window = emergency.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 350;
        window.setAttributes(p);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        emergency.setCanceledOnTouchOutside(false);
        emergency.show();
    }

    /**
     * 初始化监听去电状态
     */

    private void initCallOut() {
        mCM = CallManager.getInstance();
        phone = PhoneFactory.getDefaultPhone();
        mCM.registerPhone(phone);
        mCM.registerForPreciseCallStateChanged(mhandler, PHONE_STATE_CHANGED, null);
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
              //  finish();
                break;
            case ACTIVE://接通状态
                Log.e(TAG, "ACTIVE");
                mhandler.sendEmptyMessage(PHONE_CALL_OUT_ANSWER);
                break;
            default:
                break;
        }

    }

    /**
     * 注册子线程,接电话
     */
    public void registerThread() {
        HandlerThread mHandlerThread = new HandlerThread("DialerThread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
    }

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

    Runnable endCall = new Runnable() {
        @Override
        public void run() {
            CallUtiles.getInstance().end(mContext);
        }
    };

    /**
     * 挂电话
     */

    public void onEndCall() {
        mThreadHandler.post(endCall);

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

    private void initBroadcast() {
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.finish.activity");
        filter.addAction("com.tricheer.PHONE_STATE");//电话状态改变
        filter.addAction("android.hardware.input.action.HANDLE_UP");
        filter.addAction("android.hardware.input.action.HANDLE_DOWN");
        finishActivityReceiver = new FinishActivityBoardcast();
        //注册广播接收
        registerReceiver(finishActivityReceiver, filter);
    }

    /**
     * 获取传递过来数据
     * tag --> out 去电
     * -----> in 来电
     */
    private void initData() {
        Intent intent = getIntent();
        callOutNumber = intent.getStringExtra("callOutNumber");
        tag = intent.getStringExtra("tag");
        Log.e(TAG, "[callOutNumber]=" + callOutNumber + "[tag]=" + tag);
        Log.e(TAG, "tag=" + tag);
        if (tag.equals("IN")) { //如果是来电
            tv_title.setText("Incoming Call");
        } else if (tag.equals("OUT")) {
            tv_title.setText("Dialing");
        }
        tv_dialer_number.setText(callOutNumber);
        getSpeakName();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        img_title = findViewById(R.id.img_title);
        tv_times = findViewById(R.id.tv_times);
        tv_dialer_name = findViewById(R.id.tv_dialer_name);
        tv_dialer_number = findViewById(R.id.tv_dialer_number);
        tv_times.setVisibility(View.INVISIBLE);
        ly_emergency =  findViewById(R.id.ly_emergency);

        /**
         * 测试bt
         */
        Button bt_jinying = findViewById(R.id.bt_jinying);
        bt_jinying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "测试bt");
                Log.e(TAG,"---1-****");
            }
        });
    }


    //获取联系人名字
    Runnable getSpeakNames = new Runnable() {
        @Override
        public void run() {
            name = CallRecordsUtil.getInstance().findNames(mContext, callOutNumber);
            mhandler.sendEmptyMessage(PHONE_CALL_NAME);
        }
    };

    /**
     * 获取通话的人名
     */
    public void getSpeakName() {
        mThreadHandler.post(getSpeakNames);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        tv_times.setVisibility(View.INVISIBLE);
        myTimerTask.stopTimer();
        mThreadHandler.removeCallbacks(getSpeakNames);
        mThreadHandler.removeCallbacks(answerCall);
        mThreadHandler.removeCallbacks(endCall);
        if (emergency != null) {
            emergency.dismiss();
        }

        unregisterReceiver(finishActivityReceiver);
        MyToast.showToast(mContext, "Call ended");
    }

    class FinishActivityBoardcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "action=" + action);
            if ("com.finish.activity".equals(action)) {
                finish();
            } else if ("android.hardware.input.action.HANDLE_UP".equals(action)) { //接电话
                Log.e(TAG, "ansewer call");
                if ("IN".equals(tag)) {
                    answerCall();
                    mhandler.sendEmptyMessage(PHONE_CALL_IN_ANSWER);
                }
            } else if ("android.hardware.input.action.HANDLE_DOWN".equals(action)) { //挂电话
                onEndCall();
                finish();
            }else if("com.tricheer.PHONE_STATE".equals(action)){ //电话状态改变对方挂断
                if(Utile.getInstance().isCall(mContext)){
                    Log.e(TAG, "ansewer finish");
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.i( "[onkeyDown]=" + keyCode);
        if (isAnswering) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_1:
                    onKeypressDown('1');
                    break;
                case KeyEvent.KEYCODE_2:
                    onKeypressDown('2');
                    break;
                case KeyEvent.KEYCODE_3:
                    onKeypressDown('3');
                    break;
                case KeyEvent.KEYCODE_4:
                    onKeypressDown('4');
                    break;
                case KeyEvent.KEYCODE_5:
                    onKeypressDown('5');
                    break;
                case KeyEvent.KEYCODE_6:
                    onKeypressDown('6');
                    break;
                case KeyEvent.KEYCODE_7:
                    onKeypressDown('7');
                    break;
                case KeyEvent.KEYCODE_8:
                    onKeypressDown('8');
                    break;
                case KeyEvent.KEYCODE_9:
                    onKeypressDown('9');
                    break;
                case KeyEvent.KEYCODE_0:
                    onKeypressDown('0');
                    break;
                case KeyEvent.KEYCODE_STAR:
                    onKeypressDown('*');
                    break;
                case KeyEvent.KEYCODE_POUND:
                    onKeypressDown('#');
                    break;
                case KeyEvent.KEYCODE_DEL: //消去
                    Utile.getInstance().deleteAll(tv_dialer_number);
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER: //去电

                    break;
            }
        }
            if(KeyEvent.KEYCODE_F6 == keyCode){ //136 免提
                Log.e(TAG,"免提开关");
                if(openSpeak){
                    openSpeaker();
                    openSpeak = false;
                }else {
                    closeSpeaker();
                    openSpeak =true;
                }

            }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }

    /**
     * 接通后输入按键事件
     * DTMF 事件响应
     *
     * @param number
     */
    public void onKeypressDown(char number) {
        Utile.getInstance().change(tv_dialer_number, String.valueOf(number));
        phone.sendDtmf(number);
    }
    /**
     * 打开扬声器
     */
    private void openSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            Log.e(TAG, "currVolume=" + currVolume);

            if (!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                // audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(mContext, "扬声器已经开启", Toast.LENGTH_SHORT).show();
    }

    /**
     * 关闭扬声器
     */
    public void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(mContext, "扬声器已经关闭", Toast.LENGTH_SHORT).show();
    }

}
