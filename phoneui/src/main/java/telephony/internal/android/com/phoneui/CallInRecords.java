package telephony.internal.android.com.phoneui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.EditPhoneBookActivity;
import telephony.internal.android.com.phoneui.mode.PhoneNumberData;
import telephony.internal.android.com.phoneui.utiles.CallRecordsUtil;
import telephony.internal.android.com.phoneui.utiles.SettingsUtil;
import telephony.internal.android.com.phoneui.view.MyDialog;

/**
 * Created by yangbofeng on 2018/6/27.
 * 通话记录
 * 1.拨打电话逻辑未实现
 * 2.添加到电话本逻辑未实现
 */

public class CallInRecords extends Activity implements View.OnClickListener {
    private String TAG = "CallInRecords";
    private Button bt_down;
    private Button bt_up;
    private Button bt_callOut, bt_callMiss, bt_ok;
    private TextView tv_total_number, tv_times, tv_type, tv_name, tv_number, tv_state;
    private ArrayList<PhoneNumberData> ListIn = new ArrayList<>();
    private ArrayList<PhoneNumberData> ListOut = new ArrayList<>();
    private Context mContext;
    private int COMLPET_FIND_CALL_RECORDS_IN_AND_DOND_ANSWER = 0;//完成查来电和未接
    private boolean isComplete_find_Call_records = false; //是否正在查询数据库
    private int position = 0;
    private boolean isCallIn = true; //是否为去电记录

    //dialog
    private int index = 1;//弹出条目数
    private ImageView img_sanjiao_Redial;
    private ImageView img_sanjiao_Delete;
    private ImageView img_sanjiao_Delete_all;
    private ImageView img_sanjiao_add_contacts;

    private Button dialog_bt_up;
    private Button dialog_bt__down;
    private Button dialog_bt_ok;
    private MyDialog myUniversalDialog;
    //删除通话记录dialog
    private MyDialog mDialog_delete;
    //删除所有通话记录dialog
    private MyDialog mDialog_all_delete;
    private EditText ed_password;
    private Handler mThreadHandler;


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isComplete_find_Call_records = false;
                    Log.e(TAG, "查询完成，list  有数据");
                    if (isCallIn) { //来电
                        if (ListIn.size() > 0) {
                            setTextReceived(ListIn, position);
                        } else {
                            Log.e(TAG, "查询完成，list in  没有数据");
                        }
                    } else { //去电
                        if (ListOut.size() > 0) {
                            setTextDialed(ListOut, position);
                        } else {
                            Log.e(TAG, "查询完成，list out 没有数据");
                        }
                    }

