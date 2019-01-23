package telephony.internal.android.com.phoneui.Dialer.Blackdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yangbofeng on 2018/6/13.
 * 黑名单数据库
 */

public class BlackDatabaseHelper extends SQLiteOpenHelper {
    //创建 表格 名字data    表格里面 主键   uid  int   ,number
    private static final String CREATE_TABLE = "create table blackdata(_id INTEGER PRIMARY KEY autoincrement,number text);";
    private Context mContext;
    public BlackDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub创建数据库后，对数据库的操作
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub 更改数据库版本的操作
        db.execSQL("drop table if exists peopleinfo");
        onCreate(db);
    }

}
