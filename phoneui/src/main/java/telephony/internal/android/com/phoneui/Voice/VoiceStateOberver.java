package telephony.internal.android.com.phoneui.Voice;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb.ContactObserver;
import telephony.internal.android.com.phoneui.Voice.VoiceUtiles.VoiceUtile;
import telephony.internal.android.com.phoneui.Voice.db.VoiceDBoperations;
import telephony.internal.android.com.phoneui.Voice.mode.VoiceBean;
import telephony.internal.android.com.phoneui.utiles.SettingsUtil;
import telephony.internal.android.com.phoneui.utiles.Utiles;

/**
 * Created by yangbofeng on 2018/7/11.
 * 监听留守状态改变 ，去处理文件空间大小是否可用
 */

public class VoiceStateOberver extends ContactObserver {
    private String TAG = "VoiceStateOberver";
    private Context mContext;
    private boolean isliushou = false;
    private int totalTimes = 150*60*1000;
    private ArrayList<VoiceBean> list = new ArrayList<>(); //数据库
    private String Path = "";
    ArrayList<String> files = new ArrayList<>(); //文件夹


    public VoiceStateOberver(Context mContext, Handler handler) {
        super(mContext, handler);
        this.mContext = mContext;
       //可以自己构造适配器默认：AndroidLogAdapter

    }

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 3:
                    int recodingtotal = msg.arg1;
                    Log.e(TAG, "use time is =[已录音]" + recodingtotal);
                    int surplus = totalTimes - recodingtotal*1000; //可以录音时间长度
                    Log.e(TAG, "surplus time =[可录音]" + surplus*1000);
                    //获取录音设置时长
                    int duarytime =SettingsUtil.getInstance().getdataInt(mContext, "RECODING_TIME");
                    Log.e(TAG,"duarytime=[设置录音时间]"+duarytime);
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
                            //进行数据库对比，看是否匹配
                            getFielMetch();
                        }
                    }
                    break;
                case 5:
                    Log.e(TAG,"list size="+list.size());
                    checkMemory();
                    break;

            }
        }
    };

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        isliushou = Utiles.getInstance().initLiushou(mContext);
        Log.e(TAG, "onchange" + "留守状态--" + isliushou);
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
        Log.e(TAG, "list size = " + list.size());
        if (list.size() == 1) {
            File file = new File(list.get(0).getPath());
            Log.e(TAG, "delete path=" + list.get(0).getPath());
            file.delete();
        } else if (list.size() > 0) {
            File file = new File(list.get(lastsize).getPath());
            Log.e(TAG, "delete path=" + list.get(lastsize).getPath());
            file.delete();
        }
    }
    /**
     *对文件进行匹配 ,确保数据库和文件数目一样
     * list 数据库
     * files 文件夹
     */
   public  void   getFielMetch(){
       getFiles("/storage/sdcard0/YBF_record");
        if(list.size() > files.size()){ //数据库 大于文件夹  -->删除数据库多余的
            Log.e(TAG,"数据库多"); //做非空检测
            for (int i = 0; i < list.size(); i++) {
                String path = list.get(i).getPath();
                File file = new File(path);
                boolean isexist =  file.exists();
               if(isexist){
                   Log.e(TAG,"文件存在");
               }else {
                   VoiceDBoperations.getInstance().delete(mContext,path);
               }
            }
        }else if(list.size() < files.size()) { //数据库 小于 文件数 --->删除多余的文件
            Log.e(TAG,"文件多");
            for (int i = 0; i <files.size() ; i++) {
                String path = files.get(i).toString();
              int excess =   VoiceDBoperations.getInstance().queryFile(mContext,path);
                if(excess == 0){
                    deleteFile(i);
                }
            }
        }else  if(list.size() == files.size()){
            Logger.t("VoiceStateOberver").e("数据一致");
        }

    }


    /**
     * 获取目录下所有文件
     * @return
     */
    public ArrayList<String> getFiles(String realpath) {
        files.clear();
        File realFile = new File(realpath);
        File[] subfiles = realFile.listFiles();
        for (File file : subfiles) {
            if (file.isDirectory()) {
                getFiles(file.getAbsolutePath());
            } else { //如果是文件
                files.add(file.toString());
            }
        }
        Log.e(TAG, "file size=" + files.size());
        return files;
    }
}
