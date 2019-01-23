package telephony.internal.android.com.phoneui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Random;

import telephony.internal.android.com.phoneui.Dialer.CallActivity;
import telephony.internal.android.com.phoneui.Dialer.PhoneService;
import telephony.internal.android.com.phoneui.Dialer.Receiver.PhoneLinstener;
import telephony.internal.android.com.phoneui.Voice.db.VoiceDBoperations;
import telephony.internal.android.com.phoneui.Voice.mode.VoiceBean;
import telephony.internal.android.com.phoneui.utiles.SettingsUtil;
import telephony.internal.android.com.phoneui.utiles.Utiles;
import telephony.internal.android.com.phoneui.view.MyToast;

public class MainActivity extends Activity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private Button bt_CallIn, bt_CallPepple, bt_phone, bt_voicemail, bt_voice_liushou;
    private static Context mContext;

    private Button bt_toast;
    private MyToast myToast;
    private boolean isliushou = true;
    private Handler mHandler;
    Intent intent;
    //注册来电状态


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        initButton();
        /**
         * 注册来电监听
         */
        intent = new Intent(this, PhoneService.class);
        startService(intent);
      //  setPhoneLinsenter(true);


    }


    /**
     * 注册来电监听
     */
    public void setPhoneLinsenter(boolean isregeist) {
        if (isregeist) {
            PhoneLinstener.getInstance().registerPhoneLintener(mContext);
        } else {
            PhoneLinstener.getInstance().unRisterPhoneLintener();
        }
    }

    private void initButton() {
        bt_CallIn = (Button) findViewById(R.id.bt_CallIn);
        bt_CallPepple = (Button) findViewById(R.id.bt_CallPepple);
        bt_phone = (Button) findViewById(R.id.bt_phone);
        bt_voicemail = (Button) findViewById(R.id.bt_voicemail);
        bt_voice_liushou = (Button) findViewById(R.id.bt_voice_liushou);
        bt_CallIn.setOnClickListener(this);
        bt_CallPepple.setOnClickListener(this);
        bt_phone.setOnClickListener(this);
        bt_voicemail.setOnClickListener(this);
        bt_voice_liushou.setOnClickListener(this);

        if (Utiles.getInstance().initLiushou(this)) {
            bt_voice_liushou.setBackground(getResources().getDrawable(R.drawable.bt_bg1));
        } else {
            bt_voice_liushou.setBackground(getResources().getDrawable(R.drawable.bt_bg));
        }

        //测试
        bt_toast = findViewById(R.id.bt_toast);
        bt_toast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"getRandom()"+getRandom());
            }
        });


       Button add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceDBoperations.getInstance().insert(mContext,"/storage/sdcard0/YBF_record/20180719175109357_17629193325.amr","18268734809","ybf","0");
                VoiceDBoperations.getInstance().insert(mContext,"/storage/sdcard0/YBF_record/20180719175245577_17629193325.amr","17629193325","ybf","0");
                VoiceDBoperations.getInstance().insert(mContext,"/storage/sdcard0/YBF_record/20180719175319267_17629193325.amr","17629193325","ybf","0");
                VoiceDBoperations.getInstance().insert(mContext,"/storage/sdcard0/YBF_record/20180719175355547_17629193325.amr","17629193325","ybf","0");
               // VoiceDBoperations.getInstance().insert(mContext,"/storage/sdcard0/YBF_record/20180719175655507","17629193325","ybf","0");
            }
        });
        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceDBoperations.getInstance().delete(mContext,"/storage/sdcard0/YBF_record/20180719175109357");
                VoiceDBoperations.getInstance().delete(mContext,"/storage/sdcard0/YBF_record/20180719175245577");
                VoiceDBoperations.getInstance().delete(mContext,"/storage/sdcard0/YBF_record/20180719175319267");
                VoiceDBoperations.getInstance().delete(mContext,"/storage/sdcard0/YBF_record/20180719175355547");
            }
        });
        Button up = findViewById(R.id.up);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            VoiceDBoperations.getInstance().updateTtp(mContext,"20180102121101","1");
            }
        });
        Button query = findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            ArrayList<VoiceBean> list = new ArrayList<VoiceBean>();
            @Override
            public void onClick(View v) {
            VoiceDBoperations.getInstance().qury(mContext,list);
            }
        });

    }
    public int getRandom(){
        Random r=new Random();
        int suiji=r.nextInt(900)+100;
        return suiji;
    }
    public static Context getContext() {
        return mContext;
    }

    /**
     * 打开通话界面
     *
     * @param phoneNumber
     */
    public void openCallActivity(String phoneNumber, String tag) {
        Intent intentOut = new Intent(mContext, CallActivity.class);
        intentOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentOut.putExtra("callOutNumber", phoneNumber);
        intentOut.putExtra("tag", tag);
        startActivity(intentOut);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_CallIn: //通话记录
                Intent intent = new Intent(mContext, CallInRecords.class);
                startActivity(intent);
                break;
            case R.id.bt_CallPepple: //联系人
                Intent intent2 = new Intent(mContext, PhoneBook.class);
                startActivity(intent2);
                break;
            case R.id.bt_phone: //打开电话界面
                Intent intent3 = new Intent(mContext, DialerActivity.class);
                intent3.putExtra("HandleUp", "HOME");
                startActivity(intent3);
                break;
            case R.id.bt_voicemail: //打开语音留言
                Intent intent4 = new Intent(mContext, VoicemailActivity.class);
                startActivity(intent4);
                break;
            case R.id.bt_voice_liushou: //点击留守
                Log.e(TAG, "voice");
                if (isliushou) {
                    SettingsUtil.getInstance().setdata(mContext, "isLiushou", "YES");
                    isliushou = false;
                } else {
                    SettingsUtil.getInstance().setdata(mContext, "isLiushou", "NO");
                    isliushou = true;
                }
                if (Utiles.getInstance().initLiushou(this)) {
                    bt_voice_liushou.setBackground(getResources().getDrawable(R.drawable.bt_bg1));
                } else {
                    bt_voice_liushou.setBackground(getResources().getDrawable(R.drawable.bt_bg));
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
      //  setPhoneLinsenter(false);
    }
}
