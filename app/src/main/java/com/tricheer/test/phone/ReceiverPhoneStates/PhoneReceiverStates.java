package com.tricheer.test.phone.ReceiverPhoneStates;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tricheer.test.phone.utiles.Utiles;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by yangbofeng on 2018/6/11.
 */

public class PhoneReceiverStates extends BroadcastReceiver {
    private String TAG = "PhoneReceiverStates";
    private TelephonyManager mTelephonyManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "[action]=" + action);
        //去电状态
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            //刷新界面，为去电状态
            if (Utiles.getInstance().isSimUsed(context)) {
                Calling(phoneNumber);
                Log.e(TAG, "call OUT:" + phoneNumber);
            } else {
                SIM_INVALID();
            }

        } else {
            //查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
            //如果我们想要监听电话的拨打状况，需要这么几步 :
            Log.e(TAG, "来电======================================");
            mTelephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        }

    }

    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e(TAG, "挂断");
                    EndCallPhone();
                    mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e(TAG, "接听");
                    AnswerThePhone();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(TAG, "响铃:来电号码" + incomingNumber);
                    //输出来电号码
                    PhoneBellring(incomingNumber);
                    break;
            }


        }
    };


    public interface IPhoneReceiverStates {

        /**
         * 无SIM卡
         */
        void NO_SIM();

        /**
         * 去电中
         */
        void onCalling(String number);

        /**
         * 挂断电话
         */
        void onEndCallPhone();

        /**
         * 接听电话
         */
        void onAnswerThePhone();

        /**
         * 响铃电话
         */
        void onPhoneBellring(String number);
    }

    protected static Map<String, IPhoneReceiverStates> mMapNotifys = new HashMap<String, IPhoneReceiverStates>();

    public static void registerNotify(String notifyKey, IPhoneReceiverStates mIPhoneReceiverStates) {
        if (!mMapNotifys.containsKey(notifyKey)) {
            mMapNotifys.remove(notifyKey);
            mMapNotifys.put(notifyKey, mIPhoneReceiverStates);
        }
    }

    public static void removeNotify(String notifyKey) {
        if (mMapNotifys.containsKey(notifyKey)) {
            mMapNotifys.remove(notifyKey);
        }
    }


    //去电中 out
    public void Calling(String phonenumber) {
        try {
            for (IPhoneReceiverStates notify : mMapNotifys.values()) {
                notify.onCalling(phonenumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //挂断电话
    public void EndCallPhone() {
        try {
            for (IPhoneReceiverStates notify : mMapNotifys.values()) {
                notify.onEndCallPhone();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //接听电话
    public void AnswerThePhone() {
        try {
            for (IPhoneReceiverStates notify : mMapNotifys.values()) {
                notify.onAnswerThePhone();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 响铃电话
    public void PhoneBellring(String incomingNumber) {
        try {
            for (IPhoneReceiverStates notify : mMapNotifys.values()) {
                notify.onPhoneBellring(incomingNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void SIM_INVALID() {
        try {
            for (IPhoneReceiverStates notify : mMapNotifys.values()) {
                notify.NO_SIM();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
