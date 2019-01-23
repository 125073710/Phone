package com.tricheer.test.phone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
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

import com.tricheer.test.phone.View.CallingActivity;
import com.tricheer.test.phone.View.MyDialog;
import com.tricheer.test.phone.phonebook.CustomView.SpellsSortView;
import com.tricheer.test.phone.phonebook.PhoneContaceData;
import com.tricheer.test.phone.utiles.SimUtil;
import com.tricheer.test.phone.phonebook.adapter.ContactAdapter;
import com.tricheer.test.phone.phonebook.db.ContactObserver;
import com.tricheer.test.phone.phonebook.db.DBoperations;
import com.tricheer.test.phone.phonebook.db.DatabaseHelper;
import com.tricheer.test.phone.utiles.CallUtiles;
import com.tricheer.test.phone.utiles.Utiles;

import java.util.ArrayList;


/**
 * Created by yangbofeng on 2018/6/12.
 */

public class PhoneContact extends Activity implements View.OnClickListener {

    private String TAG = "PhoneContact";
    private Context mContext;
    private Button bt_phone, bt_sim, bt_insert,bt_insertSIM;
    private ListView lv_contact;
    private Button bt_OK;
    private ContactAdapter adapter;
    ArrayList<PhoneContaceData> phonelist = new ArrayList<>();
    private ContactObserver mContactObserver;
    DatabaseHelper helper;
    SQLiteDatabase db;
    DBoperations mDBoperations;
    //dialog
    MyDialog dialog;
    String name,jpname;
    TextView tv_insertName, tv_number1;
    EditText et_name, ed_number1;
    LinearLayout ly_isjapan;
    LinearLayout ly_isjapanName ;
    private EditText ed_dia_name;
    private EditText ed_jpname;
    private EditText ed_modify_number1;
    private EditText ed_modify_number2;
    private EditText ed_modify_number3;

    private Button bt_modify_name;
    private Button bt_modify_jpname;
    private Button bt_modify_number;
    private Button bt_modify_number2;
    private Button bt_modify_number3;
    private Button bt_deleteItem;
    int TitleItem = 0; //0 本机 ；1 sim卡



