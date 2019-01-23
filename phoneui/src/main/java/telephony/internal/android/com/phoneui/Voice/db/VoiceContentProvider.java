package telephony.internal.android.com.phoneui.Voice.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb.DatabaseHelper;

/**
 * Created by yangbofeng on 2018/6/13.
 */

public class VoiceContentProvider extends ContentProvider {
    private DatabaseHelper dbHelper;
    // 若不匹配采用UriMatcher.NO_MATCH(-1)返回
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    // 匹配码
    private static final int CODE_NOPARAM = 1;

    static {
        // 对等待匹配的URI进行匹配操作，必须符合cn.xyCompany.providers.personProvider/person格式
        // 匹配返回CODE_NOPARAM，不匹配返回-1
        MATCHER.addURI("com.tricheer.voicelist", "voicedata", CODE_NOPARAM);

    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(this.getContext(), "voicelist.db", null, 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//android.provider.ContactsContract.Contacts._ID + " DESC"  按照将序查询（倒查）
        if (db.isOpen()) {
            return db.query("voicedata", projection, selection, selectionArgs, null, null, android.provider.ContactsContract.Contacts._ID + " DESC");
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 特别说一下第二个参数是当name字段为空时，将自动插入一个NULL。
        if (db.isOpen()) {
            // 特别说一下第二个参数是当name字段为空时，将自动插入一个NULL。
            long rowid = db.insert("voicedata", "null", contentValues);
            Uri insertUri = ContentUris.withAppendedId(uri, rowid);// 得到代表新增记录的Uri
            this.getContext().getContentResolver().notifyChange(uri, null);
            db.close();
            return insertUri;
        }
        return null;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            int count = db.delete("voicedata", selection, selectionArgs);
            this.getContext().getContentResolver().notifyChange(uri, null);
            db.close();
            return count;
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            int count = db.update("voicedata", contentValues, selection, selectionArgs);
            this.getContext().getContentResolver().notifyChange(uri, null);
            db.close();
            return count;
        }
        return 0;
    }
}
