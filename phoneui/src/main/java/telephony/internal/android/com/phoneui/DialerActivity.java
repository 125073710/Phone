package telephony.internal.android.com.phoneui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import telephony.internal.android.com.phoneui.Dialer.Receiver.PhoneLinstener;
import telephony.internal.android.com.phoneui.Dialer.Receiver.PhoneReceiver;
import telephony.internal.android.com.phoneui.Dialer.Utiles.CallUtiles;
import telephony.internal.android.com.phoneui.Dialer.Utiles.Utile;
import telephony.internal.android.com.phoneui.permission.Permissions;

/**
 * Created by yangbofeng on 2018/7/5.
 * 电话来电和去电界面
 */

public class DialerActivity extends Activity implements Utile.OpenActivity, PhoneReceiver.IPhoneReceiver {
    private String TAG = "DialerActivity";

    private TextView tv_title, tv_dialer_name, tv_dialer_number, tv_times;
    private ImageView img_title;

    private static final int PHONE_STATE_CHANGED = 102;
    //响铃
    private static final int PHONE_BELLING = 0;
    //挂断
    private int END_CALL_PHONE = 1;
    //接听
    private int ANSWER_THE_PHONE = 2;
    //拨打
    private static final int ON_CALLING = 3;
    private Context mContext;
    //自动呼叫
    private static final int PHONE_AUTO_CALL = 0;
    //主界面进来 10秒没有拨号，则退回到主界面
    private static final int PHONE_AUTO_FINISH = 1;
    //主界面输入号码，10秒无按键，则返回主页面
    private String isNeetcall = "";

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PHONE_AUTO_CALL:
                    //判断是否拨号，如果不是则拨打号码
                    if (Utile.getInstance().isCall(mContext)) {
                        String number = tv_dialer_number.getText().toString().trim();
                        if (number.length() > 0) {
                            CallUtiles.getInstance().call(mContext, number);
                            Log.e(TAG, "10s call");
                        }
                    }
                    break;
                case PHONE_AUTO_FINISH:
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        mContext = getApplicationContext();
        initView();
        //初始化权限
        Permissions.requestPermissionAll(this);
        //注册暗码打开测试项监听
        Utile.getInstance().setOpenActivityListener(this);
        PhoneLinstener.getInstance().registerPhoneLintener(mContext);
        PhoneReceiver.registerNotify("DialerActivity", this);
        initHandleUP();
    }

    /**
     * 判断是从主界面进入，还是提起话筒进入拨号界面
     */
    private void initHandleUP() {
        Intent intent = getIntent();
        isNeetcall = intent.getStringExtra("HandleUp");
        Log.e(TAG, "isNeetcall=" + isNeetcall);
    }

    /**
     * 初始化动态权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Permissions.changePermissionState(this, permissions[0], true);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 初始化界面信息
     */
    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        img_title = findViewById(R.id.img_title);
        tv_times = findViewById(R.id.tv_times);
        tv_dialer_name = findViewById(R.id.tv_dialer_name);
        tv_dialer_number = findViewById(R.id.tv_dialer_number);
        tv_times.setVisibility(View.INVISIBLE);

    }

    /*adb shell input keyevent 8*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "[onkeyDown]=" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                Utile.getInstance().change(tv_dialer_number, "1");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_2:
                Utile.getInstance().change(tv_dialer_number, "2");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_3:
                Utile.getInstance().change(tv_dialer_number, "3");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_4:
                Utile.getInstance().change(tv_dialer_number, "4");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_5:
                Utile.getInstance().change(tv_dialer_number, "5");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_6:
                Utile.getInstance().change(tv_dialer_number, "6");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_7:
                Utile.getInstance().change(tv_dialer_number, "7");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_8:
                Utile.getInstance().change(tv_dialer_number, "8");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_9:
                Utile.getInstance().change(tv_dialer_number, "9");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_0:
                Utile.getInstance().change(tv_dialer_number, "0");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_STAR:
                Utile.getInstance().change(tv_dialer_number, "*");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_POUND:
                Utile.getInstance().change(tv_dialer_number, "#");
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_DEL: //消去
                Utile.getInstance().delete(tv_dialer_number);
                mhandler.removeMessages(PHONE_AUTO_CALL);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER: //去电
                CallUtiles.getInstance().call(mContext, "17629193325");
                break;
        }
        if ("HANDLE_UP".equals(isNeetcall)) {
            mhandler.sendEmptyMessageDelayed(PHONE_AUTO_CALL, 8000); //自动拨号
        } else if ("HOME".equals(isNeetcall)) {
            mhandler.sendEmptyMessageDelayed(PHONE_AUTO_FINISH, 8000); //自动拨号
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * 暗码打开A
     */
    @Override
    public void OpenA() {
        Log.e(TAG, "[open test A]");
    }

    /**
     * 暗码打开B
     */
    @Override
    public void OpenB() {
        Log.e(TAG, "[open test B]");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestory");
        PhoneLinstener.getInstance().unRisterPhoneLintener();
        PhoneReceiver.removeNotify("DialerActivity");
    }

    //拿起电话
    @Override
    public void onhandleUp() {
        Log.e(TAG, "onhandleUp");
        if (tv_dialer_number.getText().length() > 0) {  //如果不为空，拿起话筒直接拨号
            CallUtiles.getInstance().call(mContext, tv_dialer_number.getText().toString());
        }
    }

    //放下电话
    @Override
    public void onhandleDown() {
        Log.e(TAG, "onhandleDown");
        finish();
    }

    /**
     * 电话状态改变
     */
    @Override
    public void onPhoneStateChange() {
        if (Utile.getInstance().isCall(mContext)) {
            Log.e(TAG, "onPhoneStateChange1");
            finish();
        }
    }

}