                    break;
                case 1:  //通话总时间
                    Log.e(TAG, "通话总时间=" + SettingsUtil.getInstance().getdata(mContext,"times"));
                    break;
                case 2:
                    if (isCallIn) {
                        queryCallIn();
                    } else {
                        queryCallOut();
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_callin_record);
        registerThread();
        initView();
        queryCallIn();
        isComplete_find_Call_records = true;
        getTotalTime();
    }

    /**
     * 管理子线程销毁问题
     */

    public void registerThread() {
        HandlerThread mHandlerThread = new HandlerThread("CallRecordThread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
    }

    /*********************
     * start
     ***************************/
    //获取通话总时间
    Runnable getSpeakTotal = new Runnable() {
        @Override
        public void run() {
            CallRecordsUtil.getInstance().getTotalDuration(mContext, mhandler);
        }
    };

    /**
     * 获取通话总时间
     */

    public void getTotalTime() {
        mThreadHandler.post(getSpeakTotal);
    }

    /*********************
     * end
     ***************************/

    private void initView() {
        mContext = getApplicationContext();
        bt_down = findViewById(R.id.bt_down);
        bt_up = findViewById(R.id.bt_up);
        bt_callOut = findViewById(R.id.bt_callOut);
        bt_callMiss = findViewById(R.id.bt_callMiss);
        bt_ok = findViewById(R.id.bt__ok);
        bt_down.setOnClickListener(this);
        bt_up.setOnClickListener(this);
        bt_callOut.setOnClickListener(this);
        bt_callMiss.setOnClickListener(this);
        bt_ok.setOnClickListener(this);
        tv_total_number = findViewById(R.id.tv_total_number);
        tv_times = findViewById(R.id.tv_times);
        tv_type = findViewById(R.id.tv_type);
        tv_name = findViewById(R.id.tv_name);
        tv_number = findViewById(R.id.tv_number);
        tv_state = findViewById(R.id.tv_state);
    }

    @Override
    public void onClick(View view) {
        if (!isComplete_find_Call_records) {
            Log.e(TAG, "onClick--" + isComplete_find_Call_records);
            switch (view.getId()) {
                // 来电记录
                case R.id.bt_callMiss:
                    isCallIn = true;
                    position = 0;
                    queryCallIn();
                    isComplete_find_Call_records = true;
                    break;
                // 向下翻页
                case R.id.bt_down:
                    if (isCallIn) {//来电 和未接
                        if (position == ListIn.size() - 1) {
                            position = -1;
                        }
                        position += 1;
                        setTextReceived(ListIn, position);
                    } else { //去电
                        if (position == ListOut.size() - 1) {
                            position = -1;
                        }
                        position += 1;
                        setTextDialed(ListOut, position);
                    }

                    break;
                //向上翻页
                case R.id.bt_up:
                    if (isCallIn) {
                        if (position == 0) {
                            position = ListIn.size();
                        }
                        position -= 1;
                        setTextReceived(ListIn, position);
                    } else { //去电
                        if (position == 0) {
                            position = ListOut.size();
                        }
                        position -= 1;
                        setTextDialed(ListOut, position);
                    }
                    break;
                //去电记录
                case R.id.bt_callOut:
                    isCallIn = false;
                    position = 0;
                    queryCallOut();
                    isComplete_find_Call_records = true;
                    break;
                //功能键ok
                case R.id.bt__ok:
                    showDialogForCallRecords();
                    break;
            }
        } else {
            Log.e(TAG, "正在查询数据库");
        }

    }

    /**
     * 通话记录Dialog
     */
    public void showDialogForCallRecords() {
        Log.e(TAG, "[showDialogForCallRecords]>>>position=" + position);
        myUniversalDialog = new MyDialog(this);
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_call_records, null);
        myUniversalDialog.setLayoutView(dialog);
        Window window = myUniversalDialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 400;
        p.height = 300;
        window.setAttributes(p);
        myUniversalDialog.setCanceledOnTouchOutside(false);
        myUniversalDialog.show();

        img_sanjiao_Redial = dialog.findViewById(R.id.img_sanjiao_Redial);
        img_sanjiao_Delete = dialog.findViewById(R.id.img_sanjiao_Delete);
        img_sanjiao_Delete_all = dialog.findViewById(R.id.img_sanjiao_Delete_all);


        img_sanjiao_add_contacts = dialog.findViewById(R.id.img_sanjiao_add_contacts);

        dialog_bt_up = dialog.findViewById(R.id.dialog_bt_up);
        dialog_bt__down = dialog.findViewById(R.id.dialog_bt__down);
        dialog_bt_ok = dialog.findViewById(R.id.dialog_bt_OK);

        dialog_bt__down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index += 1;
                if (index > 4) {
                    index = 4;
                    return;
                }
                Log.e(TAG, "index_down=" + index);
                switch (index) {
                    case 1:
                        img_sanjiao_Redial.setVisibility(View.VISIBLE);
                        img_sanjiao_Delete.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete_all.setVisibility(View.INVISIBLE);
                        img_sanjiao_add_contacts.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        img_sanjiao_Redial.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete.setVisibility(View.VISIBLE);
                        img_sanjiao_Delete_all.setVisibility(View.INVISIBLE);
                        img_sanjiao_add_contacts.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        img_sanjiao_Redial.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete_all.setVisibility(View.VISIBLE);
                        img_sanjiao_add_contacts.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        img_sanjiao_Redial.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete_all.setVisibility(View.INVISIBLE);
                        img_sanjiao_add_contacts.setVisibility(View.VISIBLE);
                        break;
                }


            }
        });
        dialog_bt_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                index -= 1;
                if (index < 1) {
                    index = 1;
                    return;
                }
                Log.e(TAG, "index_up=" + index);
                switch (index) {
                    case 1:
                        img_sanjiao_Redial.setVisibility(View.VISIBLE);
                        img_sanjiao_Delete.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete_all.setVisibility(View.INVISIBLE);
                        img_sanjiao_add_contacts.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        img_sanjiao_Redial.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete.setVisibility(View.VISIBLE);
                        img_sanjiao_Delete_all.setVisibility(View.INVISIBLE);
                        img_sanjiao_add_contacts.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        img_sanjiao_Redial.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete_all.setVisibility(View.VISIBLE);
                        img_sanjiao_add_contacts.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        img_sanjiao_Redial.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete.setVisibility(View.INVISIBLE);
                        img_sanjiao_Delete_all.setVisibility(View.INVISIBLE);
                        img_sanjiao_add_contacts.setVisibility(View.VISIBLE);
                        break;
                }

            }
        });

        dialog_bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "ok_index=" + index);
                String number = "";
                switch (index) {
                    case 1:
                        if (isCallIn) { //来电
                            number = ListIn.get(position).getPhoneNumber();
                        } else {//去电
                            number = ListOut.get(position).getPhoneNumber();
                        }
                        myUniversalDialog.dismiss();
                        index = 1;
                        Log.e(TAG, "拨打电话" + number);
                        break;
                    case 2:
                        Log.e(TAG, "删除通话记录");
                        deleteCallRecord();
                        myUniversalDialog.dismiss();
                        index = 1;
                        break;
                    case 3:
                        deleteAllCallRecord();
                        myUniversalDialog.dismiss();
                        index = 1;
                        Log.e(TAG, "删除全部通话记录");
                        break;
                    case 4:
                        Log.e(TAG, "添加到电话本/打开电话本插入");
                        if (isCallIn) { //来电
                            number = ListIn.get(position).getPhoneNumber();
                        } else {//去电
                            number = ListOut.get(position).getPhoneNumber();
                        }
                        Intent intent = new Intent(mContext, EditPhoneBookActivity.class);
                        intent.putExtra("phoneNumber", number);
                        intent.putExtra("tag", "callRecord");
                        startActivityForResult(intent, 1);
                        myUniversalDialog.dismiss();
                        index = 1;
                        break;
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            String name = data.getExtras().getString("name");
            tv_name.setText(name);
        }
    }

    /**
     * 删除通话记录
     */
    public void deleteCallRecord() {
        mDialog_delete = new MyDialog(this);
        View dialog_delete = LayoutInflater.from(this).inflate(R.layout.dialog_call_records_delete, null);
        mDialog_delete.setLayoutView(dialog_delete);
        Window window = mDialog_delete.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 300;
        p.height = 150;
        window.setAttributes(p);
        mDialog_delete.setCanceledOnTouchOutside(false);
        mDialog_delete.show();

        TextView tv_YES = dialog_delete.findViewById(R.id.tv_YES);
        TextView tv_NO = dialog_delete.findViewById(R.id.tv_NO);

        tv_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "删除当前数据");
                if (isCallIn) { //来电
                    deleteCallLogIn();
                } else { //去电
                    deleteCallLogOut();
                }
                mDialog_delete.dismiss();
            }
        });
        tv_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog_delete.dismiss();
            }
        });

    }

    /*******************************
     * 删除来电记录
     ********************************************/
    Runnable deleteCallLogIn = new Runnable() {
        @Override
        public void run() {
            CallRecordsUtil.getInstance().deletLastCallLog(mContext, ListIn.get(position).getPhoneNumber());
            mhandler.sendEmptyMessage(2);
        }
    };

    /**
     * 删除来电记录
     */
    public void deleteCallLogIn() {
        mThreadHandler.post(deleteCallLogIn);
    }
    /*******************************end**********************************************/


    /*******************************
     * 删除去电记录
     ********************************************/
    Runnable deleteCallLogOut = new Runnable() {
        @Override
        public void run() {
            CallRecordsUtil.getInstance().deletLastCallLog(mContext, ListOut.get(position).getPhoneNumber());
            mhandler.sendEmptyMessage(2);
        }
    };

    /**
     * 删除去电记录
     */
    public void deleteCallLogOut() {
        mThreadHandler.post(deleteCallLogOut);
    }
    /*******************************end**********************************************/


    /**
     * 删除所有通话记录(All)
     */
    public void deleteAllCallRecord() {
        mDialog_all_delete = new MyDialog(this);
        View dialog_all_delete = LayoutInflater.from(this).inflate(R.layout.dialog_call_records_delete_all, null);
        mDialog_all_delete.setLayoutView(dialog_all_delete);
        Window window = mDialog_all_delete.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 300;
        p.height = 150;
        window.setAttributes(p);
        mDialog_all_delete.setCanceledOnTouchOutside(false);
        mDialog_all_delete.show();
        ed_password = dialog_all_delete.findViewById(R.id.ed_password);
        ed_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e(TAG, "charSequence=" + charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 填入来电和未接 数据信息
     *
     * @param position
     */
    public void setTextReceived(ArrayList<PhoneNumberData> list, int position) {
        Log.e(TAG, "[list.size]=" + list.size() + "[positio]=" + position);
        Log.e(TAG, "[name]=" + list.get(position).getName());
        tv_state.setText("Received");
        tv_total_number.setText(position + 1 + "/" + getCallIMissSize() + "");
        tv_times.setText(list.get(position).getDate());
        tv_type.setText(list.get(position).getStates());
        tv_name.setText(list.get(position).getName());
        tv_number.setText(list.get(position).getPhoneNumber());
    }

    /**
     * 填入去电数据信息，可能存在紧急号码，所以显示就得显示紧急号码
     *
     * @param position
     */
    public void setTextDialed(ArrayList<PhoneNumberData> list, int position) {
        tv_state.setText("Dialed");
        tv_total_number.setText(position + 1 + "/" + getCallInSize() + "");
        tv_times.setText(list.get(position).getDate());
        tv_type.setText(list.get(position).getStates());
        String phoneNumber = list.get(position).getPhoneNumber();
        Log.e(TAG, "是否为紧急号码" + (phoneNumber.equals("" +
                "18268734809")));
        if ("18268734809".equals(phoneNumber)) {
            tv_name.setText("EMERGENCY CALL");
        } else {
            tv_name.setText(list.get(position).getName());
        }
        tv_number.setText(phoneNumber);
    }

    /**
     * 获取来电和未接总个数
     */
    public int getCallIMissSize() {
        ContentResolver resolver = mContext.getContentResolver();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return 0;
        }
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE}, "type = 1 or type = 3", null, "date DESC limit 30");
        return cursor.getCount();
    }

    /**
     * 获取去电总数
     */
    public int getCallInSize() {
        ContentResolver resolver = mContext.getContentResolver();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE}, "type = 2", null, "date DESC limit 30");
        return cursor.getCount();
    }


    /*******************************查询来电和未接电话 ********************************************/

    Runnable queryCallIn = new Runnable() {
        @Override
        public void run() {
            CallRecordsUtil.getInstance().getDataListIn(ListIn, mContext, mhandler);
        }
    };

    /**
     * 开启线程查询数据库
     * 查询来电和未接电话
     */

    private void queryCallIn() {
        mThreadHandler.post(queryCallIn);
    }
    /*******************************end**********************************************/


    /*******************************
     * 查询去电
     ********************************************/
    Runnable queryCallOut = new Runnable() {
        @Override
        public void run() {
            CallRecordsUtil.getInstance().getDataListOut(ListOut, mContext, mhandler);
            mhandler.sendEmptyMessage(COMLPET_FIND_CALL_RECORDS_IN_AND_DOND_ANSWER);
        }
    };

    /**
     * 开启线程查询数据库
     * 查询去电
     */
    private void queryCallOut() {
        mThreadHandler.post(queryCallOut);
    }

    /*******************************
     * end
     **********************************************/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeThread();
    }

    /**
     * 清理开启的线程
     */
    public void removeThread() {
        mThreadHandler.removeCallbacks(getSpeakTotal);
        mThreadHandler.removeCallbacks(deleteCallLogIn);
        mThreadHandler.removeCallbacks(deleteCallLogOut);
        mThreadHandler.removeCallbacks(queryCallIn);
        mThreadHandler.removeCallbacks(queryCallOut);
    }
}
