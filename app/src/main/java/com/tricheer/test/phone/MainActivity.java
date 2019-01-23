package com.tricheer.test.phone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallManager;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.tricheer.test.phone.ReceiverPhoneStates.PhoneReceiverStates;
import com.tricheer.test.phone.View.CallingActivity;
import com.tricheer.test.phone.View.ContactInserPage;
import com.tricheer.test.phone.View.MyDialog;
import com.tricheer.test.phone.adapter.CarllRecoderAdapter;
import com.tricheer.test.phone.model.PhoneNumberData;
import com.tricheer.test.phone.permission.Permissions;
import com.tricheer.test.phone.presenter.ButtonPresenter;
import com.tricheer.test.phone.presenter.DialogPresenter;
import com.tricheer.test.phone.presenter.IRefresh;
import com.tricheer.test.phone.utiles.AudioRecoderUtils;
import com.tricheer.test.phone.utiles.CallUtiles;
import com.tricheer.test.phone.utiles.MyTimerTask;
import com.tricheer.test.phone.utiles.Utiles;

import java.util.ArrayList;

import static com.tricheer.test.phone.R.id.bt_AddContacts;
import static com.tricheer.test.phone.R.id.bt_deleteAll;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IRefresh, PhoneReceiverStates.IPhoneReceiverStates {
    private static final String TAG = "MainActivity_ybf";
    private Button bt_0, bt_1, bt_2, bt_3, bt_4, bt_5, bt_6, bt_7, bt_8, bt_9,bt_up,bt_down,bt_enter,
            bt_xinhao, bt_jinhao, bt_call, bt_delete, bt_phoneTxt, bt_endcall, bt_OutCall, bt_InCall,bt_uncall,bt_fastcall;
    private Button Dialog_bt_dial, Dialog_bt_delete, Dialog_bt_deleteAll, Dialog_bt_AddContacts;
    private TextView tv_phoneNumber, tv_states, tv_sizeOut, tv_sizeIn,tv_uncall;
    private LinearLayout mly_jianpan;
    private ButtonPresenter bt;
    private DialogPresenter DiaPresenter;
    private ListView lv_call_record;
    private Context mContext;
    private CarllRecoderAdapter adapter;
    private MyDialog myUniversalDialog;
   private  ArrayList<PhoneNumberData> lists = new ArrayList<>();
   private  ArrayList<PhoneNumberData> callout = new ArrayList<>();
   private  ArrayList<PhoneNumberData> callIn = new ArrayList<>();
    private MyTimerTask myTimerTask;
    private CallManager mCM;
    private static final int PHONE_STATE_CHANGED = 102;
    int position = 0;
    boolean iscalling =false;//通话中
    boolean iscallIn =false; //打进来中
    private MyDialog dialog;
    AudioRecoderUtils audioRecoderUtils;
    private boolean isquerySqliet = false;


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 0:
                case 1:
                    if(Utiles.getInstance().isSimUsed(mContext)){
                    Intent intent = new Intent(mContext, CallingActivity.class);
                    startActivity(intent);
                        Log.e(TAG,"open Call UI");
                }

                    break;
                case 2:
                    Log.e(TAG,"close activity");
                    Intent intent3 = new Intent();
                    intent3.setAction("com.tricheer.finish.activity");
                    sendBroadcast(intent3);
                    break;
                case 3:
                    //判断是否拨号，如果不是则拨打号码
                    if(Utiles.isCall(mContext)){
                        bt.call(tv_phoneNumber, getApplicationContext());
                        bt.cleanNumber(tv_phoneNumber);
                        Log.e(TAG,"10s call");
                    }
                    break;
                case  4:
                    adapter.notifyDataSetChanged();
                    tv_sizeOut.setText(lists.size() + "");
                    break;
                case 5:
                    adapter.notifyDataSetChanged();
                    tv_sizeIn.setText(lists.size() + "");
                    break;
                case 6:
                    adapter.notifyDataSetChanged();
                    tv_uncall.setText(lists.size() + "");
                    break;

            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏actionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mContext = getApplicationContext();
        initView();
        //注册电话监听接口
        PhoneReceiverStates.registerNotify("MainActivity", this);
        //注册键盘逻辑处理
        bt = new ButtonPresenter(this, mContext);
        //注册dialog逻辑处理
        DiaPresenter = new DialogPresenter(this, mContext);
        //init ListView
        adapter = new CarllRecoderAdapter(lists, mContext);
        lv_call_record.setAdapter(adapter);
        //初始化权限
        Permissions.requestPermissionAll(this);
         audioRecoderUtils = new AudioRecoderUtils();
       // initCallOut();
    }
    //初始化监听去电状态
    private void initCallOut() {
        mCM = CallManager.getInstance();
        Phone phone = PhoneFactory.getDefaultPhone();
        mCM.registerPhone(phone);
        mCM.registerForPreciseCallStateChanged(mhandler, PHONE_STATE_CHANGED, null);
    }
    //监听去电接通
    private void updatePhoneSateChange() {
        Call fgCall = mCM.getActiveFgCall();
        if (mCM.hasActiveRingingCall()) {
            fgCall = mCM.getFirstActiveRingingCall();
        }
        final com.android.internal.telephony.Call.State state = fgCall.getState();
        switch (state) {
            case IDLE:
                Log.e(TAG, "ISLE");
                break;
            case ACTIVE:
                Log.e(TAG, "ACTIVE");
                myTimerTask = new MyTimerTask(tv_states);
                myTimerTask.startTimer();
                break;
            default:
                break;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Permissions.changePermissionState(this, permissions[0], true);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        tv_phoneNumber = (TextView) findViewById(R.id.tv_phontNumber);
        tv_states = (TextView) findViewById(R.id.tv_states);
        tv_sizeOut = (TextView) findViewById(R.id.tv_sizeOut);
        tv_sizeIn = (TextView) findViewById(R.id.tv_sizeIn);
        tv_uncall = (TextView) findViewById(R.id.tv_uncall);
        bt_0 = (Button) findViewById(R.id.bt_0);
        bt_1 = (Button) findViewById(R.id.bt_1);
        bt_2 = (Button) findViewById(R.id.bt_2);
        bt_3 = (Button) findViewById(R.id.bt_3);
        bt_4 = (Button) findViewById(R.id.bt_4);
        bt_5 = (Button) findViewById(R.id.bt_5);
        bt_6 = (Button) findViewById(R.id.bt_6);
        bt_7 = (Button) findViewById(R.id.bt_7);
        bt_8 = (Button) findViewById(R.id.bt_8);
        bt_9 = (Button) findViewById(R.id.bt_9);
        bt_up = (Button) findViewById(R.id.bt_up);
        bt_down = (Button) findViewById(R.id.bt_down);
        bt_enter = (Button) findViewById(R.id.bt_enter);

        bt_jinhao = (Button) findViewById(R.id.bt_jinhao);
        bt_xinhao = (Button) findViewById(R.id.bt_xinhao);
        bt_call = (Button) findViewById(R.id.bt_call);
        bt_endcall = (Button) findViewById(R.id.bt_endcall);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        bt_phoneTxt = (Button) findViewById(R.id.bt_phoneTxt);
        bt_OutCall = (Button) findViewById(R.id.bt_OutCall);
        bt_InCall = (Button) findViewById(R.id.bt_InCall);
        bt_uncall = (Button) findViewById(R.id.bt_uncall);
        bt_fastcall = (Button) findViewById(R.id.bt_fastcall);

        mly_jianpan = (LinearLayout) findViewById(R.id.ly_jianpan);
        lv_call_record = (ListView) findViewById(R.id.lv_call_record);

        bt_0.setOnClickListener(this);
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
        bt_3.setOnClickListener(this);
        bt_4.setOnClickListener(this);
        bt_5.setOnClickListener(this);
        bt_6.setOnClickListener(this);
        bt_7.setOnClickListener(this);
        bt_8.setOnClickListener(this);
        bt_9.setOnClickListener(this);
        bt_up.setOnClickListener(this);
        bt_down.setOnClickListener(this);
        bt_enter.setOnClickListener(this);
        bt_jinhao.setOnClickListener(this);
        bt_xinhao.setOnClickListener(this);
        bt_call.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        bt_phoneTxt.setOnClickListener(this);
        bt_endcall.setOnClickListener(this);
        bt_OutCall.setOnClickListener(this);
        bt_InCall.setOnClickListener(this);
        bt_uncall.setOnClickListener(this);
        bt_fastcall.setOnClickListener(this);

        //长按删除
        bt_delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tv_phoneNumber.setText("");
                return false;
            }
        });
        lv_call_record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "position=" + i);
                adapter.setSelectPosition(i);
                position = i;
                myUniversalDialog = new MyDialog(MainActivity.this);
                View dialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
                myUniversalDialog.setLayoutView(dialog);
                Window window = myUniversalDialog.getWindow();
                final WindowManager.LayoutParams p = window.getAttributes();
                p.gravity = Gravity.CENTER;
                p.width = 300;
                window.setAttributes(p);
                myUniversalDialog.setCanceledOnTouchOutside(true);
                myUniversalDialog.show();
                Dialog_bt_dial = dialog.findViewById(R.id.bt_dial);
                Dialog_bt_delete = dialog.findViewById(R.id.bt_delete);
                Dialog_bt_deleteAll = dialog.findViewById(bt_deleteAll);
                Dialog_bt_AddContacts = dialog.findViewById(bt_AddContacts);
                Dialog_bt_dial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "dial--" + lists.get(position).getPhoneNumber());
                        // Log.e(TAG,)
                        DiaPresenter.call(lists.get(position).getPhoneNumber());
                        myUniversalDialog.dismiss();
                        mhandler.sendEmptyMessage(0);
                    }
                });
                Dialog_bt_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "delete");
                        DiaPresenter.deletLastCallLog(lists.get(position).getPhoneNumber());
                        myUniversalDialog.dismiss();
                    }
                });
                Dialog_bt_deleteAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "delete_all");
                        DiaPresenter.DeleteCallAll();
                        myUniversalDialog.dismiss();
                    }
                });
                Dialog_bt_AddContacts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "add");
                        Intent intent = new Intent(MainActivity.this,ContactInserPage.class);
                        intent.putExtra("phonenumber",lists.get(position).getPhoneNumber());
                        startActivity(intent);
                        myUniversalDialog.dismiss();
                    }
                });

            }
        });

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG,"keyCode = "+keyCode);
        switch (keyCode){
            case  KeyEvent.KEYCODE_0:
                bt.change(tv_phoneNumber, "0");
                break;
            case  KeyEvent.KEYCODE_1:
                bt.change(tv_phoneNumber, "1");
                break;
            case  KeyEvent.KEYCODE_2:
                bt.change(tv_phoneNumber, "2");
                break;
            case  KeyEvent.KEYCODE_3:
                bt.change(tv_phoneNumber, "3");
                break;
            case  KeyEvent.KEYCODE_4:
                bt.change(tv_phoneNumber, "4");
                break;
            case  KeyEvent.KEYCODE_5:
                bt.change(tv_phoneNumber, "5");
                break;
            case  KeyEvent.KEYCODE_6:
                bt.change(tv_phoneNumber, "6");
                break;
            case  KeyEvent.KEYCODE_7:
                bt.change(tv_phoneNumber, "7");
                break;
            case  KeyEvent.KEYCODE_8:
                bt.change(tv_phoneNumber, "8");
                break;
            case  KeyEvent.KEYCODE_9:
                bt.change(tv_phoneNumber, "9");
                break;
            case  KeyEvent.KEYCODE_STAR:
                bt.change(tv_phoneNumber, "*");
                break;
            case  KeyEvent.KEYCODE_POUND :
                bt.change(tv_phoneNumber, "#");
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                Log.e(TAG,"up");
                adapter.setSelectPosition(position+1);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Log.e(TAG,"down");
                if(lists.size()>0){

                    adapter.setSelectPosition(position-1);
                }

                break;

        }

        mhandler.sendEmptyMessageDelayed(3,10000);
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_0:
                bt.change(tv_phoneNumber, "0");
                break;
            case R.id.bt_1:
                bt.change(tv_phoneNumber, "1");
                break;
            case R.id.bt_2:
                bt.change(tv_phoneNumber, "2");
                break;
            case R.id.bt_3:
                bt.change(tv_phoneNumber, "3");
                break;
            case R.id.bt_4:
                bt.change(tv_phoneNumber, "4");
                break;
            case R.id.bt_5:
                bt.change(tv_phoneNumber, "5");
                break;
            case R.id.bt_6:
                bt.change(tv_phoneNumber, "6");
                break;
            case R.id.bt_7:
                bt.change(tv_phoneNumber, "7");
                break;
            case R.id.bt_8:
                bt.change(tv_phoneNumber, "8");
                break;
            case R.id.bt_9:
                bt.change(tv_phoneNumber, "9");
                break;
            case R.id.bt_xinhao:
                bt.change(tv_phoneNumber, "*");
                break;
            case R.id.bt_jinhao:
                bt.change(tv_phoneNumber, "#");
                break;
            case R.id.bt_delete:
                bt.delete(tv_phoneNumber);
                break;
            case R.id.bt_call:
                bt.call(tv_phoneNumber, getApplicationContext());
                break;
            case R.id.bt_endcall:
                if(iscalling || iscallIn){
                    Log.e(TAG,"endcall");
                    CallUtiles.end(mContext);

                }
                iscalling =false;
                iscallIn = false;
                break;
            case R.id.bt_phoneTxt:
                Log.e("TAG", "contact");
                openTeleContact();
                break;
            case R.id.bt_OutCall:
                if(!isquerySqliet){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CallOut();
                        }
                    }).start();
                }else{
                    Log.e(TAG, "正在查询数据库");
                }



                break;
            case R.id.bt_InCall: //来电记录
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CallIn();
                    }
                }).start();
                break;
            case R.id.bt_up:
                Log.e(TAG,"key up");
                if(position == 0){
                    position = lists.size();
                }
                position -=1;
                if(lists.size() >=1 && position<= lists.size()){
                    lv_call_record.setSelection(position);
                    adapter.setSelectPosition(position);
                }
                lv_call_record.smoothScrollToPositionFromTop(position,0);
                Log.e(TAG,"key position="+position);
                tv_sizeOut.setText(position+1+"/"+lists.size());
                break;
            case R.id.bt_down:
                Log.e(TAG,"key down");
                Log.e(TAG," position="+position);
                if(position == lists.size()){
                    position = 0;
                }
                if(lists.size()>=0  && position <=lists.size()){
                    lv_call_record.setSelection(position);
                    adapter.setSelectPosition(position);
                }
                lv_call_record.smoothScrollToPositionFromTop(position,0);
                tv_sizeOut.setText(position+1+"/"+lists.size());
                Log.e(TAG,"key position="+position);
                position +=1;
                break;
            case R.id.bt_uncall:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UnCall();
                       // mhandler.sendEmptyMessage(6);
                    }
                }).start();
                break;
            case R.id.bt_fastcall:
                String number = getFastNumber(mContext,"number1");
                if("".equals(number)){
                    Toast.makeText(mContext,"请设置号码",Toast.LENGTH_SHORT).show();
                    ShowSaveFastNumberDialog();
                }else{
                    tv_phoneNumber.setText(number);
                   bt.call(tv_phoneNumber,mContext);
                }
                break;

            case R.id.bt_enter:

                break;
        }
      // 自动呼叫  mhandler.sendEmptyMessageDelayed(3,10000);
    }
    //打开电话本
    private void openTeleContact() {
        Intent intent = new Intent(this, PhoneContact.class);
        startActivity(intent);
    }

    //保存快速拨号
    public  void saveFastCallNumber(Context context,String Number, String key) {
        SharedPreferences spf = context. getSharedPreferences("number", MODE_PRIVATE);
        spf.edit().putString(Number, key).commit();
    }

    //获取快速拨号值
    public  String getFastNumber(Context context,String Number) {
        SharedPreferences spf =context. getSharedPreferences("number", MODE_PRIVATE);
        return spf.getString(Number, "");
    }

    /**
     * 刷新拨打出去的通话记录
     */
    public void CallOut() {
         bt.getDataListOut(lists,mhandler);
    }

    /**
     * 刷新来电时的通话记录
     */
    public void   CallIn() {
        bt.getDataListIn(lists,mhandler);


    }


    /**
     * 刷新未接
     */
    public  void UnCall(){
        bt.getDataListUncall(lists,mhandler);

    }

    @Override
    public void Deleted_Refresh() {
        Log.e(TAG, "deleted refresh:");
        if ("打出".equals(lists.get(position).getStates())) {
          //  CallOut();
        } else {
          //  CallIn();
        }

        adapter.notifyDataSetChanged();

    }

    @Override
    public void Delete_Refresh_All() {
        CallOut();
        CallIn();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void NO_SIM() {
        Toast.makeText(mContext,"无SIM可用",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalling(String number) {
        Log.e(TAG, "去电中");
        tv_states.setText("去电中--" + number);
        mhandler.sendEmptyMessage(1);
        iscalling = true;
    }

    @Override
    public void onEndCallPhone() {
        Log.e(TAG, "endcall");
        tv_states.setText("挂断");
        mhandler.sendEmptyMessage(2);
        audioRecoderUtils.stopRecord();

    }

    @Override
    public void onAnswerThePhone() {
        Log.e(TAG, "answer");
        tv_states.setText("通话中。。。");
        iscalling =true;
        iscallIn = true;
        audioRecoderUtils.startRecord();
    }

    @Override
    public void onPhoneBellring(String number) {
        Log.e(TAG, "来电,响铃"+number);
       if(number.equals("18268734809")){ //黑名单
            CallUtiles.end(mContext);
            Log.e(TAG,"end");
        }else{

           tv_states.setText("来电--" + number);
           mhandler.sendEmptyMessage(1);
        }
        iscallIn = true;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhoneReceiverStates.removeNotify("MainActivity_ybf");
    }

    public void ShowSaveFastNumberDialog() {
        dialog = new MyDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_fast_save_number, null);
        dialog.setLayoutView(view);
        Window window = dialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 300;
        window.setAttributes(p);
        dialog.setCanceledOnTouchOutside(false);//触摸边缘不消失
        dialog.show();
      final EditText ed_fastNumber = view.findViewById(R.id.ed_fastNumber);
      Button bt_saveFastNumber = view.findViewById(R.id.bt_saveFastNumber);
        bt_saveFastNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             String number =   ed_fastNumber.getText().toString().trim();
                saveFastCallNumber(mContext,"number1",number);
                dialog.dismiss();
            }
        });

    }
}
