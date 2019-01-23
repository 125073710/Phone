package telephony.internal.android.com.phoneui.Dialer.Mute;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telecom.InCallService;

/**
 * Created by yangbofeng on 2018/7/12.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class ICallservice extends InCallService {
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
