package telephony.internal.android.com.phoneui.Dialer.Blackdb;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yangbofeng on 2018/6/13.
 */

public class BlackListObserver extends ContentObserver {
    private static final String TAG = "observer";
    private Context mContext;
    private static  ArrayList<String> blacklist; //黑名单集合
    private Handler mhandler;

    public BlackListObserver(Context mContext, Handler handler) {
        super(handler);
        this.mContext = mContext;
        this.mhandler = handler;
        blacklist = new ArrayList<>();
    }

    @Override
    public void onChange(boolean selfChange) {
        // TODO Auto-generated method stub
        super.onChange(selfChange);
        Log.e(TAG, "onChange");
        new Thread(new Runnable() {
            @Override
            public void run() {
                BlackDBoperations.getInstance().qury(mContext, blacklist);
            }
        }).start();

    }


    public static  boolean isBlackList(String number) {
        if (blacklist.size() <= 0) {
            return false;
        }
        for (String list : blacklist) {
            if (list.equals(number)) {
                Log.e(TAG,"number="+list);
                return true;
            }
        }
        return false;
    }

    public static  int getBlackListSize(){
        return blacklist.size();
    }
}
