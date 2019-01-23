package telephony.internal.android.com.phoneui.utiles;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by yangbofeng on 2018/6/29.
 */

public class SettingsUtil {


    private static SettingsUtil instance = new SettingsUtil();

    private SettingsUtil() {
    }

    public static SettingsUtil getInstance() {
        return instance;
    }

    //Setting 数据库中写值 String
    public  void setdata(Context cxt, String  key,String value) {
        try {
            Settings.Global.putString(cxt.getContentResolver(), key, value);
        } catch (Exception e) {
            if (e != null) {
                Log.i("TAG", "setRecorderRear(Context," + value + ")> " + e.getMessage());
            }
        }
    }

    //Setting 数据库中读取值 String
    public  String getdata(Context cxt,String key) {
        try {
            return Settings.Global.getString(cxt.getContentResolver(), key);
        } catch (Exception e) {
            if (e != null) {
                Log.i("TAG", "getRecorderRear(Context)> " + e.getMessage());
            }
        }
        return "";
    }


    //Setting 数据库中写值 int
    public  void setdataInt(Context cxt, String  key,int value) {
        try {
            Settings.Global.putInt(cxt.getContentResolver(), key, value);
        } catch (Exception e) {
            if (e != null) {
                Log.i("TAG", "setRecorderRear(Context," + value + ")> " + e.getMessage());
            }
        }
    }

    //Setting 数据库中读取值 Int
    public int getdataInt(Context cxt, String key) {
        try {
            return Settings.Global.getInt(cxt.getContentResolver(), key);
        } catch (Exception e) {
            if (e != null) {
                Log.i("TAG", "getRecorderRear(Context)> " + e.getMessage());
            }
        }
        return -1;
    }
}
