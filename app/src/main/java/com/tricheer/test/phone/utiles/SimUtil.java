package com.tricheer.test.phone.utiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.tricheer.test.phone.phonebook.PhoneContaceData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yangbofeng on 2018/6/25.
 */

public class SimUtil {
    private String TAG ="SimUtil";
    private static SimUtil instance = new SimUtil();

    private SimUtil() {
    }

    public static SimUtil getInstance() {
        return instance;
    }

    /**
     * 查询sim卡联系人
     */
    public void queryAllContact(Context mContext,ArrayList<PhoneContaceData> list,Handler mhandler) {
        list.clear();
        Uri uri = Uri.parse("content://icc/adn");
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        Log.d(TAG, "cursor count=" + cursor.getCount());
        while (cursor.moveToNext()) {
            int flagIdx = 0;
            String name = cursor.getString(0);
            String number = cursor.getString(1);
            String emails = cursor.getString(2);
            int tag = cursor.getColumnIndex("anrs");
            String number3 = cursor.getString(tag);
            PhoneContaceData phcontace = new PhoneContaceData();
            phcontace.setName(name);
            phcontace.setNumber1(number);
            phcontace.setNumber2(emails);
            phcontace.setNumber3(number3);
            list.add(phcontace);
            Log.d(TAG, "simcardinfo=" + name +"----"+ number + "---" + emails + "---" + number3);
            flagIdx++;
            if (list.size() % 3 == 0 || flagIdx == (cursor.getCount() - 1)) {
                mhandler.sendEmptyMessage(0);
            }
        }
        cursor.close();
        mhandler.sendEmptyMessage(0);
        //对集合排序
        Collections.sort(list, new Comparator<PhoneContaceData>() {
            @Override
            public int compare(PhoneContaceData lhs, PhoneContaceData rhs) {
                //根据拼音进行排序
                return lhs.getPinyin().compareTo(rhs.getPinyin());
            }
        });
    }
    /**
     * SIM卡写
     *
     * @param name
     */
    public void insertContact(String name,Context mContext) {
        ContentValues values = new ContentValues();
        Uri uri = Uri.parse("content://icc/adn");
        values.put("tag", name);
        Uri insertInfo = mContext.getContentResolver().insert(uri, values);
        Log.d(TAG, insertInfo.toString());
    }

    /**
     * 更新
     * @parm activity
     */
    public void SimUpdate(Context mContext, String name , String number1, String number2, String number3,
                          TextView tv_Name, TextView tv_number, TextView tv_number2, TextView tv_number3, Handler mhandler) {
        Uri uri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag",name );
        values.put("number",number1 );
        values.put("emails", number2);
        values.put("anrs",number3);
        values.put("newTag",tv_Name.getText().toString().trim() );
        values.put("newNumber", tv_number.getText().toString().trim());
        values.put("newEmails", tv_number2.getText().toString().trim());
        values.put("newAnrs", tv_number3.getText().toString().trim());
        mContext.getContentResolver().update(uri, values, null, null);
        mhandler.sendEmptyMessage(3);


    }

}
