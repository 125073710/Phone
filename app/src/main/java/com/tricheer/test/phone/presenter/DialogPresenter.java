package com.tricheer.test.phone.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.tricheer.test.phone.utiles.Utiles;

/**
 * Created by yangbofeng on 2018/6/12.
 */

public class DialogPresenter {
    private static final String TAG = "DialogPresenter";
    //拨打电话
               /*
                if (!iscall) {
                    bt.callRecoder(lists.get(i).getPhoneNumber());
                     //删除一条电话记录
                bt.deleteCall(lists.get(i).getPhoneNumber());
                }*/
    private Context mContext;
    private IRefresh iRefresh;

    public DialogPresenter(IRefresh iRefresh, Context mContext) {
        this.mContext = mContext;
        this.iRefresh = iRefresh;
    }


    /**
     * 拨打电话
     */
    public void call(String number) {
        boolean iscall = Utiles.getInstance().isCall(mContext);
        Log.e(TAG, "iscall" + iscall);
        if (iscall) {
            callRecoder(number);
        }
    }

    public void callRecoder(String number) {
        //拨打通话记录
        if(Utiles.getInstance().isSimUsed(mContext)){
            try {
                ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
                iTel.call(mContext.getPackageName(), number);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(mContext,"无SIM卡",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 删除所有记录
     */
    public void DeleteCallAll() {
        ContentResolver resolver = mContext.getContentResolver();
        resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
        Log.e(TAG, "detele all" );
        iRefresh.Delete_Refresh_All();
    }

    /**
     * 删除指定通话记录
     *
     * @param callLog
     */
    public void deletLastCallLog(String number) {
        boolean isCallOut = false;
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{"_id"}, "number=? and (type=1 or type =2 or type=3)", new String[]{number}, "_id desc limit 1");
        Log.e(TAG, "result="+cursor.moveToFirst());
        if (cursor.moveToFirst()) {

            int id = cursor.getInt(0);
            resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id + ""});
        }
        cursor.close();
        iRefresh.Deleted_Refresh();
    }
}
