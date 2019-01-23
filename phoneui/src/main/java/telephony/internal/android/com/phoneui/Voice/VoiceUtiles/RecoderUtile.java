package telephony.internal.android.com.phoneui.Voice.VoiceUtiles;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangbofeng on 2018/6/21.
 */

public class RecoderUtile {

    private static RecoderUtile instance = new RecoderUtile();

    public static RecoderUtile getInstance() {
        return instance;
    }

    //文件路径
    private String filePath;
    //文件夹路径
    private String FolderPath;
    //电话号码
    private String number;
    //name
    private String name;

    private MediaRecorder mMediaRecorder;
    private final String TAG = "AudioRecoderUtils";
    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;


    /**
     * 文件存储默认sdcard/record
     */
    public RecoderUtile(){

        //默认保存路径为/sdcard/record/下
        this(Environment.getExternalStorageDirectory()+"/Default_Voice/");
        Log.e(TAG,"path="+Environment.getExternalStorageDirectory()+"/Default_Voice/");
    }

    public RecoderUtile(String filePath) {

        File path = new File(filePath);
        if(!path.exists())
            path.mkdirs();

        this.FolderPath = filePath;
    }

    private long startTime;
    private long endTime;



    /**
     * 开始录音 使用amr格式
     *      录音文件
     * @return
     */
    public void startRecord() {

        this.number =number;
        this.name = name;
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null){
            mMediaRecorder = new MediaRecorder();
        }else {
            Log.e(TAG,"startRecord()");
            return;
        }
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// // 采集MIC
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 0 是未播放 ，1 是播放
            filePath = FolderPath + "UerVoice"+".amr";
            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
           // updateMicStatus();
            Log.e(TAG, "startTime" + startTime);
        } catch (IllegalStateException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (mMediaRecorder == null)
            return 0L;
        endTime = System.currentTimeMillis();

        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            filePath = "";

        }catch (RuntimeException e){
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        }
        Log.e(TAG,"录音时间长度"+(endTime-startTime));
        return endTime - startTime;
    }

    /**
     * 取消录音
     */
    public void cancelRecord(){

        try {

            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

        }catch (RuntimeException e){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        File file = new File(filePath);
        if (file.exists())
            file.delete();

        filePath = "";

    }


    //时间格式化
    public String   getTime(){  
        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmm");
        Date d1=new Date(time);  
        String t1=format.format(d1);  
        Log.e(TAG, t1);
        return t1;
    } 

}
