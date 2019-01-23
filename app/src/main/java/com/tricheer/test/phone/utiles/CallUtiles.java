package com.tricheer.test.phone.utiles;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

/**
 * Created by yangbofeng on 2018/6/15.
 */

public class CallUtiles {
    /**
     * 拨打电话
     * @param mContext
     * @param number
     */
    public  static  void call(Context mContext,String number) {
        if(Utiles.getInstance().isCall(mContext) &&Utiles.getInstance().isSimUsed(mContext)){
            try {
                ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
                iTel.call(mContext.getPackageName(), number);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(mContext,"无有效SIM卡",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 挂断电话
     */
    public static  void end(Context mContext) {
        if((Utiles.getInstance().isCalling(mContext)|| Utiles.getInstance().isCallIn(mContext))&& Utiles.getInstance().isSimUsed(mContext)){
            try {
                ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
                iTel.endCall();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(mContext,"无有效SIM卡",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 接电话
     */
    public static  void answerCall() {
        try {
            ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
            iTel.answerRingingCall();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
