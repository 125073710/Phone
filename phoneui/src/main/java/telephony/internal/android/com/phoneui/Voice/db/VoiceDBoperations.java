package telephony.internal.android.com.phoneui.Voice.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.Voice.mode.VoiceBean;

/**
 * Created by yangbofeng on 2018/6/13.
 * 电话本数据查询工具类
 */

public class VoiceDBoperations {
    private String TAG = "VoiceDBoperations";

    private static VoiceDBoperations instance = new VoiceDBoperations();

    private VoiceDBoperations() {
    }

    public static VoiceDBoperations getInstance() {
        return instance;
    }
    //date number name typ

    /**
     * //插入数据到数据库
     *
     * @param mContext
     */
    public void insert(Context mContext, String path, String number, String name, String typ) {

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.tricheer.voicelist/voicedata");
        ContentValues values = new ContentValues();
        values.put("path", path);
        values.put("number", number);
        values.put("name", name);
        values.put("typ", typ);
        Uri uri = contentResolver.insert(insertUri, values);
    }


    public void delete(Context mContext, String path) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.voicelist/voicedata");
        Cursor cursor = contentResolver.query(uri, null, "path=?", new String[]{path}, null);
        if (cursor.moveToFirst()) {
            contentResolver.delete(uri, "path=?", new String[]{path});
        }
    }

    /**
     * 修改typ
     *
     * @param mContext
     */
    public void updateTtp(Context mContext, String path, String typ) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.voicelist/voicedata");
        ContentValues values = new ContentValues();
        values.put("typ", typ);
        int updateConut = contentResolver.update(uri, values, "path = ?", new String[]{path});
        Logger.e( "更新成功 typ");
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
     * /storage/sdcard0/YBF_record/2018 0718 1706 13 476_17629193325.amr
     */

    public void qury(Context mContext, ArrayList<VoiceBean> list) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.voicelist/voicedata");
        //android.provider.ContactsContract.Contacts._ID + " DESC"  按照将序查询（倒查）
        Cursor cursor = contentResolver.query(uri, null, null, null, android.provider.ContactsContract.Contacts._ID + " DESC");
        Logger.e("[qury]=" + cursor.getCount());
        list.clear();
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex("path"));
            String numb = cursor.getString(cursor.getColumnIndex("number"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String typ = cursor.getString(cursor.getColumnIndex("typ"));
            Logger.e("path--" + path + "--numb---" + numb + "---name--" + name + "--typ--" + typ);
            String[] info = path.split("/");
            String date = info[4].substring(0,12);
            Logger.e("date="+date);
            VoiceBean mVoiceBean = new VoiceBean();
            mVoiceBean.setPath(path);
            mVoiceBean.setNumber(numb);
            mVoiceBean.setName(name);
            mVoiceBean.setTag(typ);
            mVoiceBean.setDate(date);
            list.add(mVoiceBean);
        }
        cursor.close();
    }


    /**
     * 查询数据库未播放录音条数
     * 0 未播 ；已播放
     */
    public int queryNotPlay(Context mContext,ArrayList<String> list){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.voicelist/voicedata");
        Cursor cursor = contentResolver.query(uri, null, null, null, android.provider.ContactsContract.Contacts._ID + " DESC");
        list.clear();
        while (cursor.moveToNext()) {
            String typ = cursor.getString(cursor.getColumnIndex("typ"));
            if("0".equals(typ)){
                list.add(typ);
            }
        }
        return list.size();
    }



    /**
     * 查询数据库未播放录音条数
     * 0 未播 ；已播放
     */
    public int queryFile(Context mContext,String path){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.tricheer.voicelist/voicedata");
        Cursor cursor = contentResolver.query(uri, null, "path = ?", new String[]{path}, android.provider.ContactsContract.Contacts._ID + " DESC");
        return cursor.getCount();
    }
}
