package telephony.internal.android.com.phoneui;

import android.app.Application;
import android.content.Context;

/**
 * Created by yangbofeng on 2018/7/10.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

    }

    public static Context getContext() {
        return mContext;
    }
}
