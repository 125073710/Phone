package telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * Created by yangbofeng on 2018/6/13.
 *
 */

public class ContactObserver extends ContentObserver {
    private static final String TAG = "observer";
    private Context mContext;
    private Handler handler;
    public ContactObserver(Context mContext, Handler handler) {
        super(handler);
        this.mContext = mContext;
        this.handler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        // TODO Auto-generated method stub
        super.onChange(selfChange);
        Log.e(TAG, "change");
        handler.sendEmptyMessage(1);
    }

}
