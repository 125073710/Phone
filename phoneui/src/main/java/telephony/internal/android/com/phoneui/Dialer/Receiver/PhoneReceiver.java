package telephony.internal.android.com.phoneui.Dialer.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import telephony.internal.android.com.phoneui.Dialer.CallActivity;
import telephony.internal.android.com.phoneui.Dialer.Utiles.KeyEvents;
import telephony.internal.android.com.phoneui.Dialer.Utiles.Utile;
import telephony.internal.android.com.phoneui.DialerActivity;
import telephony.internal.android.com.phoneui.utiles.Utiles;

/**
 * Created by yangbofeng on 2018/7/9.
 */

public class PhoneReceiver extends BroadcastReceiver {
    private String TAG = "PhoneReceiver";
    private PhoneReceiver.IPhoneReceiver mIPhoneReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "[action]=" + action);
        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) { //去电
            Log.e(TAG, "[去]");
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            openCallActivity(context,phoneNumber,"OUT");
        } else if (action.equals("android.intent.action.PHONE_STATE")) { //来电
            //来电在phoneLinstener 里面打开界面,在响铃时去开启界面
            Intent intent3 = new Intent();
            intent3.setAction("com.tricheer.PHONE_STATE");
            context.sendBroadcast(intent3);
            phoneStateChange();
        } else if (action.equals(KeyEvents.ACTION_HANDLE_UP)) { //拿起话筒

            if("telephony.internal.android.com.phoneui.Voice.SettingActivity.VoiceSettingUserActivity".equals(Utiles.getInstance().getRunningActivityName(context))){
                Log.e(TAG,"class name="+Utiles.getInstance().getRunningActivityName(context));
                //如果界面是VoiceSettingUserActivity 时，不打开拨号页面
            }else {
                if(Utile.getInstance().isCall(context)){ //空闲状态拿起电话打开拨号界面
                    Intent intentUp = new Intent(context, DialerActivity.class);
                    intentUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentUp.putExtra("HandleUp", "HANDLE_UP");
                    context.startActivity(intentUp);
                }
            }

            handleUp();
        } else if (action.equals(KeyEvents.ACTION_HANDLE_DOWN)) { //挂断电话
            handleDown();
        }else if(action.equals("android.intent.action.BOOT_COMPLETED")){ //开机广播启动监听服务

        }
    }


    /**
     * 打开通话界面
     *
     * @param context
     * @param phoneNumber
     */
    public void openCallActivity(Context context, String phoneNumber, String tag) {
        Intent intentOut = new Intent(context, CallActivity.class);
        intentOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentOut.putExtra("callOutNumber", phoneNumber);
        intentOut.putExtra("tag", tag);
        context.startActivity(intentOut);
    }

    public interface IPhoneReceiver {

        /**
         * 拿起手柄
         */
        void onhandleUp();

        /**
         * 放下手柄
         */
        void onhandleDown();

        /**
         * 电话状态改变
         */
        void onPhoneStateChange();

    }

    protected static Map<String, IPhoneReceiver> mMapNotifys = new HashMap<String, IPhoneReceiver>();

    public static void registerNotify(String notifyKey, IPhoneReceiver mIPhoneReceiver) {
        if (!mMapNotifys.containsKey(notifyKey)) {
            mMapNotifys.remove(notifyKey);
            mMapNotifys.put(notifyKey, mIPhoneReceiver);
        }
    }

    public static void removeNotify(String notifyKey) {
        if (mMapNotifys.containsKey(notifyKey)) {
            mMapNotifys.remove(notifyKey);
        }
    }


    //拿起手柄
    public void handleUp() {
        try {
            for (IPhoneReceiver notify : mMapNotifys.values()) {
                notify.onhandleUp();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //放下手柄
    public void handleDown() {
        try {
            for (IPhoneReceiver notify : mMapNotifys.values()) {
                notify.onhandleDown();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
    //放下手柄
    public void phoneStateChange() {
        try {
            for (IPhoneReceiver notify : mMapNotifys.values()) {
                notify.onPhoneStateChange();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
