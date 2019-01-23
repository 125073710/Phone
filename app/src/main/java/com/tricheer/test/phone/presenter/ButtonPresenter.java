package com.tricheer.test.phone.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.CallLog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.tricheer.test.phone.model.PhoneNumberData;
import com.tricheer.test.phone.utiles.Utiles;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yangbofeng on 2018/6/7.
 */

public class ButtonPresenter  implements  Ibutton{

    private static final String TAG ="ButtonPresenter_ybf" ;
    private  IRefresh iRefresh;
    private Context mContext;
    private StringBuffer sb;


    public ButtonPresenter(IRefresh iRefresh,Context mContext) {
        this.iRefresh =iRefresh;
        this.mContext = mContext;

    }


    @Override
    public void call(TextView view,Context mContext) {
        String  number  ="";
        /**
         * 拨打电话
         */
          number = (String) view.getText();
        if(Utiles.getInstance().isCall(mContext) &&Utiles.getInstance().isSimUsed(mContext)){
            try {
                ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));

                if(!number.isEmpty()){
                    Log.e(TAG,"[number=]"+number);
                    iTel.call(mContext.getPackageName(),number );
                }else{
                    Toast.makeText(mContext,"号码为空",Toast.LENGTH_SHORT).show();
                }

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
    @Override
    public void end() {
        if(Utiles.getInstance().isCalling(mContext)&& Utiles.getInstance().isSimUsed(mContext)){
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

    @Override
    public void delete(TextView tv_phoneNumber) {
        if (tv_phoneNumber.getText() != null && tv_phoneNumber.getText().length() > 1) {
            StringBuffer sb = new StringBuffer(tv_phoneNumber.getText());
            tv_phoneNumber.setText(sb.substring(0, sb.length() - 1));
        } else if (tv_phoneNumber.getText() != null && !"".equals(tv_phoneNumber.getText())) {
            tv_phoneNumber.setText("");
        }
    }

    /**
     * 暗码控制处
     * @param tv_phoneNumber
     * @param number
     */
    @Override
    public void change(TextView tv_phoneNumber,String number) {
         sb = new StringBuffer(tv_phoneNumber.getText());
        StringBuffer numbers = sb.append(number);
        Log.e(TAG,"number= "+numbers);
        tv_phoneNumber.setText(numbers);
        String testNumber = numbers.toString();
        if(testNumber.equals("*#*#123#*#*")){
            Log.e(TAG,"[open test A]");
        }else if(testNumber.equals("*#*#1#*#*")){
            Log.e(TAG,"[open test B]");
        }
    }

    public void cleanNumber(TextView tv_phoneNumber){
        if(sb!= null){
            int  sb_length = sb.length();// 取得字符串的长度
            sb.delete(0,sb_length);
            tv_phoneNumber.setText("");
        }

    }
    @Override
    public void getDataListOut(List<PhoneNumberData>  ListOut ,Handler mhandler) {
        // 1.获得ContentResolver
        ContentResolver resolver = mContext.getContentResolver();
        // 2.利用ContentResolver的query方法查询通话记录数据库
        /**
         * @param uri 需要查询的URI，（这个URI是ContentProvider提供的）
         * @param projection 需要查询的字段
         * @param selection sql语句where之后的语句
         * @param selectionArgs ?占位符代表的数据
         * @param sortOrder 排序方式  CallLog.Calls.DEFAULT_SORT_ORDER
         * "date DESC limit 10"
         */
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI,
                new String[] { CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.DATE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.TYPE },
                "type = 2", null,
                "date DESC limit 30"

        );

        // 3.通过Cursor获得数据
        ListOut.clear();
        while (cursor.moveToNext()) {
            int flagIdx = 0;
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));

            String typeString = "";
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    typeString = "打入";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    typeString = "未接";
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    typeString = "打出";
                    PhoneNumberData phdataOut = new PhoneNumberData();
                    phdataOut.setPhoneNumber(number);
                    phdataOut.setDate(date+"");
                    phdataOut.setStates(typeString);
                    phdataOut.setTimes(duration+"");
                    String names= Utiles.getInstance().findNames(mContext,number);
                    phdataOut.setName(names);
                    ListOut.add(phdataOut);
                    break;
                default:
                    break;
            }

            Log.e(TAG, "[name=" + name + "]" + "[number=" + number + "]" + "--" + "[date=" + date + "]--"
                    + "[duration=" + (duration) + "秒" + "]--" + "type=" + typeString);
            flagIdx++;
            if (ListOut.size() % 3 == 0 || flagIdx == (cursor.getCount() - 1)) {
                mhandler.sendEmptyMessage(4);
            }
        }
        cursor.close();
        mhandler.sendEmptyMessage(4);

    }

    @Override
    public void getDataListIn(List<PhoneNumberData>   ListIn, Handler mhandler) {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI,
                new String[] { CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.DATE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.TYPE },
                "type = 1", null,
                "date DESC limit 30"
        );
        // 3.通过Cursor获得数据
        ListIn.clear();
        while (cursor.moveToNext()) {
            int flagIdx = 0;
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));

            String typeString = "";
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    typeString = "打入";
                    PhoneNumberData phdata = new PhoneNumberData();
                    phdata.setPhoneNumber(number);
                    phdata.setDate(date+"");
                    phdata.setStates(typeString);
                    phdata.setTimes(duration+"");
                    String names= Utiles.getInstance().findNames(mContext,number);
                    phdata.setName(names);
                    ListIn.add(phdata);
                    Log.e(TAG, "[name=" + name + "]" + "[number=" + number + "]" + "--" + "[date=" + date + "]--"
                            + "[duration=" + (duration) + "秒" + "]--" + "type=" + typeString);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    typeString = "未接";
                 /*   PhoneNumberData phdata1 = new PhoneNumberData();
                    phdata1.setPhoneNumber(number);
                    phdata1.setDate(date+"");
                    phdata1.setStates(typeString);
                    phdata1.setTimes(duration/60+"");
                    String namess= Utiles.getInstance().findNames(mContext,number);
                    phdata1.setName(namess);
                    ListIn.add(phdata1);*/
                    Log.e(TAG, "[name=" + name + "]" + "[number=" + number + "]" + "--" + "[date=" + date + "]--"
                            + "[duration=" + (duration) + "秒" + "]--" + "type=" + typeString);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    typeString = "打出";
                    break;
                default:
                    break;
            }
            flagIdx++;
            if (ListIn.size() % 3 == 0 || flagIdx == (cursor.getCount() - 1)) {
                mhandler.sendEmptyMessage(5);
            }
        }
        cursor.close();
        mhandler.sendEmptyMessage(5);
    }

    @Override
    public void getDataListUncall(List<PhoneNumberData>   ListUncall,Handler mhandler) {

        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI,
                new String[] { CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.DATE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.TYPE },
                "type = 3 ", null,
                "date DESC limit 30"

        );
        // 3.通过Cursor获得数据
        ListUncall.clear();
        while (cursor.moveToNext()) {
            int flagIdx = 0;
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));

            String typeString = "";
            switch (type) {
                case CallLog.Calls.MISSED_TYPE:
                    typeString = "未接";
                    PhoneNumberData phdata1 = new PhoneNumberData();
                    phdata1.setPhoneNumber(number);
                    phdata1.setDate(date+"");
                    phdata1.setStates(typeString);
                    phdata1.setTimes(duration+"");
                    String names= Utiles.getInstance().findNames(mContext,number);
                    phdata1.setName(names);
                    ListUncall.add(phdata1);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    typeString = "打出";
                    break;
                default:
                    break;
            }

            Log.e(TAG, "[name=" + name + "]" + "[number=" + number + "]" + "--" + "[date=" + date + "]--"
                    + "[duration=" + (duration) + "秒" + "]--" + "type=" + typeString);
            flagIdx++;
            if (ListUncall.size() % 3 == 0 || flagIdx == (cursor.getCount() - 1)) {
                mhandler.sendEmptyMessage(6);
            }

        }
        cursor.close();
        mhandler.sendEmptyMessage(6);
    }


}
