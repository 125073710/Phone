package telephony.internal.android.com.phoneui.Dialer.Blackdb;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yangbofeng on 2018/6/13.
 * 电话本数据查询工具类
 */

public class BlackDBoperations {
    private String TAG = "BlackDBoperations";

    private static BlackDBoperations instance = new BlackDBoperations();

    private BlackDBoperations() {
    }

    public static BlackDBoperations getInstance() {
        return instance;
    }

    /**
     * //插入数据到数据库
     *
     * @param mContext
     */
    public void insert(Context mContext, String number) {

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.tricheer.blacklist/blackdata");
        ContentValues values = new ContentValues();
        values.put("number", number);
        Uri uri = contentResolver.insert(insertUri, values);
    }


    public void delete(Context mContext, String name) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.blacklist/blackdata");
        Cursor cursor = contentResolver.query(uri, null, "name=?", new String[]{name}, null);
        if (cursor.moveToFirst()) {
            contentResolver.delete(uri, "name=?", new String[]{name});

        }
    }

    /**
     * 修改名字
     *
     * @param mContext
     */
    public void updateName(Context mContext, String number, String newNumber) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.blacklist/blackdata");
        ContentValues values = new ContentValues();
        values.put("number", newNumber);
        int updateConut = contentResolver.update(uri, values, "number = ?", new String[]{newNumber});
        Log.e(TAG, "更新成功 number");
    }


    /**
     * 清空数据表
     *
     * @param db
     */
    public void DeleteAll(SQLiteDatabase db) {
        // db.execSQL("drop table if exists  data");//删除表格
        db.execSQL("DELETE FROM blackdata"); //前清空表格
    }

    /**
     * 查询数据
     */

    public void qury(Context mContext, ArrayList<String> list) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.blacklist/blackdata");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        Log.e(TAG, "[qury]=" + cursor.getCount());
        list.clear();
        while (cursor.moveToNext()) {
            String blacknumber = cursor.getString(cursor.getColumnIndex("number"));
            Log.e(TAG,"blacknumber="+blacknumber);
            list.add(blacknumber);
        }
        cursor.close();
    }
}
