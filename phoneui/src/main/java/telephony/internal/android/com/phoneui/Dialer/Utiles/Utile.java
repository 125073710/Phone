package telephony.internal.android.com.phoneui.Dialer.Utiles;

import android.app.Service;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by yangbofeng on 2018/7/5.
 */

public class Utile {
    private String TAG = "Utile";
    private StringBuffer sb;
    private OpenActivity mOpenActivity;

    public final int SIM_STATE_READY = 5;

    private static Utile instance = new Utile();

    private Utile() {
    }

    public static Utile getInstance() {
        return instance;
    }

    /**
     * 判断sim卡是否可用
     */

    public boolean isSimUsed(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
        int state = tm.getSimState();
        if (SIM_STATE_READY == state) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 判断电话状态
     * 返回电话状态
     * CALL_STATE_IDLE 无任何状态时
     * CALL_STATE_OFFHOOK 接起电话时
     * CALL_STATE_RINGING 电话进来时
     */
    /**
     * CALL_STATE_OFFHOOK 接起电话时
     *
     * @param mcontext
     * @return
     */
    public boolean isCalling(Context mcontext) {
        TelephonyManager tm = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否有通话
     *
     * @param mcontext
     * @return
     */
    public boolean isCall(Context mcontext) {
        TelephonyManager tm = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
            return true; //空闲状态
        } else {
            return false;
        }
    }

    /**
     * 判断电话 是否为进来时
     *
     * @param mcontext
     * @return
     */
    public boolean isCallIn(Context mcontext) {
        TelephonyManager tm = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 暗码控制处，字符拼接
     *
     * @param tv_phoneNumber
     * @param number
     */
    public void change(TextView tv_phoneNumber, String number) {
        sb = new StringBuffer(tv_phoneNumber.getText());
        StringBuffer numbers = sb.append(number);
        Log.e(TAG, "number= " + numbers);
        tv_phoneNumber.setText(numbers);
        String testNumber = numbers.toString();
        if (testNumber.equals("*#123#*")) {
            mOpenActivity.OpenA();
        } else if (testNumber.equals("*#*#1#*#*")) {
            mOpenActivity.OpenB();
        }
    }

    /**
     * 删除字符
     *
     * @param tv_phoneNumber
     */
    public void delete(TextView tv_phoneNumber) {
        if (tv_phoneNumber.getText() != null && tv_phoneNumber.getText().length() > 1) {
            StringBuffer sb = new StringBuffer(tv_phoneNumber.getText());
            tv_phoneNumber.setText(sb.substring(0, sb.length() - 1));
        } else if (tv_phoneNumber.getText() != null && !"".equals(tv_phoneNumber.getText())) {
            tv_phoneNumber.setText("");
        }
    }

    /**
     * 通话过程中只能使用
     *
     * @param tv_phoneNumber
     */
    public void deleteAll(TextView tv_phoneNumber) {
        if (tv_phoneNumber.getText() != null && tv_phoneNumber.getText().length() > 1) {
            StringBuffer sb = new StringBuffer("");
            tv_phoneNumber.setText(sb.toString());
        } else if (tv_phoneNumber.getText() != null && !"".equals(tv_phoneNumber.getText())) {
            tv_phoneNumber.setText("");
        }
    }

    /**
     * 注册暗码监听
     *
     * @param mOpenActivity
     */
    public void setOpenActivityListener(OpenActivity mOpenActivity) {
        this.mOpenActivity = mOpenActivity;
    }


    public interface OpenActivity {
        /**
         * 暗码打开A
         */
        void OpenA();

        /**
         * 暗码打开B
         */
        void OpenB();
    }
}
