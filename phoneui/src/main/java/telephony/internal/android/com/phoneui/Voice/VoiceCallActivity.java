package telephony.internal.android.com.phoneui.Voice;

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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import telephony.internal.android.com.phoneui.Dialer.Utiles.CallUtiles;
import telephony.internal.android.com.phoneui.Dialer.Utiles.Utile;
import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.Voice.VoiceUtiles.AudioRecoderUtils;
import telephony.internal.android.com.phoneui.Voice.VoiceUtiles.VoiceUtile;
import telephony.internal.android.com.phoneui.Voice.db.VoiceDBoperations;
import telephony.internal.android.com.phoneui.Voice.mode.VoiceBean;
import telephony.internal.android.com.phoneui.utiles.CallRecordsUtil;
import telephony.internal.android.com.phoneui.utiles.SettingsUtil;
import telephony.internal.android.com.phoneui.utiles.Utiles;

/**
 * Created by yangbofeng on 2018/7/11.
 * 来电后，此界面处理录音逻辑
 */

public class VoiceCallActivity extends Activity {
    private String TAG = "VoiceCallActivity";
    private Context mContext;
    private TextView tv_voice_title, tv_voice_name, tv_voice_times, tv_voice_number, tv_recoder_state;
    private VoiceCallActivity.FinishActivityBoardcast finishActivityReceiver;
    private LinearLayout ly_callin;
    private FrameLayout fram_recoder;
    private String callNumber;
    private Handler mThreadHandler;
    private String name;
    private static final int PHONE_CALL_NAME = 0;
    private boolean isup = false; //留守状态是否拿起电话筒
    private ArrayList<VoiceBean> list = new ArrayList<>();
    private String Path = "";
    private int totalTimes = 150*60;//150分钟
    private ArrayList<String> notplaylist = new ArrayList<>();
    private int notplaySize =0;

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PHONE_CALL_NAME:
                    tv_voice_name.setText(name);
                    break;
                case 1: //响铃时间接听电话
                    tv_voice_title.setText("Voicemail");
                    tv_recoder_state.setText("<<Getting>>");
                    answerCall();
                    if (name == null || "".equals(name)) {
                        AudioRecoderUtils.getInstance().startRecord(mContext,callNumber, "null","0");
                    } else {
                        AudioRecoderUtils.getInstance().startRecord(mContext,callNumber, name,"0");
                    }
                    ly_callin.setVisibility(View.GONE);
                    fram_recoder.setVisibility(View.VISIBLE);
                    //设置录音时间长度、获取设置的录音时间值
                    if (!isup) { //没拿起话筒，则设置录音时间
                        int recodingTime =10*1000;//SettingsUtil.getInstance().getdataInt(mContext, "RECODING_TIME");
                        mhandler.sendEmptyMessageDelayed(2, recodingTime);
                    }
                    break;
                case 2: //挂断
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CallUtiles.getInstance().end(mContext);
                        }
                    }).start();

                    finish();
                    break;
                case 3: //处理录音时间是否超过150分钟
                    int recodingtotal = msg.arg1;
                    Log.e(TAG, "use time is =[已录音]" + recodingtotal);
                    int surplus = 50 - recodingtotal; //可以录音时间长度
                    Log.e(TAG, "surplus time =[可录音]" + surplus*1000);
                    //获取录音设置时长
                    int duarytime = SettingsUtil.getInstance().getdataInt(mContext, "RECODING_TIME");
                    Log.e(TAG,"duarytime="+duarytime);
                    if (surplus <= 0) { //剩余时间小于零，直接删除最后一个文件
                        if (list.size() > 0) {
                            int positio = list.size() - 1;
                            deleteFile(positio);
                            VoiceDBoperations.getInstance().delete(mContext,list.get(positio).getPath());
                            Utiles.getInstance().getFilesInfo(mContext, list,mhandler);
                        }

                    } else { //大于零，则需要和录音设置时长做比较
                        if (surplus*1000 < duarytime) {
                            if (list.size() > 0) {
                                int positio = list.size() - 1;
                                deleteFile(positio);
                                VoiceDBoperations.getInstance().delete(mContext,list.get(positio).getPath());
                                Utiles.getInstance().getFilesInfo(mContext, list,mhandler);
                            }
                        } else {
                            Log.e(TAG, "录音空间够用");
                        }
                    }
                    break;
                case 4: //保存未读条数
                    SettingsUtil.getInstance().setdataInt(mContext,"NOT_PLAY",notplaySize);
                    break;
                case 5:
                    Log.e(TAG,"list size="+list.size());
                   checkMemory();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);
        mContext = getApplicationContext();
        initRecodingTime();
        initView();
        registerThread();
        initData();
        initBroadcast();
        initAutoAnswer();
    }

    /**
     * 初始化获取录音总时间，看录音空间是否可用
     */
    private void initRecodingTime() {
        Utiles.getInstance().getFilesInfo(mContext, list,mhandler);
    }

    /**
     * 检测录音空间是否够用
     */
    private void checkMemory() {

        if (list.size() == 0) {
            Message message = new Message();
            message.arg1 = 0;
            message.what = 3;//标志是哪个线程传
            mhandler.sendMessage(message);//发送message信息
        } else {
            //1.获取录音总时长-->获取总文件个数-->获取每个文件路径
            //获取录音总时间
            new Thread(new Runnable() {
                int totalTime = 0;
                @Override
                public void run() {
                    for (VoiceBean info : list) {
                        String path = info.getPath();
                        Log.e(TAG, "path=" + path);
                        int longtime = VoiceUtile.gettime(path);
                        totalTime += longtime;
                    }
                    Log.e(TAG, "录音时长=" + totalTime);
                    Message message = new Message();
                    message.arg1 = totalTime;
                    message.what = 3;//标志是哪个线程传
                    mhandler.sendMessage(message);//发送message信息
                }
            }).start();
        }

    }

    /**
     * 删除指定文件
     * 文件路径
     */
    private void deleteFile(int lastsize) {
        Log.e(TAG, "删除指定文件");
        if (list.size() == 1) {
            File file = new File(list.get(0).getPath());
            Log.e(TAG, "delete last one path=" + list.get(0).getPath());
            file.delete();
        } else if (list.size() > 0) {
            File file = new File(list.get(lastsize).getPath());
            Log.e(TAG, "delete path=" + list.get(lastsize).getPath());
            file.delete();
        }

    }

    /**
     * 自动接听电话时间
     */
    private void initAutoAnswer() {
        String answer_time = SettingsUtil.getInstance().getdata(mContext, "ANSWER_TIME");
        if ("".equals(answer_time) || answer_time == null) {
            answer_time = "10000";
        }
        long time = Long.valueOf(answer_time).longValue();
        Log.e(TAG, "anser time =" + time);
        mhandler.sendEmptyMessageDelayed(1, time);
    }

    /**
     * 注册子线程,接电话
     */
    public void registerThread() {
        HandlerThread mHandlerThread = new HandlerThread("VoiceCallThread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
    }

    /**
     * 初始化 传递过来数据
     */
    private void initData() {
        Intent intent = getIntent();
        callNumber = intent.getStringExtra("callOutNumber");
        tv_voice_number.setText(callNumber);
        ly_callin.setVisibility(View.VISIBLE);
        Log.e(TAG, "callNumber" + callNumber);
        getSpeakName();
        getVoicemilNotPlaySize();
    }

    /**
     * 初始化UI
     */
    private void initView() {
        tv_voice_title = findViewById(R.id.tv_voice_title);
        tv_voice_name = findViewById(R.id.tv_voice_name);
        tv_voice_times = findViewById(R.id.tv_voice_times);
        tv_voice_number = findViewById(R.id.tv_voice_number);
        tv_recoder_state = findViewById(R.id.tv_recoder_state);
        ly_callin = findViewById(R.id.ly_callin);
        fram_recoder = findViewById(R.id.fram_recoder);
    }


    //获取联系人名字
    Runnable getSpeakNames = new Runnable() {
        @Override
        public void run() {
            name = CallRecordsUtil.getInstance().findNames(mContext, callNumber);
            mhandler.sendEmptyMessage(PHONE_CALL_NAME);
        }
    };

    /**
     * 获取通话的人名
     */
    public void getSpeakName() {
        mThreadHandler.post(getSpeakNames);
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
     * 获取未播放录音条数
     */
    Runnable notplay = new Runnable() {
        @Override
        public void run() {
             notplaySize=  VoiceDBoperations.getInstance().queryNotPlay(mContext,notplaylist);
            mhandler.sendEmptyMessage(4);
        }
    };

    /**
     * 接电话
     */
    public void answerCall() {
        mThreadHandler.post(answerCall);
    }

    /**
     * 注册广播事件
     */
    private void initBroadcast() {
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.finish.activity");
        filter.addAction("com.tricheer.PHONE_STATE");//电话状态改变
        filter.addAction("android.hardware.input.action.HANDLE_UP");//接电话
        filter.addAction("android.hardware.input.action.HANDLE_DOWN");
        finishActivityReceiver = new VoiceCallActivity.FinishActivityBoardcast();
        //注册广播接收
        registerReceiver(finishActivityReceiver, filter);
    }

    /**
     * 获未播放的条数
     */
    public void getVoicemilNotPlaySize() {
        mThreadHandler.post(notplay);
    }

    /**
     * 广播事件
     */
    class FinishActivityBoardcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "action=" + action);
            if ("com.finish.activity".equals(action)) {
                finish();
            } else if ("com.tricheer.PHONE_STATE".equals(action)) { //电话状态改变对方挂断
                if (Utile.getInstance().isCall(mContext)) {
                    Log.e(TAG, "ansewer finish");
                    finish();
                }
            } else if ("android.hardware.input.action.HANDLE_UP".equals(action)) { //接听
                Log.e(TAG, "ansewer");
                isup = true;
                if (isup) {
                    answerCall();
                }

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioRecoderUtils.getInstance().stopRecord();
        unregisterReceiver(finishActivityReceiver);
        mThreadHandler.removeCallbacks(getSpeakNames);
        mThreadHandler.removeCallbacks(answerCall);
        mThreadHandler.removeCallbacks(notplay);
    }
}
