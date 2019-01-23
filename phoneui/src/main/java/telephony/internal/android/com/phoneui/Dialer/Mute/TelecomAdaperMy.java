package telephony.internal.android.com.phoneui.Dialer.Mute;

import android.os.Looper;
import android.telecom.Phone;

import com.android.internal.util.Preconditions;

/**
 * Created by yangbofeng on 2018/7/12.
 */

public class TelecomAdaperMy implements InCallPhoneList {
    private static TelecomAdaperMy sInstance;
    private Phone mPhone;
    static TelecomAdaperMy getInstance() {
        Preconditions.checkState(Looper.getMainLooper().getThread() == Thread.currentThread());
        if (sInstance == null) {
            sInstance = new TelecomAdaperMy();
        }
        return sInstance;
    }
    @Override
    public void setPhone(Phone phone) {
        this.mPhone =phone;
    }

    @Override
    public void clearPhone() {

    }
}
