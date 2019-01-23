package telephony.internal.android.com.phoneui.Dialer.Receiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.Dialer.Blackdb.BlackListObserver;
import telephony.internal.android.com.phoneui.Dialer.CallActivity;
import telephony.internal.android.com.phoneui.Dialer.Utiles.CallUtiles;
import telephony.internal.android.com.phoneui.MainActivity;
import telephony.internal.android.com.phoneui.Voice.VoiceCallActivity;
import telephony.internal.android.com.phoneui.utiles.Utiles;

/**
 * Created by yangbofeng on 2018/7/9.
 * 来电状态监听  来电录音
 */

public class PhoneLinstener {
    private String TAG = "PhoneLinstener";
    private TelephonyManager mTelephonyManager;
    private ArrayList<String> blacklist;
    private Context mContext;

    private static PhoneLinstener instance = new PhoneLinstener();

    private PhoneLinstener() {
        blacklist = new ArrayList<>();
    }

    public static PhoneLinstener getInstance() {
        return instance;
    }


    /**
     * 注册来电状态监听
     *
     * @param mContext
     */
    public void registerPhoneLintener(Context mContext) {
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
        mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        this.mContext = mContext;
    }

    /**
     * 取消来电状态监听
     */
    public void unRisterPhoneLintener() {
        mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * 电话状态监听
     */
    PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e(TAG, "finish");
                    Intent intent3 = new Intent();
                    intent3.setAction("com.finish.activity");
                    mContext.sendBroadcast(intent3);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e(TAG, "通话中" + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(TAG, "来电号码" + incomingNumber + BlackListObserver.isBlackList(incomingNumber));
                    if (BlackListObserver.isBlackList(incomingNumber)) { //黑名单
                        CallUtiles.getInstance().end(MainActivity.getContext());
                    } else {
                        /**
                         * 1.不在黑名单中的电话，判断是否开启留守状态
                         * 2.打开来电界面
                         */
                        if(!Utiles.getInstance().initLiushou(mContext)){
                            Log.e(TAG,"call in open activity");
                            openCallActivity(MainActivity.getContext(), incomingNumber, "IN",CallActivity.class);
                        }else {
                            Log.e(TAG,"留守 开");
                            openCallActivity(MainActivity.getContext(), incomingNumber, "IN",VoiceCallActivity.class);
                        }

                    }
                    break;
            }
        }
    };

    /**
     * 打开通话界面
     *
     * @param context
     * @param phoneNumber
     */
    public void openCallActivity(Context context, String phoneNumber, String tag,Class cls) {
        Intent intentOut = new Intent(context, cls);
        intentOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentOut.putExtra("callOutNumber", phoneNumber);
        intentOut.putExtra("tag", tag);
        context.startActivity(intentOut);
    }



}