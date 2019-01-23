package telephony.internal.android.com.phoneui.Voice;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.Voice.VoiceUtiles.VoiceUtile;
import telephony.internal.android.com.phoneui.Voice.db.VoiceDBoperations;
import telephony.internal.android.com.phoneui.Voice.mode.VoiceBean;
import telephony.internal.android.com.phoneui.utiles.Utiles;
import telephony.internal.android.com.phoneui.view.MyDialog;

/**
 * Created by yangbofeng on 2018/7/12.
 * 语音信箱，详细界面
 */

public class VoiceMessageActivity extends Activity implements View.OnClickListener {
    private String TAG = "VoiceMessageActivity";

    private ImageView img_new;
    private TextView tv_total_voice, tv_date_voice, tv_times_voice, tv_name_voice, tv_number_voice;
    private Button bt_voice_down, bt_voice_up, bt_voice_ok,bt_stop;
    private String Path = "";
    private ArrayList<VoiceBean> fileInfo = new ArrayList<>();
    private int position = 0;
    //dialog
    private MyDialog voiceDialog;
    private LinearLayout ly_voice_play;
    private LinearLayout ly_voice_delete;
    private LinearLayout ly_voice_delete_all;
    private LinearLayout ly_voice_Volume;
    private MediaPlayer mplayer;
    private FrameLayout fy_play_voice;
    private boolean isplaying = false;
    private Context mContext;
    private int totalTime = 0;

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 5://数据获取完毕
                    initData();
                    Log.e(TAG," fileInfo.size()="+ fileInfo.size());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_message);
        mContext = getApplicationContext();
        Path = Environment.getExternalStorageDirectory() + "/YBF_record/";
        initViewKey();
        initView();
    }


    /**
     * 初始化控制键
     */
    private void initViewKey() {
        bt_voice_down = findViewById(R.id.bt_voice_down);
        bt_voice_up = findViewById(R.id.bt_voice_up);
        bt_voice_ok = findViewById(R.id.bt_voice_ok);
        bt_voice_down.setOnClickListener(this);
        bt_voice_up.setOnClickListener(this);
        bt_voice_ok.setOnClickListener(this);
    }

    private void initView() {
        img_new = findViewById(R.id.img_new);
        tv_total_voice = findViewById(R.id.tv_total_voice);
        tv_date_voice = findViewById(R.id.tv_date_voice);
        tv_times_voice = findViewById(R.id.tv_times_voice);
        tv_name_voice = findViewById(R.id.tv_name_voice);
        tv_number_voice = findViewById(R.id.tv_number_voice);
        fy_play_voice = findViewById(R.id.fy_play_voice);
        Utiles.getInstance().getFilesInfo(mContext,fileInfo,mhandler);
        Log.e(TAG, "fileInfo size" + fileInfo.size());


       // initTimes();
    }

    /**
     * 在点击留守时去处理/在来电留守页面去处理
     * 获取录音总时间
     */
    private void initTimes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (VoiceBean info :fileInfo){
                    String path = info.getPath();
                    Log.e(TAG, "path=" + path);
                    int longtime = VoiceUtile.gettime(path);
                    totalTime +=longtime;
                    Log.e(TAG, "longtime=" + longtime);
                }
                mhandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initData() {
        if(fileInfo.size() ==0){
            tv_total_voice.setText("0");
            img_new.setVisibility(View.GONE);
            return;
        }else {
            tv_total_voice.setText(position + 1 + "/" + fileInfo.size() + "");
            String date = fileInfo.get(position).getDate();// 201801010102-->   2018/01/01/01/02
            Log.e(TAG, "date=" + date);
            tv_date_voice.setText(date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8));
            tv_times_voice.setText(date.substring(8, 10) + ":" + date.substring(10, 12));
            Log.e(TAG,"name="+fileInfo.get(position).getName());
            if ("null".equals(fileInfo.get(position).getName())) {
                tv_name_voice.setText("");
            } else {
                tv_name_voice.setText(fileInfo.get(position).getName());
            }
            tv_number_voice.setText(fileInfo.get(position).getNumber());
            String tag = fileInfo.get(position).getTag();
            Log.e(TAG,"tag="+tag);
            if ("0".equals(tag)) { //0 未播放；1 已播放
                img_new.setVisibility(View.VISIBLE);
            }else {
                img_new.setVisibility(View.GONE);
                Log.e(TAG,"已播放");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_voice_down:
                position += 1;
                if (position == fileInfo.size()) {
                    position = fileInfo.size() - 1;
                }
                Log.e(TAG, "position=" + position);
                initData();
                break;
            case R.id.bt_voice_up:
                position -= 1;
                if (position <= 0) {
                    position = 0;
                }
                Log.e(TAG, "position=" + position);
                initData();
                break;
            case R.id.bt_voice_ok:
                if (!isplaying) {
                    ShowDialog();
                }
                break;
            //dialog
            case R.id.ly_voice_play:
                Log.e(TAG, "player");
                if(!(fileInfo.size() == 0)){
                    player(position);
                    fy_play_voice.setVisibility(View.VISIBLE);
                    VoiceDBoperations.getInstance().updateTtp(mContext,fileInfo.get(position).getPath(),"1");
                }
                voiceDialog.dismiss();
                break;
            case R.id.ly_voice_delete:
                if(!(fileInfo.size() == 0)){
                    deleteFile(position);
                }
                voiceDialog.dismiss();
                finish();
                break;
            case R.id.ly_voice_delete_all:
                break;
            case R.id.ly_voice_Volume:

                break;
            case R.id.bt_stop:
                stopPlay();
                break;
        }
    }


    /**
     * 显示Dialog
     */
    public void ShowDialog() {
        voiceDialog = new MyDialog(this);
        View dialog_voice = LayoutInflater.from(this).inflate(R.layout.dialog_voice_message, null);
        voiceDialog.setLayoutView(dialog_voice);
        Window window = voiceDialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 300;
        p.height = 350;
        window.setAttributes(p);
        voiceDialog.setCanceledOnTouchOutside(false);
        voiceDialog.show();

        ly_voice_play = dialog_voice.findViewById(R.id.ly_voice_play);
        ly_voice_delete = dialog_voice.findViewById(R.id.ly_voice_delete);
        ly_voice_delete_all = dialog_voice.findViewById(R.id.ly_voice_delete_all);
        ly_voice_Volume = dialog_voice.findViewById(R.id.ly_voice_Volume);
        bt_stop = dialog_voice.findViewById(R.id.bt_stop);

        ly_voice_play.setOnClickListener(this);
        ly_voice_delete.setOnClickListener(this);
        ly_voice_delete_all.setOnClickListener(this);
        ly_voice_Volume.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
    }

    /**
     * 播放音乐
     */
    public void player(int position) {
        if(fileInfo.size() ==0){
            return;
        }
        String path = fileInfo.get(position).getPath();
        Log.e(TAG, "path=" + path);
        mplayer = new MediaPlayer();
        mplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mplayer.reset();
            mplayer.setDataSource(path);
            mplayer.prepare();
            mplayer.start();
            isplaying = true;
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
        isplaying = false;
        img_new.setVisibility(View.GONE);
        fy_play_voice.setVisibility(View.GONE);
    }
    /**
     * 设置播放监听
     */
    private void setPlayerComliteListener() {
        mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG, "播放完毕");
                stopPlay();
            }
        });
    }

    /**
     * 删除指定文件
     */
    private void deleteFile(int position){
        String path = fileInfo.get(position).getPath();
        File file = new File(path);
        file.delete();
        VoiceDBoperations.getInstance().delete(mContext,path);
    }

    /**
     * 删除所有文件
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    //删除文件夹
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
