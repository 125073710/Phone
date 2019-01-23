package telephony.internal.android.com.phoneui.Dialer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import telephony.internal.android.com.phoneui.Dialer.Blackdb.BlackDatabaseHelper;
import telephony.internal.android.com.phoneui.Dialer.Blackdb.BlackListObserver;
import telephony.internal.android.com.phoneui.Dialer.Receiver.PhoneLinstener;
import telephony.internal.android.com.phoneui.Voice.VoiceStateOberver;
import telephony.internal.android.com.phoneui.Voice.db.VoiceDatabaseHelper;

/**
 * Created by yangbofeng on 2018/7/5.
 */

public class PhoneService extends Service {
    private String TAG = "PhoneService";
    private Context mContext;
    private Handler mhandler = new Handler();
    private BlackListObserver blackListObserver;
    private VoiceStateOberver mVoiceStateOberver;
    SQLiteDatabase blackdb;
    SQLiteDatabase voicedb;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreat");
        mContext = getApplicationContext();
          setPhoneLinsenter(true);
        intBlackListDB();
        initVoiceListDb();
        setBlackChangLinstener();
        setVoiceStateLinstener();
        initLog();
    }

    /**
     * 初始化log
     */
    private void initLog() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("PhoneUI")//（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        //关闭log
      /*  Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return false;
            }
        });*/
    }

    /**
     * 初始化黑名单数据库
     */
    private void intBlackListDB() {
        BlackDatabaseHelper mBlackDatabaseHelper = new BlackDatabaseHelper(mContext, "blacklist.db", null, 1);
        blackdb = mBlackDatabaseHelper.getReadableDatabase();
    }
    /**
     * 初始化语音信箱数据库
     */
    private void initVoiceListDb(){
        VoiceDatabaseHelper mVoiceDatabaseHelper = new VoiceDatabaseHelper(mContext,"voicelist.db",null,1);
        voicedb = mVoiceDatabaseHelper.getReadableDatabase();
    }

    /**
     * 注册留守状态改变监听
     */
    public void setVoiceStateLinstener() {
         mVoiceStateOberver = new VoiceStateOberver(mContext, mhandler);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("isLiushou"), true,
                mVoiceStateOberver);
    }

    /**
     * 注册黑名单改变内容观察者
     */
    public void setBlackChangLinstener() {
        blackListObserver = new BlackListObserver(mContext, mhandler);
        Uri uri = Uri.parse("content://com.tricheer.blacklist/blackdata");
        getContentResolver().registerContentObserver(uri, true, blackListObserver);
    }

    /**
     * 注册来电监听
     */
    public void setPhoneLinsenter(boolean isregeist) {
        if (isregeist) {
            PhoneLinstener.getInstance().registerPhoneLintener(mContext);
        } else {
            PhoneLinstener.getInstance().unRisterPhoneLintener();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
          setPhoneLinsenter(false);
        getContentResolver().unregisterContentObserver(blackListObserver);
        getContentResolver().unregisterContentObserver(mVoiceStateOberver);
    }
}
