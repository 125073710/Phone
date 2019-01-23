package telephony.internal.android.com.phoneui.Voice.db;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * Created by yangbofeng on 2018/6/13.
 * 监听数据库改变
 */

public class VoiceListObserver extends ContentObserver {
    private static final String TAG = "VoiceListObserver";
    private Context mContext;
    private Handler mhandler;

    public VoiceListObserver(Context mContext, Handler handler) {
        super(handler);
        this.mContext = mContext;
        this.mhandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        // TODO Auto-generated method stub
        super.onChange(selfChange);
        Log.e(TAG, "onChange");

    }

}
