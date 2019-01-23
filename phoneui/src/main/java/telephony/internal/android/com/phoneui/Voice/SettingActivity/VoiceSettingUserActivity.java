package telephony.internal.android.com.phoneui.Voice.SettingActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.Voice.VoiceUtiles.RecoderUtile;
import telephony.internal.android.com.phoneui.view.MyDialog;

/**
 * Created by yangbofeng on 2018/7/17.
 * Setting&UserVoice&
 */

public class VoiceSettingUserActivity extends Activity implements View.OnClickListener {
    private String TAG ="VoiceSettingUserActivity";
    private LinearLayout ly_voice_user_recoding, ly_voice_user_play, ly_voice_user_delete;
    private MyDialog dialog;
    private MyDialog dialogEnter;
    private FinishActivityBoardcast finishActivityReceiver;
    private TextView tv_voice_yes,tv_voice_no;
    private FrameLayout framly_voice;
    private MediaPlayer mplayer;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user_voice);
        initView();
        initBroadcast();
    }

    private void initView() {
        ly_voice_user_recoding = findViewById(R.id.ly_voice_user_recoding);
        ly_voice_user_play = findViewById(R.id.ly_voice_user_play);
        ly_voice_user_delete = findViewById(R.id.ly_voice_user_delete);
        framly_voice = findViewById(R.id.framly_voice);
        ly_voice_user_recoding.setOnClickListener(this);
        ly_voice_user_play.setOnClickListener(this);
        ly_voice_user_delete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_voice_user_recoding:
                ShowDialog();
                break;
            case R.id.ly_voice_user_play:
                player();
                break;
            case R.id.ly_voice_user_delete:
                deleteFile();
                break;
            case R.id.tv_voice_yes://开始录音
                Log.e(TAG,"yes");
                deleteFile();
                dialogEnter.dismiss();
                framly_voice.setVisibility(View.VISIBLE);
                RecoderUtile.getInstance().startRecord();
                mHandler.sendEmptyMessageDelayed(0,15000);
                break;
            case R.id.tv_voice_no:

                break;
        }
    }
    /**
     * 播放音乐
     */
    public void player() {
        String path = "/storage/sdcard0/Default_Voice/UerVoice.amr";
        Log.e(TAG, "path=" + path);
        mplayer = new MediaPlayer();
        mplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mplayer.reset();
            mplayer.setDataSource(path);
            mplayer.prepare();
            mplayer.start();
            framly_voice.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPlayerComliteListener();
    }

    /**
     * 暂停播放
     */
    public void stopPlay(){
        if (mplayer != null && mplayer.isPlaying()) {
            mplayer.stop();
            mplayer.release();
            mplayer = null;
        }
    }
    /**
     * 设置播放监听
     */
    private void setPlayerComliteListener() {
        mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG, "播放完毕");
                framly_voice.setVisibility(View.GONE);
            }
        });
    }
    /**
     * 删除指定文件
     */
    private void  deleteFile() {
        File file = new File("/storage/sdcard0/Default_Voice/UerVoice.amr");
        file.delete();
    }
    /**
     * 显示Dialog
     */
    public void ShowDialog() {
        dialog = new MyDialog(this);
        View dialog_voice = LayoutInflater.from(this).inflate(R.layout.dialog_voice_setting_user, null);
        dialog.setLayoutView(dialog_voice);
        Window window = dialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 300;
        window.setAttributes(p);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 显示Dialog 确认 取消
     */
    public void ShowDialogEnter() {
        dialogEnter = new MyDialog(this);
        View dialog_voice = LayoutInflater.from(this).inflate(R.layout.dialog_voice_setting_user_enter, null);
        dialogEnter.setLayoutView(dialog_voice);
        Window window = dialogEnter.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 300;
        p.height = 200;
        window.setAttributes(p);
        dialogEnter.setCanceledOnTouchOutside(false);
        dialogEnter.show();

        tv_voice_yes =  dialog_voice.findViewById(R.id.tv_voice_yes);
        tv_voice_no =  dialog_voice.findViewById(R.id.tv_voice_no);
        tv_voice_yes.setOnClickListener(this);
        tv_voice_no.setOnClickListener(this);

    }


    /**
     * 注册广播事件
     */
    private void initBroadcast() {
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.input.action.HANDLE_UP");//接电话
        filter.addAction("android.hardware.input.action.HANDLE_DOWN");
        finishActivityReceiver = new FinishActivityBoardcast();
        //注册广播接收
        registerReceiver(finishActivityReceiver, filter);
    }

    /**
     * 广播事件
     */
    class FinishActivityBoardcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "action=" + action);
            if ("android.hardware.input.action.HANDLE_UP".equals(action)) { //接听
                dialog.dismiss();
                ShowDialogEnter();
            } else if ("android.hardware.input.action.HANDLE_DOWN".equals(action)) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
        RecoderUtile.getInstance().stopRecord();
        unregisterReceiver(finishActivityReceiver);
    }
}
