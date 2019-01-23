package telephony.internal.android.com.phoneui.utiles;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import telephony.internal.android.com.phoneui.Dialer.Blackdb.BlackListObserver;
import telephony.internal.android.com.phoneui.mode.PhoneNumberData;

/**
 * Created by yangbofeng on 2018/6/28.
 */

public class CallRecordsUtil {
    private String TAG = "CallRecordsUtil";

    private static CallRecordsUtil instance = new CallRecordsUtil();

    private CallRecordsUtil() {
    }

    public static CallRecordsUtil getInstance() {
        return instance;
    }

    /**
     * 获取通话记录
     * @param ListIn
     * @param mContext
     */
    public void getDataListIn(List<PhoneNumberData> ListIn, Context mContext, Handler mhandler) {
        try {
            ContentResolver resolver = mContext.getContentResolver();
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE}, "type = 1 or type = 3", null, "date DESC limit 30");
            // 3.通过Cursor获得数据
            Log.e(TAG, "cursor=" + cursor.getCount());
            ListIn.clear();
            while (cursor.moveToNext()) {
                int flagIdx = 0;
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                String date = new SimpleDateFormat("MM/dd HH:mm").format(new Date(dateLong));
                int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                //如果是黑名单里的，就不添加到通话记录列表
                if(BlackListObserver.isBlackList(number)){
                    continue;
                }
                String typeString = "";
                switch (type) {
                    case CallLog.Calls.INCOMING_TYPE:
                        typeString = "打入";
                        PhoneNumberData phdata = new PhoneNumberData();
                        phdata.setPhoneNumber(number);
                        phdata.setDate(date + "");
                        phdata.setStates(typeString);
                        phdata.setTimes(duration + "");
                        String names = findNames(mContext, number);
                        phdata.setName(names);
                        ListIn.add(phdata);
                        Log.e(TAG, "[name=" + name + "]" + "[number=" + number + "]" + "--" + "[date=" + date + "]--"
                                + "[duration=" + (duration) + "秒" + "]--" + "type=" + typeString);
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        typeString = "Miss";
                        PhoneNumberData phdata1 = new PhoneNumberData();
                        phdata1.setPhoneNumber(number);
                        phdata1.setDate(date + "");
                        phdata1.setStates(typeString);
                        phdata1.setTimes(duration / 60 + "");
                        String namess = findNames(mContext, number);
                        phdata1.setName(namess);
                        ListIn.add(phdata1);
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
                    mhandler.sendEmptyMessage(0);
                }
            }
            cursor.close();
            mhandler.sendEmptyMessage(0);
        } catch (Exception e) {
        }
        ;

    }

    /**
     * 获取去电数据
     * @param ListOut
     * @param mContext
     * @param mhandler
     */
    public void getDataListOut(List<PhoneNumberData> ListOut, Context mContext, Handler mhandler) {
        ContentResolver resolver = mContext.getContentResolver();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE}, "type = 2", null, "date DESC limit 30");
        // 3.通过Cursor获得数据
        ListOut.clear();
        while (cursor.moveToNext()) {
            int flagIdx = 0;
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = new SimpleDateFormat("MM/dd HH:mm").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));

            String typeString = "";
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    typeString = "打入";
                    Log.e(TAG, "[name=" + name + "]" + "[number=" + number + "]" + "--" + "[date=" + date + "]--"
                            + "[duration=" + (duration) + "秒" + "]--" + "type=" + typeString);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    typeString = "Miss";

                    Log.e(TAG, "[name=" + name + "]" + "[number=" + number + "]" + "--" + "[date=" + date + "]--"
                            + "[duration=" + (duration) + "秒" + "]--" + "type=" + typeString);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    typeString = "打出";
                    PhoneNumberData phdataOut = new PhoneNumberData();
                    phdataOut.setPhoneNumber(number);
                    phdataOut.setDate(date + "");
                    phdataOut.setStates(typeString);
                    phdataOut.setTimes(duration + "");
                    String namesss = findNames(mContext, number);
                    phdataOut.setName(namesss);
                    ListOut.add(phdataOut);
                    break;
                default:
                    break;
            }
            flagIdx++;
            if (ListOut.size() % 3 == 0 || flagIdx == (cursor.getCount() - 1)) {
                mhandler.sendEmptyMessage(0);
            }
        }
        cursor.close();
        mhandler.sendEmptyMessage(0);
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
        Log.e(TAG, "find name");
        String name = "";
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
        Cursor cursor = contentResolver.query(uri, null, "number1 =? or number2 = ? or number3 = ? ", new String[]{number}, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
                Log.e(TAG, "name=" + name);
            }

        }
        if (name.equals("") || name == null) {
            cursor.close();
            return queryAllContact(mContext, number);
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
    public String queryAllContact(Context mContext, String numb) {
        Log.e(TAG, "findSIM");
        String names = "";
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
            Log.d(TAG, "simcardinfo=" + name + "----" + number + "---" + emails + "---" + number3 + "---" + id + "--" + id4);
            if (numb.equals(number)) {
                names = name;
                break;
            } else {
                names = "";
            }
        }
        cursor.close();
        Log.e(TAG, "findSIM end");
        return names;

    }

    /**
     * 获取总通话时间
     * @param mContext
     * @param mhandler
     */
    public void getTotalDuration(Context mContext, Handler mhandler) {

        ContentResolver resolver = mContext.getContentResolver();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE}, "type = 2 or type = 1", null, CallLog.Calls.DEFAULT_SORT_ORDER);
        // 3.通过Cursor获得数据
        long incoming = 0L;
        long outgoing = 0L;
        while (cursor.moveToNext()) {
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));

            String typeString = "";
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    typeString = "打入";
                    incoming += duration;
                case CallLog.Calls.MISSED_TYPE:
                    typeString = "Miss";
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    typeString = "打出";
                    outgoing += duration;
                    break;
                default:
                    break;
            }
        }
        cursor.close();
        Log.e(TAG, "通话总时长=" + (incoming + outgoing));
        SettingsUtil.getInstance().setdata(mContext, "times",(incoming + outgoing) + "秒");
        mhandler.sendEmptyMessage(1);
    }
    /**
     * 拨打电话
     */


    /**
     * 删除指定通话记录
     *
     * @param number
     */
    public void deletLastCallLog(Context mContext, String number) {
        boolean isCallOut = false;
        ContentResolver resolver = mContext.getContentResolver();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{"_id"}, "number=? and (type=1 or type =2 or type=3)", new String[]{number}, "_id desc limit 1");
        Log.e(TAG, "result=" + cursor.moveToFirst());
        if (cursor.moveToFirst()) {

            int id = cursor.getInt(0);
            resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id + ""});
        }
        cursor.close();
    }

    /**
     * 删除所有（ALL）通话记录
     */

    public void DeleteCallAll(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
        Log.e(TAG, "detele all" );

    }


}
