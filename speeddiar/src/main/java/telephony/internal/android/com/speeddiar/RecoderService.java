package telephony.internal.android.com.speeddiar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by yangbofeng on 2018/6/22.
 */

public class RecoderService extends Service {
    AudioRecoderUtils au;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
         au = new AudioRecoderUtils();
        au.startRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        au.stopRecord();
    }
}