    //快速查询
    private SpellsSortView ss_sort;
    private TextView tv_centerspell;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    Log.e(TAG, "mhadler notify");
                    break;
                case 1:
                    Log.e(TAG, "mhadler notify = 1");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mDBoperations.querydata(db, phonelist,mHandler);
                        }
                    }).start();
                    break;
                case  2:
                    if(Utiles.getInstance().isSimUsed(mContext)){
                        Intent intent = new Intent(mContext, CallingActivity.class);
                        startActivity(intent);
                    }

                    break;
                case 3:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                           SimUtil.getInstance().queryAllContact(mContext,phonelist,mHandler);
                        }
                    }).start();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact);
        mContext = getApplicationContext();
        initDB();
        initView();
    }

    private void initDB() {
        helper = new DatabaseHelper(PhoneContact.this, "contact.db", null, 1);
        db = helper.getWritableDatabase();
        mDBoperations = new DBoperations();
        //注册内容观察者
        mContactObserver = new ContactObserver(mContext, mHandler);

        Uri uri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        // 注册内容观察者
        getContentResolver().registerContentObserver(uri, true, mContactObserver);
    }

    private void initView() {
        bt_phone = findViewById(R.id.bt_phone);
        bt_sim = findViewById(R.id.bt_sim);
        bt_insert = findViewById(R.id.bt_insert);
        bt_insertSIM = findViewById(R.id.bt_insertSIM);
        lv_contact = findViewById(R.id.lv_contact);
        bt_phone.setOnClickListener(this);
        bt_sim.setOnClickListener(this);
        bt_insert.setOnClickListener(this);
        bt_insertSIM.setOnClickListener(this);
        adapter = new ContactAdapter(phonelist, mContext);
        lv_contact.setAdapter(adapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //查询本地电话本
                mDBoperations.querydata(db, phonelist,mHandler);
            }
        }).start();
        ListViewclickItem();

        initFastFind();
    }
    //***********************************//快速查找*****************************************************//
    private void initFastFind() {
        ss_sort= (SpellsSortView) findViewById(R.id.ss_sort);
        tv_centerspell= (TextView) findViewById(R.id.tv_centerspell);
        ss_sort.setOnWordsChangeListener(new SpellsSortView.onWordsChangeListener() {
            @Override
            public void wordsChange(String words) {
                Log.e(TAG,"words="+words);
                updateWord(words);
                updateListView(words);
            }
        });
    }
    /**
     * 更新中央的字母提示
     *
     * @param words 首字母
     */
    private void updateWord(String words) {
        tv_centerspell.setText(words);
        tv_centerspell.setVisibility(View.VISIBLE);
        //清空之前的所有消息
        mHandler.removeCallbacksAndMessages(null);
        //1s后让tv隐藏
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {tv_centerspell.setVisibility(View.GONE);
            }
        }, 500);
    }

    /**
     * @param words 首字母
     */
    private void updateListView(String words) {
        for (int i = 0; i < phonelist.size(); i++) {
            String headerWord = phonelist.get(i).getHeaderWord();
            Log.e(TAG,"headerWord="+headerWord);
            //将手指按下的字母与列表中相同字母开头的项找出来
            String word = words.toLowerCase();//大写字母转小写字母
            if (word.equals(headerWord)) {
                //将列表选中哪一个
                lv_contact.setSelection(i);
                //找到开头的一个即可
                return;
            }
        }
    }
    //***************************************end*************************************************//
    private void ListViewclickItem() {
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "position=" + i);
                if(TitleItem == 0){
                    String name = phonelist.get(i).getName();
                    //增删改查
                    showDialog(name, i);
                }else if(TitleItem == 1){
                    showDialogAddSIM(i);
                }


            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_phone: //本机联系人
                Log.e(TAG, "phone");
                TitleItem =0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mDBoperations.querydata(db, phonelist,mHandler);
                    }
                }).start();

                break;
            case R.id.bt_sim://查询sim卡联系人
                Log.e(TAG, "sim");
                TitleItem = 1;
               new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimUtil.getInstance().queryAllContact(mContext,phonelist,mHandler);
                    }
                }).start();
                break;
            case R.id.bt_insert:
                Log.e(TAG, "insert");
                TitleItem =0;
                ShowDialog();
                adapter.notifyDataSetChanged();
                break;
            case  R.id.bt_insertSIM:
                TitleItem = 1;
                ShowDialogSIM();
                adapter.notifyDataSetChanged();
                break;
            //插入名字
            case R.id.bt_OK:
                if(TitleItem == 0){
                    Log.e(TAG, "bt_insertName" + name);
                    name = et_name.getText().toString().trim();
                    jpname = ed_jpname.getText().toString().trim();
                    mDBoperations.insert(mContext, name,jpname);
                    dialog.dismiss();
            }else if(TitleItem == 1){
                    name = et_name.getText().toString().trim();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SimUtil.getInstance().insertContact(name,mContext);
                            dialog.dismiss();
                            mHandler.sendEmptyMessage(3);
                        }
                    }).start();

                }
                break;

        }
    }
    //SIM卡 增删改查
    public  void showDialogAddSIM(final int position){
        final MyDialog dialog = new MyDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_contact_num1_item, null);
        dialog.setLayoutView(view);
        Window window = dialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 400;
        window.setAttributes(p);
        dialog.setCanceledOnTouchOutside(false);//触摸边缘不消失
        dialog.show();
        ly_isjapanName = view.findViewById(R.id.ly_isjapanName);
        ly_isjapanName.setVisibility(View.GONE);
        ed_dia_name = view.findViewById(R.id.ed_dia_name);

        bt_modify_jpname = view.findViewById(R.id.bt_modify_jpname);
        bt_modify_jpname.setVisibility(View.GONE);
        ed_modify_number1 = view.findViewById(R.id.ed_modify_number1);
        ed_modify_number2 = view.findViewById(R.id.ed_modify_number2);
        ed_modify_number3 = view.findViewById(R.id.ed_modify_number3);

        final String name = phonelist.get(position).getName();
        final String number1 = phonelist.get(position).getNumber1();
        final String number2 = phonelist.get(position).getNumber2();
        final String number3 = phonelist.get(position).getNumber3();
        String   numb2 ="";
        String numb3="";
        if(number2 !=null){
              numb2 = number2.split(",")[0];
        }
        if(number3 != null ){
             numb3 = number3.split(":")[0];
        }


        ed_dia_name.setText(name);
        ed_modify_number1.setText(number1);
        ed_modify_number2.setText(numb2);
        ed_modify_number3.setText(numb3);

        bt_modify_name = view.findViewById(R.id.bt_modify_name);
        bt_modify_number = view.findViewById(R.id.bt_modify_number);
        bt_modify_number2 = view.findViewById(R.id.bt_modify_number2);
        bt_modify_number3 = view.findViewById(R.id.bt_modify_number3);
        bt_deleteItem = view.findViewById(R.id.bt_deleteItem);
        bt_deleteItem.setVisibility(View.GONE);

        Button bt_call1 = view.findViewById(R.id.bt_call1);
        Button bt_call2 = view.findViewById(R.id.bt_call2);
        Button bt_call3 = view.findViewById(R.id.bt_call3);


        //拨打电话
        bt_call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number1 = phonelist.get(position).getNumber1();
                if(number1 !=null){
                    CallUtiles.call(mContext, number1);
                    dialog.dismiss();
                    mHandler.sendEmptyMessage(2);
                }else {
                    Toast.makeText(mContext,"号码不能为空",Toast.LENGTH_SHORT).show();
                }

            }
        });
        bt_call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number2 = phonelist.get(position).getNumber2();
                if(number2 !=null){
                    CallUtiles.call(mContext, number2.split(",")[0]);
                    mHandler.sendEmptyMessage(2);
                    dialog.dismiss();
                }else{
                    Toast.makeText(mContext,"号码不能为空",Toast.LENGTH_SHORT).show();
                }



            }
        });
        bt_call3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number3 = phonelist.get(position).getNumber3();
                if(number3 != null){
                    CallUtiles.call(mContext, number3);
                    dialog.dismiss();
                    mHandler.sendEmptyMessage(2);
                }else {
                    Toast.makeText(mContext,"号码不能为空",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //删除item 数据
        bt_deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDBoperations.delete(mContext, name);
                dialog.dismiss();
            }
        });
        //修改名字
        bt_modify_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimUtil.getInstance().SimUpdate(mContext,name,number1,number2,number3,ed_dia_name,ed_modify_number1,ed_modify_number2,ed_modify_number3,mHandler);
                dialog.dismiss();
            }
        });
        bt_modify_jpname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimUtil.getInstance().SimUpdate(mContext,name,number1,number2,number3,ed_dia_name,ed_modify_number1,ed_modify_number2,ed_modify_number3,mHandler);
                dialog.dismiss();
            }
        });
        bt_modify_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimUtil.getInstance().SimUpdate(mContext,name,number1,number2,number3,ed_dia_name,ed_modify_number1,ed_modify_number2,ed_modify_number3,mHandler);                dialog.dismiss();
            }
        });
        bt_modify_number2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimUtil.getInstance().SimUpdate(mContext,name,number1,number2,number3,ed_dia_name,ed_modify_number1,ed_modify_number2,ed_modify_number3,mHandler);                dialog.dismiss();
            }
        });
        bt_modify_number3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimUtil.getInstance().SimUpdate(mContext,name,number1,number2,number3,ed_dia_name,ed_modify_number1,ed_modify_number2,ed_modify_number3,mHandler);
                dialog.dismiss();
            }
        });
    }

    /**
     * 增删改查电话号码，及姓名
     */

    public void showDialog(final String name, final int position) {
        final MyDialog dialog = new MyDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_contact_num1_item, null);
        dialog.setLayoutView(view);
        Window window = dialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 400;
        window.setAttributes(p);
        dialog.setCanceledOnTouchOutside(false);//触摸边缘不消失
        dialog.show();
        ed_dia_name = view.findViewById(R.id.ed_dia_name);
        ed_jpname = view.findViewById(R.id.ed_jpname);
        bt_modify_jpname = view.findViewById(R.id.bt_modify_jpname);
        ed_modify_number1 = view.findViewById(R.id.ed_modify_number1);
        ed_modify_number2 = view.findViewById(R.id.ed_modify_number2);
        ed_modify_number3 = view.findViewById(R.id.ed_modify_number3);

        ed_dia_name.setText(phonelist.get(position).getName());
        String jpname = phonelist.get(position).getJpname();
        if(!"".equals(jpname)){
            ed_jpname.setText(jpname);
        }

        ly_isjapanName =  view.findViewById(R.id.ly_isjapanName);
        if(!Utiles.isLanugEn(mContext)){
            ly_isjapanName.setVisibility(View.VISIBLE);
        }else{
            ly_isjapanName.setVisibility(View.GONE);
        }
        ed_modify_number1.setText(phonelist.get(position).getNumber1());
        ed_modify_number2.setText(phonelist.get(position).getNumber2());
        ed_modify_number3.setText(phonelist.get(position).getNumber3());

        bt_modify_name = view.findViewById(R.id.bt_modify_name);
        bt_modify_number = view.findViewById(R.id.bt_modify_number);
        bt_modify_number2 = view.findViewById(R.id.bt_modify_number2);
        bt_modify_number3 = view.findViewById(R.id.bt_modify_number3);
        bt_deleteItem = view.findViewById(R.id.bt_deleteItem);


        Button bt_call1 = view.findViewById(R.id.bt_call1);
        Button bt_call2 = view.findViewById(R.id.bt_call2);
        Button bt_call3 = view.findViewById(R.id.bt_call3);


        //拨打电话
        bt_call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallUtiles.call(mContext, phonelist.get(position).getNumber1());
                dialog.dismiss();
                mHandler.sendEmptyMessage(2);
            }
        });
        bt_call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallUtiles.call(mContext, phonelist.get(position).getNumber2());
                dialog.dismiss();
                mHandler.sendEmptyMessage(2);
            }
        });
        bt_call3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallUtiles.call(mContext, phonelist.get(position).getNumber3());
                dialog.dismiss();
                mHandler.sendEmptyMessage(2);
            }
        });
        //删除item 数据
        bt_deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDBoperations.delete(mContext, name);
                dialog.dismiss();
            }
        });
        //修改名字
        bt_modify_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newname = ed_dia_name.getText().toString().trim();
                mDBoperations.updateName(mContext, name, newname);
                dialog.dismiss();
            }
        });
        bt_modify_jpname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jpname = ed_jpname.getText().toString().trim();
                mDBoperations.updatejpName(mContext,name,jpname);
                dialog.dismiss();
            }
        });
        bt_modify_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number1 = ed_modify_number1.getText().toString();
                mDBoperations.updateNumber1(mContext, name, number1);
                dialog.dismiss();
            }
        });
        bt_modify_number2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number2 = ed_modify_number2.getText().toString();
                mDBoperations.updateNumber2(mContext, name, number2);
                dialog.dismiss();
            }
        });
        bt_modify_number3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number3 = ed_modify_number3.getText().toString();
                mDBoperations.updateNumber3(mContext, name, number3);
                dialog.dismiss();
            }
        });
        //修改完查询数据库，刷新界面
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimUtil.getInstance().queryAllContact(mContext,phonelist,mHandler);
            }
        }).start();
    }

    /**
     * 添加联系人本机
     */
    public void ShowDialog() {
        dialog = new MyDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_contact_item, null);
        dialog.setLayoutView(view);
        Window window = dialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 300;
        window.setAttributes(p);
        dialog.setCanceledOnTouchOutside(false);//触摸边缘不消失
        dialog.show();

        et_name = view.findViewById(R.id.ed_name);
        ed_jpname = view.findViewById(R.id.ed_jpname);
        ed_number1 = view.findViewById(R.id.ed_number1);

        bt_OK = view.findViewById(R.id.bt_OK);
        tv_insertName = view.findViewById(R.id.tv_insertName);
        tv_number1 = view.findViewById(R.id.tv_number1);

        bt_OK.setOnClickListener(this);

        ly_isjapan = view.findViewById(R.id.ly_isjapan_name);
        if (!Utiles.isLanugEn(mContext)) {
            ly_isjapan.setVisibility(View.VISIBLE);
        } else {
            ly_isjapan.setVisibility(View.GONE);
        }
    }

    /**
     * 添加联系人到SIM
     */
    public void ShowDialogSIM() {
        dialog = new MyDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_contact_item, null);
        dialog.setLayoutView(view);
        Window window = dialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 300;
        window.setAttributes(p);
        dialog.setCanceledOnTouchOutside(false);//触摸边缘不消失
        dialog.show();
        ly_isjapan = view.findViewById(R.id.ly_isjapan_name);
        ly_isjapan.setVisibility(View.GONE);

        et_name = view.findViewById(R.id.ed_name);
        bt_OK = view.findViewById(R.id.bt_OK);
        bt_OK.setOnClickListener(this);


    }


}
