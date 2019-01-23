package telephony.internal.android.com.speeddiar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.internal.telephony.ITelephony;

import telephony.internal.android.com.speeddiar.permission.Permissions;


public class MainActivity extends AppCompatActivity implements PhoneReceiverStates.IPhoneReceiverStates {
    private String TAG = "Testybf";
    private static final int PHONE_STATE_CHANGED = 102;
    private Button btton_answer;
    private Button bt_start;
    private Button bt_stop;
    AudioRecoderUtils au;
    Context mContext;
    Intent intent;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    intent = new Intent();
                    intent.setClass(mContext, RecoderService.class);
                    startService(intent);
                    break;
                case 2:
                    stopService(intent);
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "oncreat");
        btton_answer = (Button) findViewById(R.id.btton_answer);
        bt_start = (Button) findViewById(R.id.bt_start);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        mContext = getApplicationContext();
        init();
        initClick();
        //注册电话监听接口
        PhoneReceiverStates.registerNotify("MainActivity", this);

        au = new AudioRecoderUtils();

    }

    private void init() {
        //初始化权限
        Permissions.requestPermissionAll(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Permissions.changePermissionState(this, permissions[0], true);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initClick() {

        btton_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        answerCall();
                    }
                }).start();

            }
        });
        //开始录音
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        au.startRecord();
                    }
                }).start();


            }
        });
        //停止录音
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        au.stopRecord();
                    }
                }).start();

            }
        });

    }

    /**
     * 接电话
     */
    public void answerCall() {
        try {
            ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
            iTel.answerRingingCall();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PhoneReceiverStates.removeNotify("MainActivity_ybf");
    }

    @Override
    public void onCalling() {

    }

    @Override
    public void onEndCallPhone() {
        Log.e(TAG, "挂断");
        mHandler.sendEmptyMessage(2);
    }

    @Override
    public void onAnswerThePhone() {
        Log.e(TAG, "接听");
        mHandler.sendEmptyMessage(1);

    }

    @Override
    public void onPhoneBellring() {
        Log.e(TAG, "响铃");
    }
}
