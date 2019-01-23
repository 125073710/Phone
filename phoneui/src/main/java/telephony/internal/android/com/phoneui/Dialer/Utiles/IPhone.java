package telephony.internal.android.com.phoneui.Dialer.Utiles;

/**
 * Created by yangbofeng on 2018/7/6.
 */

public interface IPhone {
    /**
     * 拨打电话
     */
    void  call(String number);
    /**
     * 挂电话
     */
    void  endCall();
    /**
     * 接电话
     */
    void answerCall();
}
