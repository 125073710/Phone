package telephony.internal.android.com.phoneui.utiles;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import telephony.internal.android.com.phoneui.Voice.db.VoiceDBoperations;
import telephony.internal.android.com.phoneui.Voice.mode.VoiceBean;

/**
 * Created by yangbofeng on 2018/7/2.
 */

public class Utiles {
    private String TAG = "Utiles";
    private static Utiles instance = new Utiles();

    private Utiles() {
    }

    public static Utiles getInstance() {
        return instance;
    }


    /**
     * 判断系统语言
     *
     * @return
     */
    public boolean isLanugEn(Context mContext) {
        String locale = Locale.getDefault().getLanguage();

        if (locale != null && (locale.trim().equals("en")))//en  英语
            return true;
        else
            return false;
    }


    /**
     * 判断是否为留守状态
     */
    public boolean initLiushou(Context mContext) {
        String isliushou = SettingsUtil.getInstance().getdata(mContext, "isLiushou");
        Log.e(TAG, "是否留守 =" + isliushou);
        if (isliushou.equals("YES")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取文件中个数
     */
    public int getVoicemailSize(String Path) {
        File file = new File(Path);
        String files[];
        files = file.list();
        int num = files.length;
        Log.e(TAG, "num=" + num);
        return num;
    }

    /**
     * 从数据库获取文件信息
     * path--/storage/sdcard0/YBF_record/20180718170613476_17629193325.amr
      */
    public void getFilesInfo(final Context mContext , final ArrayList<VoiceBean> list, final Handler mhandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                VoiceDBoperations.getInstance().qury(mContext,list);
                mhandler.sendEmptyMessage(5);
            }
        }).start();

    }

    /**
     * 获取目录下所有文件(按时间排)
     *  废弃掉，直接从数据库获取
     * @param path
     * @return
     */
    public ArrayList<File> getFileSort(String path) {
        ArrayList<File> list = getFiles(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }

                }
            });
        }

        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public ArrayList<File> getFiles(String realpath, ArrayList<File> files) {
        files.clear();
        File realFile = new File(realpath);
        File[] subfiles = realFile.listFiles();
        for (File file : subfiles) {
            if (file.isDirectory()) {
                getFiles(file.getAbsolutePath(), files);
            } else {
                files.add(file);
            }
        }
        Log.e(TAG, "file size=" + files.size());
        return files;
    }

    /**
     * 获取当前页面Activity 名字
     *
     * @param mContext
     * @return
     */
    public String getRunningActivityName(Context mContext) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }


    /**
     * 产生4位随机数
     * @return
     */
    public long getRandomNumber(){
        int n = 4;
        if(n<1){
            throw new IllegalArgumentException("随机数位数必须大于0");
        }
        return (long)(Math.random()*9*Math.pow(10,n-1)) + (long)Math.pow(10,n-1);
    }
}
