package telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import telephony.internal.android.com.phoneui.mode.PhoneContaceData;
import telephony.internal.android.com.phoneui.utiles.Utiles;

/**
 * Created by yangbofeng on 2018/6/25.
 */

public class SimUtil {
    private String TAG ="SimUtil";
    private int REFRESH_SIM_DATA = 0;//更新data 数据
    private static SimUtil instance = new SimUtil();

    private SimUtil() {
    }

    public static SimUtil getInstance() {
        return instance;
    }

    /**
     * 查询sim卡联系人
     */
    public void queryAllContact(Context mContext, ArrayList<PhoneContaceData> list, Handler mhandler,String word) {
        String pinyin ="";
        list.clear();
        Uri uri = Uri.parse("content://icc/adn");
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        Log.d(TAG, "cursor count=" + cursor.getCount());
        list.clear();
        while (cursor.moveToNext()) {
            int flagIdx = 0;
            String name = cursor.getString(0);
            String number = cursor.getString(1);
            String emails = cursor.getString(2);
            int tag = cursor.getColumnIndex("anrs");
            String number3 = cursor.getString(tag);
            Log.e(TAG,"name="+name);
            PhoneContaceData phcontace = new PhoneContaceData();
            phcontace.setName(name);
            phcontace.setNumber1(number);
            phcontace.setNumber2(emails);
            phcontace.setNumber3(number3);

            boolean isEn = Utiles.getInstance().isLanugEn(mContext);
            if("".equals(name)||name==null){
                pinyin ="#";
            }else {
                pinyin = phcontace.getHeaderWord().toUpperCase();
            }

               /* if("A".equals(pinyin)||"B".equals(pinyin)||"C".equals(pinyin)||
                        "D".equals(pinyin)||"E".equals(pinyin)||"F".equals(pinyin)||
                        "G".equals(pinyin)||"H".equals(pinyin)||"I".equals(pinyin)||
                        "J".equals(pinyin)||"K".equals(pinyin)||"L".equals(pinyin)||
                        "M".equals(pinyin)||"N".equals(pinyin)||"O".equals(pinyin)||
                        "P".equals(pinyin)||"Q".equals(pinyin)||"R".equals(pinyin)||
                        "S".equals(pinyin)||"T".equals(pinyin)||"U".equals(pinyin)||
                        "V".equals(pinyin)||"W".equals(pinyin)||"X".equals(pinyin)||
                        "Y".equals(pinyin)||"Z".equals(pinyin)||
                        "a".equals(pinyin)|| "b".equals(pinyin)||"b".equals(pinyin)||
                        "d".equals(pinyin)||"e".equals(pinyin)||"f".equals(pinyin)||
                        "g".equals(pinyin)||"h".equals(pinyin)||"l".equals(pinyin)||
                        "j".equals(pinyin)||"k".equals(pinyin)||"l".equals(pinyin)||
                        "m".equals(pinyin)||"n".equals(pinyin)||"o".equals(pinyin)||
                        "p".equals(pinyin)||"q".equals(pinyin)||"r".equals(pinyin)||
                        "s".equals(pinyin)||"t".equals(pinyin)||"u".equals(pinyin)||
                        "v".equals(pinyin)||"w".equals(pinyin)||"x".equals(pinyin)||
                        "y".equals(pinyin)||"".equals(pinyin)){
                    pinyin = phcontace.getHeaderWord().toUpperCase();//拿到第一个字母转大写
                }else{
                    pinyin = phcontace.getHeaderWord();
                }*/
            if(isEn){ //系统语言为英语
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
            }else { //系统语言为 日语
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
            Log.d(TAG, "simcardinfo=" + name +"----"+ number + "---" + emails + "---" + number3);
            flagIdx++;
            if (list.size() % 3 == 0 || flagIdx == (cursor.getCount() - 1)) {
                mhandler.sendEmptyMessage(REFRESH_SIM_DATA);
            }
        }
        cursor.close();
        mhandler.sendEmptyMessage(REFRESH_SIM_DATA);
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
     * SIM卡插入名字
     *
     * @param name
     */
    public void insertName(String name,Context mContext) {
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
    public void SimUpName(Context mContext, String name , String number1, String number2, String number3,
                          String newname, TextView tv_number, TextView tv_number2, TextView tv_number3, Handler mhandler) {
        Uri uri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag",name );
        values.put("number",number1 );
        values.put("emails", number2);
        values.put("anrs",number3);
        values.put("newTag",newname );
        values.put("newNumber", tv_number.getText().toString().trim());
        values.put("newEmails", tv_number2.getText().toString().trim());
        values.put("newAnrs", tv_number3.getText().toString().trim());
        mContext.getContentResolver().update(uri, values, null, null);
        mhandler.sendEmptyMessage(3);


    }
    public void SimUpNumber1(Context mContext, String name , String number1, String number2, String number3,
                          TextView tv_Name, String newnumber, TextView tv_number2, TextView tv_number3, Handler mhandler) {
        Uri uri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag",name );
        values.put("number",number1 );
        values.put("emails", number2);
        values.put("anrs",number3);
        values.put("newTag",tv_Name.getText().toString().trim() );
        values.put("newNumber",newnumber );
        values.put("newEmails", tv_number2.getText().toString().trim());
        values.put("newAnrs", tv_number3.getText().toString().trim());
        mContext.getContentResolver().update(uri, values, null, null);
        mhandler.sendEmptyMessage(3);

    }
    public void SimUpNumber2(Context mContext, String name , String number1, String number2, String number3,
                          TextView tv_Name, TextView tv_number, String  newnumber2, TextView tv_number3, Handler mhandler) {
        Uri uri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag",name );
        values.put("number",number1 );
        values.put("emails", number2);
        values.put("anrs",number3);
        values.put("newTag",tv_Name.getText().toString().trim() );
        values.put("newNumber", tv_number.getText().toString().trim());
        values.put("newEmails",newnumber2 );
        values.put("newAnrs", tv_number3.getText().toString().trim());
        mContext.getContentResolver().update(uri, values, null, null);
        mhandler.sendEmptyMessage(3);


    }
    public void SimUpNumber3(Context mContext, String name , String number1, String number2, String number3,
                          TextView tv_Name, TextView tv_number, TextView tv_number2, String newnumber3, Handler mhandler) {
        Uri uri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag",name );
        values.put("number",number1 );
        values.put("emails", number2);
        values.put("anrs",number3);
        values.put("newTag",tv_Name.getText().toString().trim() );
        values.put("newNumber", tv_number.getText().toString().trim());
        values.put("newEmails", tv_number2.getText().toString().trim());
        values.put("newAnrs",newnumber3);
        mContext.getContentResolver().update(uri, values, null, null);
        mhandler.sendEmptyMessage(3);
    }

    /**
     * 获取sim卡联系人总数
     */

    public int getSIMsize(Context mContext){
        Uri uri = Uri.parse("content://icc/adn");
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        return cursor.getCount();
    }

    /**
     * 删除联系人
     * @param name
     * @param phone
     */
    public void deleteContact(Context mContext,String name, String phone) {
        // 这种方式删除数据时不行，查阅IccProvider源码发现，在provider中重写的delete方法并没有用到String[]
        // whereArgs这个参数
        // int delete = getContentResolver().delete(uri,
        // " tag = ? AND number = ? ",
        // new String[] { "jason", "1800121990" });
        Uri uri = Uri.parse("content://icc/adn");
        String where = "tag='" + name + "'";

        where += " AND number='" + phone + "'";
        int delete = mContext.getContentResolver().delete(uri, where, null);
        Log.d(TAG, "delete =" + delete);

    }

}
