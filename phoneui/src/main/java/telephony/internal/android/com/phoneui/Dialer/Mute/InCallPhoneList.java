package telephony.internal.android.com.phoneui.Dialer.Mute;

import android.telecom.Phone;

/**
 * Created by yangbofeng on 2018/7/12.
 */

public interface InCallPhoneList  {
    /**
     * Called once at {@code InCallService} startup time with a valid {@code Phone}. At
     * that time, there will be no existing {@code Call}s.
     *
     * @param phone The {@code Phone} object.
     */
    void setPhone(Phone phone);

    /**
     * Called once at {@code InCallService} shutdown time. At that time, any {@code Call}s
     * will have transitioned through the disconnected state and will no longer exist.
     */
    void clearPhone();
}
