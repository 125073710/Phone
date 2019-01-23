package telephony.internal.android.com.phoneui.Voice.VoiceUtiles;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by yangbofeng on 2018/7/15.
 */

public class VoiceUtile {
    /**
     * 获取录音文件总时间
     * @param string
     * @return
     */
    public static int gettime(String string) {   //使用此方法可以直接在后台获取音频文件的播放时间，而不会真的播放音频
        MediaPlayer player = new MediaPlayer();  //首先你先定义一个mediaplayer
        try {
            player.setDataSource(string);  //String是指音频文件的路径
            player.prepare();        //这个是mediaplayer的播放准备 缓冲

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {//监听准备

            @Override
            public void onPrepared(MediaPlayer player) {
                int size = player.getDuration();
                String timelong = size / 1000 + "''";

            }
        });
        double size = player.getDuration();//得到音频的时间
        int  timelong1 = (int) Math.ceil((size / 1000));//转换为秒 单位为 秒
        player.stop();//暂停播放
        player.release();//释放资源
        return timelong1;  //返回音频时间
    }

}
