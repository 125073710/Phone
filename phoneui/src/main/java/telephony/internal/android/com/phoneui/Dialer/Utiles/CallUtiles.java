package telephony.internal.android.com.phoneui.Dialer.Utiles;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.android.internal.telephony.ITelephony;

import telephony.internal.android.com.phoneui.view.MyToast;

/**
 * Created by yangbofeng on 2018/7/6.
 * 拨打电话工具类
 */

public class CallUtiles {
    private static CallUtiles instance = new CallUtiles();

    private CallUtiles() {
    }

    public static CallUtiles getInstance() {
        return instance;
    }

    /**
     * 拨打电话
     * @param mContext
     * @param number
     */
    public    void call(Context mContext, String number) {
        if(Utile.getInstance().isCall(mContext) &&Utile.getInstance().isSimUsed(mContext)){
            try {
                ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
                iTel.call(mContext.getPackageName(), number);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            MyToast.showToast(mContext,"NO SIM Card");
        }
    }

    /**
     * 挂断电话
     */
    public   void end(Context mContext) {
        if((Utile.getInstance().isCalling(mContext)|| Utile.getInstance().isCallIn(mContext))&& Utile.getInstance().isSimUsed(mContext)){
            try {
                ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
                iTel.endCall();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {

        }
    }
    /**
     * 接电话
     */
    public   void answerCall() {
        try {
            ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
            iTel.answerRingingCall();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
