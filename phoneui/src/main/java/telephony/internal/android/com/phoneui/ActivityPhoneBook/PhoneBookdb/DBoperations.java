package telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import telephony.internal.android.com.phoneui.mode.PhoneContaceData;
import telephony.internal.android.com.phoneui.utiles.Utiles;

/**
 * Created by yangbofeng on 2018/6/13.
 * 电话本数据查询工具类
 */

public class DBoperations {

    private int REFERSH_ADAPTER =0;
    private int ABC_PEOPLE_SIZE =1; //联系人每个字母中的数
    private String TAG ="DBoperations";

    private static DBoperations instance = new DBoperations();

    private DBoperations() {
    }

    public static DBoperations getInstance() {
        return instance;
    }

    /**
     * //插入数据到数据库,给名字追加4位随机数确保名字唯一性
     * @param mContext
     * @param name
     */
    public void insert(Context mContext,String name,String jpname) {

        ContentResolver contentResolver =mContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.tricheer.phone.phonebook/data");
        ContentValues values = new ContentValues();
        values.put("name", name+getRandomNumber());
        values.put("jpname", jpname);
        Uri uri = contentResolver.insert(insertUri, values);
    }
    /**
     * 产生4位随机数
     * @return
     */
    public long getRandomNumber(){
        int n = 4;
        if(n<1){
            throw new IllegalArgumentException("随机数位数必须大于0");
        }
        return (long)(Math.random()*9*Math.pow(10,n-1)) + (long)Math.pow(10,n-1);
    }
    /**
     * 插入姓名电话
     * @param mContext
     * @param name
     */
    public void insertNameAndNumber(Context mContext,String name,String number) {

        ContentResolver contentResolver =mContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.tricheer.phone.phonebook/data");
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("number1", number);
        Uri uri = contentResolver.insert(insertUri, values);
    }

    public  void delete(Context mContext,String name){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
        Cursor cursor = contentResolver.query(uri, null,"name=?", new String[]{name}, null);
        if(cursor.moveToFirst()){
            contentResolver.delete(uri, "name=?", new String[]{name});

        }
    }

    /**
     * 查询数据库
     * @param db
     */
    public void querydata(SQLiteDatabase db, ArrayList<PhoneContaceData> list, Handler mhandler,String word,Context mContext){
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
            Log.e(TAG,"[name=]"+name+"+[jpname=]"+jpname+"--[number1=]"+number1+"--[number2]"+number2+"--[number3]="+number3);

            PhoneContaceData phcontace = new PhoneContaceData();
            phcontace.setName(name);
            phcontace.setJpname(jpname);
            phcontace.setNumber1(number1);
            phcontace.setNumber2(number2);
            phcontace.setNumber3(number3);

            String pinyin = phcontace.getHeaderWord().toUpperCase();
            boolean isEn = Utiles.getInstance().isLanugEn(mContext);
            Log.e(TAG,"words="+word+"---piny--"+pinyin);
            if(isEn){ //英语
                Log.e(TAG,"英语");
                if(word.equals(pinyin)){
                    list.add(phcontace);
                }else {
                    if ("#".equals(word)) {
                        if ("A".equals(pinyin) || "B".equals(pinyin) || "C".equals(pinyin) ||
                                "D".equals(pinyin) || "E".equals(pinyin) || "F".equals(pinyin) ||
                                "G".equals(pinyin) || "H".equals(pinyin) || "I".equals(pinyin) ||
                                "J".equals(pinyin) || "K".equals(pinyin) || "L".equals(pinyin) ||
                                "M".equals(pinyin) || "N".equals(pinyin) || "O".equals(pinyin) ||
                                "P".equals(pinyin) || "Q".equals(pinyin) || "R".equals(pinyin) ||
                                "S".equals(pinyin) || "T".equals(pinyin) || "U".equals(pinyin) ||
                                "V".equals(pinyin) || "W".equals(pinyin) || "X".equals(pinyin) ||
                                "Y".equals(pinyin) || "Z".equals(pinyin)) {
                            continue;
                        } else {
                            list.add(phcontace);
                        }
                    }
                }
            }else {
                Log.e(TAG,"日语");
                if(word.equals(pinyin)){
                    list.add(phcontace);
                }else { //あかさ たなは まやら わ他
                    if("あ".equals(word)||"か".equals(word)||"さ".equals(word)||
                            "た".equals(word)||"な".equals(word)||"は".equals(word)||
                            "ま".equals(word)||"や".equals(word)||"ら".equals(word)||
                            "わ".equals(word)){
                        continue;
                    }else {
                        list.add(phcontace);
                    }
                }
            }

            flagIdx++;
            if (list.size() % 3 == 0 || flagIdx == (cursor.getCount() - 1)) {
                mhandler.sendEmptyMessage(ABC_PEOPLE_SIZE);
            }
        }

        cursor.close();
        mhandler.sendEmptyMessage(REFERSH_ADAPTER);
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
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
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
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
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
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
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
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
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
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
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
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
        Cursor cursor =contentResolver.query(uri,null,"number1 = ?", new String[]{number1},null);
        String  name;
        while (cursor.moveToNext()) {
           name = cursor.getString(cursor.getColumnIndex("name"));
            Log.e("DBoperations","name="+name);
        }
    }

    /**
     * 查询数据
     */

    public  void qury(Context mContext,ArrayList<PhoneContaceData> list, Handler mhandler){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        Log.e(TAG,"[qury]="+cursor.getCount());
        list.clear();
        while (cursor.moveToNext()) {
            int flagIdx = 0;
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String jpname = cursor.getString(cursor.getColumnIndex("jpname"));
            String  number1= cursor.getString(cursor.getColumnIndex("number1"));
            String number2 = cursor.getString(cursor.getColumnIndex("number2"));
            String number3 = cursor.getString(cursor.getColumnIndex("number3"));
           // Log.e(TAG,"[qury][name=]"+name+"+[jpname=]"+jpname+"--[number1=]"+number1+"--[number2]"+number2+"--[number3]="+number3);

            PhoneContaceData phcontace = new PhoneContaceData();
            phcontace.setName(name);
            phcontace.setJpname(jpname);
            phcontace.setNumber1(number1);
            phcontace.setNumber2(number2);
            phcontace.setNumber3(number3);
            list.add(phcontace);
            flagIdx++;
            if (list.size() % 3 == 0 || flagIdx == (cursor.getCount() - 1)) {
                mhandler.sendEmptyMessage(REFERSH_ADAPTER);
            }
        }

        cursor.close();
        mhandler.sendEmptyMessage(REFERSH_ADAPTER);
        //对集合排序
        Collections.sort(list, new Comparator<PhoneContaceData>() {
            @Override
            public int compare(PhoneContaceData lhs, PhoneContaceData rhs) {
                //根据拼音进行排序
                return lhs.getPinyin().compareTo(rhs.getPinyin());
            }
        });
    }
}
