package com.tricheer.test.phone.utiles;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Locale;

/**
 * Created by yangbofeng on 2018/6/11.
 */

public class Utiles {
    private static final String TAG ="Utiles" ;
    public  final int SIM_STATE_READY = 5;
    /**
     * \
     * 字符切割
     */
    private static Utiles instance = new Utiles();

    private Utiles() {
    }

    public static Utiles getInstance() {
        return instance;
    }



    /**
     * 判断电话状态
     * 返回电话状态
     * CALL_STATE_IDLE 无任何状态时
     * CALL_STATE_OFFHOOK 接起电话时
     * CALL_STATE_RINGING 电话进来时
     */


    public  boolean isCalling(Context mcontext){
        TelephonyManager tm = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK){
            return  true;
        }else{
            return  false;
        }
    }

    /**
     * 判断是否有通话
     * @param mcontext
     * @return
     */
    public static boolean isCall(Context mcontext){
        TelephonyManager tm = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getCallState() == TelephonyManager.CALL_STATE_IDLE){
            return  true; //空闲状态
        }else{
            return  false;
        }
    }

    /**
     * 判断电话 是否为进来时
     * @param mcontext
     * @return
     */
    public  boolean isCallIn(Context mcontext){
        TelephonyManager tm = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getCallState() == TelephonyManager.CALL_STATE_RINGING){
            return  true;
        }else{
            return  false;
        }
    }

    /**
     * 判断系统语言
     * @return
     */
    public  static boolean isLanugEn(Context mContext) {
        String locale = Locale.getDefault().getLanguage();

        if (locale != null && (locale.trim().equals("ch") ))//en  英语
            return true;
        else
            return false;
    }

    /**
     * 判断sim卡是否可用
     */

    public boolean isSimUsed(Context mContext){
        TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Service.TELEPHONY_SERVICE);
        int state = tm.getSimState();
        if(SIM_STATE_READY ==state){
                return  true;
        }else{
                return  false;
        }
       /* switch (state) {
            case TelephonyManager.SIM_STATE_READY :
                simState = SIM_VALID;
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN :
            case TelephonyManager.SIM_STATE_ABSENT :
            case TelephonyManager.SIM_STATE_PIN_REQUIRED :
            case TelephonyManager.SIM_STATE_PUK_REQUIRED :
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED :
            default:
                simState = SIM_INVALID;
                break;
        }*/
    }



    ///////////////////////////////
    /**
     * 查找姓名
     *
     * @param mContext
     * @param number
     * @return
     */
    public String findNames(Context mContext, String number) {
        String name = "";
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        Cursor cursor = contentResolver.query(uri, null, "number1 = ? or number2 = ? or number3 = ?", new String[]{number}, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
                Log.e("DBoperations", "name=" + name);
            }

        }
        if (name.equals("") || name == null) {
             cursor.close();
            return queryAllContact(mContext,number);
        } else {
            cursor.close();
            return name;
        }
    }

    /**
     * 查询SIM卡数据库,获取联系人名字
     * @param numb
     * @return
     */
    public String  queryAllContact(Context mContext,String numb) {
        String names ="";
        Uri uri = Uri.parse("content://icc/adn");
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        Log.d(TAG, "cursor count=" + cursor.getCount());
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String number = cursor.getString(1);
            String emails = cursor.getString(2);
            String id = cursor.getString(3);
            String id4 = cursor.getString(4);
            int tag = cursor.getColumnIndex("_id");
            String number3 = cursor.getString(tag);
            Log.d(TAG, "simcardinfo=" + name + "----" + number + "---" + emails + "---" + number3+ "---"+id+"--"+id4);
            if(numb.equals(number)  ){
                names=  name;
                break;
            }else{
                names = "";
            }
        }
        cursor.close();
        return names;

    }
    ///////////////////////////////
}
