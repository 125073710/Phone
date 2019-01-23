package com.tricheer.test.phone.phonebook.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.tricheer.test.phone.phonebook.PhoneContaceData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yangbofeng on 2018/6/13.
 * 电话本数据查询工具类
 */

public class DBoperations {


    private String TAG ="DBoperations";

    /**
     * //插入数据到数据库
     * @param mContext
     * @param name
     */
    public void insert(Context mContext,String name,String jpname) {

        ContentResolver contentResolver =mContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("jpname", jpname);
        Uri uri = contentResolver.insert(insertUri, values);
    }

    public  void delete(Context mContext,String name){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        Cursor cursor = contentResolver.query(uri, null,"name=?", new String[]{name}, null);
        if(cursor.moveToFirst()){
            contentResolver.delete(uri, "name=?", new String[]{name});

        }
    }

    /**
     * 查询数据库
     * @param db
     */
    public void querydata(SQLiteDatabase db, ArrayList<PhoneContaceData> list, Handler mhandler){
        //查询数据
        Cursor cursor = db.query("data", null, null, null, null, null, null);
            list.clear();
        while (cursor.moveToNext()) {
            int flagIdx = 0;
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String jpname = cursor.getString(cursor.getColumnIndex("jpname"));
            String  number1= cursor.getString(cursor.getColumnIndex("number1"));
            String number2 = cursor.getString(cursor.getColumnIndex("number2"));
            String number3 = cursor.getString(cursor.getColumnIndex("number3"));
            Log.e(TAG,"[name=]"+name+"--[number1=]"+number1+"--[number2]"+number2+"--[number3]="+number3);

            PhoneContaceData phcontace = new PhoneContaceData();
            phcontace.setName(name);
            phcontace.setJpname(jpname);
            phcontace.setNumber1(number1);
            phcontace.setNumber2(number2);
            phcontace.setNumber3(number3);
            list.add(phcontace);
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
     *修改名字
     * @param mContext
     * @param name
     */
    public void updateName(Context mContext,String name,String newname){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        ContentValues values = new ContentValues();
        values.put("name",newname);
        int updateConut = contentResolver.update(uri, values,"name = ?", new String[]{name});
        Log.e(TAG, "更新成功 name" );
    }

    /**
     *修改姓
     * @param mContext
     * @param name
     */
    public void updatejpName(Context mContext,String name,String jpname){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        ContentValues values = new ContentValues();
        values.put("jpname",jpname);
        int updateConut = contentResolver.update(uri, values,"name = ?", new String[]{name});
        Log.e(TAG, "更新成功 frist name" );
    }

    /**
     *修改电话号码1
     * @param mContext
     * @param number1
     */
    public void updateNumber1(Context mContext,String name,String number1){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        ContentValues values = new ContentValues();
        values.put("number1",number1);
        int updateConut = contentResolver.update(uri, values, "name = ?", new String[]{name});
        Log.e(TAG, "更新成功 number1 " );
    }

    /**
     *
     * @param mContext
     * @param number2
     */
    public void updateNumber2(Context mContext,String name,String number2){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        ContentValues values = new ContentValues();
        values.put("number2",number2);
        int updateConut = contentResolver.update(uri, values, "name = ?", new String[]{name});
        Log.e(TAG, "更新成功 number2" );
    }

    /**
     *
     * @param mContext
     * @param number3
     */
    public void updateNumber3(Context mContext,String name,String number3){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        ContentValues values = new ContentValues();
        values.put("number3",number3);
        int updateConut = contentResolver.update(uri, values, "name = ?", new String[]{name});
        Log.e(TAG, "更新成功 number3" );
    }

    /**
     * 清空数据表
     * @param db
     */
    public void DeleteAll(SQLiteDatabase db){
       // db.execSQL("drop table if exists  data");//删除表格
        db.execSQL("DELETE FROM data"); //前清空表格
    }


    /**
     * 根据电话号码查姓名
     * @return
     */

    public  void   findName(Context mContext,String number1) {

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        Cursor cursor =contentResolver.query(uri,null,"number1 = ?", new String[]{number1},null);
        String  name;
        while (cursor.moveToNext()) {
           name = cursor.getString(cursor.getColumnIndex("name"));
            Log.e("DBoperations","name="+name);
        }
    }
}
