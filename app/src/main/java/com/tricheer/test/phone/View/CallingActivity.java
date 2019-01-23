package com.tricheer.test.phone.View;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallManager;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.tricheer.test.phone.R;
import com.tricheer.test.phone.utiles.CallUtiles;
import com.tricheer.test.phone.utiles.AudioRecoderUtils;
import com.tricheer.test.phone.utiles.MyTimerTask;

/**
 * Created by yangbofeng on 2018/6/15.
 */

public class CallingActivity extends Activity  {

    private String TAG = "CallingActivity_ybf";
    private TextView tv_time;
    private MyTimerTask myTimerTask;

    private CallManager mCM;
    private static final int PHONE_STATE_CHANGED = 102;

    private FinishActivityBoardcast finishActivityReceiver;
    private Context mContext;
    private int currVolume;
    private Button bt_speak;
    private Button bt_stop_spea;
    //private int currVolume;
    private AudioManager audioManager;
    private   AudioRecoderUtils audioRecoderUtils;


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "updatePhoneSateChange()");
            switch (msg.what) {
                case PHONE_STATE_CHANGED:
                    updatePhoneSateChange();
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        tv_time = findViewById(R.id.tv_time);
        mContext = getApplicationContext();
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        Log.e(TAG, "[currVolume]=" + currVolume);
        initButton();
        initBroadcast();
        initCallOut();
         audioRecoderUtils = new AudioRecoderUtils();
    }


    //初始化监听去电状态
    private void initCallOut() {
        mCM = CallManager.getInstance();
        Phone phone = PhoneFactory.getDefaultPhone();
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
                break;
            case ACTIVE://接通状态
                Log.e(TAG, "ACTIVE");
                myTimerTask = new MyTimerTask(tv_time);
                myTimerTask.startTimer();
                break;
            default:
                break;
        }

    }

    private void initBroadcast() {
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.tricheer.finish.activity");
        filter.addAction("com.start.timer");
        finishActivityReceiver = new FinishActivityBoardcast();
        //注册广播接收
        registerReceiver(finishActivityReceiver, filter);
    }

    private void initButton() {
        Button bt_end = findViewById(R.id.bt_call_end);
        bt_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CallUtiles.end(mContext);
                    }
                }).start();
                finish();
            }
        });
        Button bt_call_answer = findViewById(R.id.bt_call_answer);
        bt_call_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "answercall");

                //开始录音
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CallUtiles.answerCall();
                     //   audioRecoderUtils.startRecord();
                    }
                }).start();

            }
        });


        bt_speak = findViewById(R.id.bt_spea);
        bt_stop_spea = findViewById(R.id.bt_stop_spea);
        bt_stop_spea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "close_speak");
                closeSpeaker();
            }
        });
        bt_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开免提
                Log.e(TAG, "openSpeak");
                openSpeaker();
            }
        });

    }
    /**
     * 通话录音
     */



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

    //关闭扬声器
    public void CloseSpeaker() {

        try {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
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

    class FinishActivityBoardcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "action=" + action);
            if (action.equals("com.tricheer.finish.activity")) {
                finish();
            } else if (action.equals("com.start.timer")) {
                myTimerTask.startTimer();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishActivityReceiver);
        myTimerTask.stopTimer();
        //停止录音
        audioRecoderUtils.stopRecord();
        Log.e(TAG, "finish CallActivity");
    }
}
