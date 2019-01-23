package telephony.internal.android.com.phoneui.ActivityPhoneBook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb.DBoperations;
import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.mode.PhoneContaceData;
import telephony.internal.android.com.phoneui.utiles.Utiles;
import telephony.internal.android.com.phoneui.view.MyDialog;

/**
 * Created by yangbofeng on 2018/7/2.
 * 电话本编辑页面
 */

public class EditPhoneBookActivity extends Activity implements View.OnClickListener {
    private String TAG = "EditPhoneBookActivity";

    private TextView tv_edit_name, tv_edit_phone1, tv_edit_phone2, tv_edit_phone3, tv_edit_jpname;
    private LinearLayout ly_jpname, ly_name, ly_number2, ly_number3, ly_number1;
    private Button bt_edit_up, bt_edit_dowm, bt_edit_people_ok;
    private FrameLayout fy_edit, fy_people_item;
    private TextView tv_edit_title, tv_edit_title_shurufa;
    private EditText ed_modify_name;

    private int position;
    private ArrayList<PhoneContaceData> list;
    private Context mContext;
    private int index = 0;
    private MyDialog editDialog;

    private MyBroadcastReceiver myBroadcastReceiver;
    private int times_ok = 0; //ok次数
    private int times_up = 0; //ok次数
    private int index_item = 0; //ok次数
    //save dialog
    private MyDialog SaveDialog;
    ImageView img_editphonebook_yes;
    ImageView img_editphonebook_no;
    boolean isSave = true;
    //show edit phone number
    private MyDialog editPhoneNumber;
    ImageView img_dial;
    ImageView img_edit;
    ImageView img_speeddial;
    //获取通话记录界面插入电话号码
    private String phoneNumber = "";
    private String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_edit_phone_book);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        String positions = intent.getStringExtra("position");
        //获取通话录添加号码
        phoneNumber = intent.getStringExtra("phoneNumber");
        tag = intent.getStringExtra("tag");


        if (positions != null) {
            position = Integer.parseInt(positions);
        }
        list = (ArrayList<PhoneContaceData>) intent.getSerializableExtra("phonelist");
        Log.e(TAG, "position=" + position);
        if (list != null) {
            Log.e(TAG, "phonelist= " + list.size());
        }

        initView();
        initViewSetHighlight();
        IntentFilter filter = new IntentFilter();
        filter.addAction("up");
        filter.addAction("down");
        filter.addAction("left");
        filter.addAction("right");
        filter.addAction("ok");
        filter.addAction("cancle");
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, filter);
    }


    private void initView() {
        tv_edit_name = findViewById(R.id.tv_edit_name);
        tv_edit_jpname = findViewById(R.id.tv_edit_jpname);
        tv_edit_phone1 = findViewById(R.id.tv_edit_phone1);
        tv_edit_phone2 = findViewById(R.id.tv_edit_phone2);
        tv_edit_phone3 = findViewById(R.id.tv_edit_phone3);

        ly_name = findViewById(R.id.ly_name);
        ly_jpname = findViewById(R.id.ly_jpname);
        ly_number1 = findViewById(R.id.ly_number1);
        ly_number2 = findViewById(R.id.ly_number2);
        ly_number3 = findViewById(R.id.ly_number3);

        fy_edit = findViewById(R.id.fy_edit);
        fy_people_item = findViewById(R.id.fy_people_item);

        tv_edit_title = findViewById(R.id.tv_edit_title);
        tv_edit_title_shurufa = findViewById(R.id.tv_edit_title_shurufa);
        ed_modify_name = findViewById(R.id.ed_modify_name);
        //判断系统语言是否显示日文名字
        if (Utiles.getInstance().isLanugEn(mContext)) {
            ly_jpname.setVisibility(View.GONE);
        } else {
            ly_jpname.setVisibility(View.VISIBLE);
        }

        bt_edit_up = findViewById(R.id.bt_edit_up);
        bt_edit_dowm = findViewById(R.id.bt_edit_dowm);
        bt_edit_people_ok = findViewById(R.id.bt_edit_people_ok);
        bt_edit_up.setOnClickListener(this);
        bt_edit_dowm.setOnClickListener(this);
        bt_edit_people_ok.setOnClickListener(this);
        showPeopleInfo();
        initEditView();
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    /**
     * 初始化进来，显示人员名字，电话信息
     */
    private void showPeopleInfo() {
        //如果从电话本界面打开直接填数据进来
        if (list == null) {
            return;
        }
        if (list.size() > 0) {
            String name = list.get(position).getName();
            String jpname = list.get(position).getJpname();
            String number1 = list.get(position).getNumber1();
            String number2 = list.get(position).getNumber2();
            String number3 = list.get(position).getNumber3();
            Log.e(TAG, "[name]=" + name + "[jpname]=" + jpname + "[number1]=" + number1 + "[number2]=" + number2 + "[number3]=" + number3);
            if (Utiles.getInstance().isLanugEn(mContext)) { //英语系统
                ly_jpname.setVisibility(View.GONE);
                if (jpname == null ||"".equals(jpname)) {
                    if(name == null || "".equals(name)){
                        tv_edit_name.setText("");
                    }else {
                        tv_edit_name.setText(name);
                    }

                } else {
                    tv_edit_name.setText(name + jpname);
                }

            } else {
                ly_jpname.setVisibility(View.VISIBLE);
                if(name ==null || "".equals(name)){
                    tv_edit_name.setText("");
                }else {
                    tv_edit_name.setText(name);
                }

                tv_edit_jpname.setText(jpname);
            }
            tv_edit_phone1.setText(number1);
            tv_edit_phone2.setText(number2);
            tv_edit_phone3.setText(number3);
        }
    }

    /**
     * 根据tag判断是否从通话记录界面打开
     * 通话记录界面打开电话编辑界面
     */
    public void initEditView() {
        Log.e(TAG, "通话记录电话插入到电话本" + tag + "[number]=" + phoneNumber);
        if ("callRecord".equals(tag)) {
            tv_edit_phone1.setText(phoneNumber);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_edit_up:
                if (index <= 0) {
                    index = 5;
                }
                index -= 1;
                Log.e(TAG, "onclick" + "up" + "index=" + index);
                initViewSetHighlight();
                break;
            case R.id.bt_edit_dowm:
                if (index >= 4) {
                    index = -1;
                }
                index += 1;
                Log.e(TAG, "onclick" + "down" + "index=" + index);
                initViewSetHighlight();
                break;
            case R.id.bt_edit_people_ok:
                Log.e(TAG, "onclick" + "---ok" + "index=" + index);
                break;

        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "keyCode=" + keyCode);

        return super.onKeyDown(keyCode, event);

    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "action=" + action);
            if (action.equals("up")) {

            } else if (action.equals("down")) {


            } else if (action.equals("left")) {
                isSave = true;
                img_editphonebook_no.setVisibility(View.INVISIBLE);
                img_editphonebook_yes.setVisibility(View.VISIBLE);
            } else if (action.equals("right")) {
                isSave = false;
                img_editphonebook_no.setVisibility(View.VISIBLE);
                img_editphonebook_yes.setVisibility(View.INVISIBLE);

            } else if (action.equals("ok")) {
                times_ok += 1;
                Log.e(TAG, "tiems_ok=" + times_ok);

                if (times_ok == 1) {//编辑提醒
                    showDialogToast();

                }
                if (times_ok == 2) {//进入到编辑界面
                    editDialog.dismiss();
                    fy_edit.setVisibility(View.VISIBLE);
                    fy_people_item.setVisibility(View.GONE);
                    initDialogEditPage();
                }
                if (times_ok == 3) {
                    showSaVeDialog();
                }
                if (times_ok == 4) {
                    if (isSave) {//是否保存
                        //名字插入数据库
                        SavePeopleInfo();
                        SaveDialog.dismiss();
                        editDialog.dismiss();
                        //保存完后回到人的信息页面
                        fy_edit.setVisibility(View.GONE);
                        fy_people_item.setVisibility(View.VISIBLE);
                        //显示姓名，电话
                    } else {
                        SaveDialog.dismiss();
                    }
                    times_ok = 0;
                }

            } else if (action.equals("cancle")) {
                if (editDialog != null) {
                    editDialog.dismiss();
                }
                if (SaveDialog != null) {
                    SaveDialog.dismiss();
                }
                if (editPhoneNumber != null) {
                    editPhoneNumber.dismiss();
                }

                times_ok = 0;
            }
            ;
        }


    }

    /**
     * 判断显示编辑提示dialog
     * 还是电话号码编辑/拨号/添加到快速拨号
     */
    private void showDialogToast() {
        String number1 = "";
        String number2 = "";
        String number3 = "";
        if (list != null) {
            number1 = list.get(position).getNumber1();
            number2 = list.get(position).getNumber2();
            number3 = list.get(position).getNumber3();
        }
        switch (index) {
            case 0:
                showDialogEdit();
                break;
            case 1:
                showDialogEdit();
                break;
            case 2:
                if ("".equals(number1) || number1 == null) {
                    showDialogEdit();
                } else {
                    showEditPhoneNumberDialog();
                }
                break;
            case 3:
                if ("".equals(number2) || number2 == null) {
                    showDialogEdit();
                } else {
                    showEditPhoneNumberDialog();
                }
                break;
            case 4:
                if ("".equals(number3) || number3 == null) {
                    showDialogEdit();
                } else {
                    showEditPhoneNumberDialog();
                }
                break;
        }

    }


    /**
     * 保存修改后数据 到数据库
     */

    private void SavePeopleInfo() {
        String name = "";
        String newdata = ed_modify_name.getText().toString().trim();
        if (list != null) {
            name = list.get(position).getName();
        }

        //姓名
        if (index == 0) {
            times_ok = 0;
            if ("".equals(newdata) || newdata == null) { //名字为空，提示进入编辑
                showDialogEdit();
                times_ok = 1;
            } else { //名字不为空，则编辑为修改
                if (newdata == null || "".equals(newdata)) {
                    if (tag.equals("callRecord")) { //通话记录界面打开的
                        DBoperations.getInstance().insertNameAndNumber(mContext, "", phoneNumber);
                    } else {
                        DBoperations.getInstance().updateName(mContext, name, "");
                    }

                } else {
                    if (tag.equals("callRecord")) { //通话记录界面保存姓名和电话
                        DBoperations.getInstance().insertNameAndNumber(mContext, newdata, phoneNumber);
                        //名字返回到第一个页面
                        Intent intent = new Intent();
                        intent.putExtra("name", newdata);
                        setResult(1, intent);
                    } else if(tag.equals("EditPhoneBookToastActivity")) {
                        DBoperations.getInstance().insert(mContext,newdata,"");
                    }else {
                        DBoperations.getInstance().updateName(mContext, name, newdata);
                    }

                }

                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            }
            tv_edit_name.setText(newdata);

        }
        //日本姓名
        if (index == 1) {
            times_ok = 0;
            if ("".equals(newdata) || newdata == null) {
                showDialogEdit();
            } else {
                if (newdata == null || "".equals(newdata)) {
                    DBoperations.getInstance().updatejpName(mContext, name, "");
                } else {
                    DBoperations.getInstance().updatejpName(mContext, name, newdata);
                }

                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            }
            tv_edit_jpname.setText(newdata);
        }
        //电话1
        if (index == 2) {
            Log.e(TAG, "newdata" + newdata);
            if ("".equals(newdata) || null == newdata) {
                showDialogEdit();
                times_ok = 1;
            } else {
                if (newdata == null || "".equals(newdata)) {
                    DBoperations.getInstance().updateNumber1(mContext, name, "");
                } else {
                    DBoperations.getInstance().updateNumber1(mContext, name, newdata);
                }

                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            }
            tv_edit_phone1.setText(newdata);
            times_ok = 0;
        }
        //电话2
        if (index == 3) {
            if ("".equals(newdata) || newdata == null) {
                showDialogEdit();
                times_ok = 1;
            } else {
                if (newdata == null || "".equals(newdata)) {
                    DBoperations.getInstance().updateNumber2(mContext, name, "");
                } else {
                    DBoperations.getInstance().updateNumber2(mContext, name, newdata);
                }

                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            }
            tv_edit_phone2.setText(newdata);
            times_ok = 0;
        }
        //电话3
        if (index == 4) {

            if ("".equals(newdata) || newdata == null) {
                showDialogEdit();
                times_ok = 1;
            } else {
                if (newdata == null || "".equals(newdata)) {
                    DBoperations.getInstance().updateNumber3(mContext, name, "");
                } else {
                    DBoperations.getInstance().updateNumber3(mContext, name, newdata);
                }

                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            }

            tv_edit_phone3.setText(newdata);
            times_ok = 0;
        }

    }

    /**
     * 存在号码的情况下/拨号、编辑、设置为快速拨号
     */
    private void showEditPhoneNumberDialog() {
        editPhoneNumber = new MyDialog(this);
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit_phone_book_editnumber, null);
        editPhoneNumber.setLayoutView(dialog);
        Window window = editPhoneNumber.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 400;
        p.height = 200;
        window.setAttributes(p);
        editPhoneNumber.setCanceledOnTouchOutside(false);
        editPhoneNumber.show();

        img_dial = dialog.findViewById(R.id.dialog_edit_phone_book_editnumber_dial);
        img_edit = dialog.findViewById(R.id.dialog_edit_phone_book_editnumber_edit);
        img_speeddial = dialog.findViewById(R.id.dialog_edit_phone_book_editnumber_speedDial);
        setViewVisible(index_item);
    }

    ;

    /**
     * 设置三角图片是否可见
     *
     * @param times_down
     */
    private void setViewVisible(int times_down) {
        switch (times_down) {
            case 0:
                img_edit.setVisibility(View.VISIBLE);
                img_dial.setVisibility(View.INVISIBLE);
                img_speeddial.setVisibility(View.INVISIBLE);
                break;
            case 1:
                img_edit.setVisibility(View.INVISIBLE);
                img_dial.setVisibility(View.VISIBLE);
                img_speeddial.setVisibility(View.INVISIBLE);
                break;
            case 2:
                img_edit.setVisibility(View.INVISIBLE);
                img_dial.setVisibility(View.INVISIBLE);
                img_speeddial.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 保存的dialog /Save?
     */
    public void showSaVeDialog() {
        SaveDialog = new MyDialog(this);
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit_phone_book_save, null);
        SaveDialog.setLayoutView(dialog);
        Window window = SaveDialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 400;
        p.height = 300;
        window.setAttributes(p);
        SaveDialog.setCanceledOnTouchOutside(false);
        SaveDialog.show();
        img_editphonebook_yes = dialog.findViewById(R.id.img_editphonebook_yes);
        img_editphonebook_no = dialog.findViewById(R.id.img_editphonebook_no);

    }

    /**
     * 初始化编辑页面的标题信息
     */
    public void initDialogEditPage() {
        tv_edit_title_shurufa.setText("Aa");
        if (index == 0) {
            tv_edit_title.setText("[EDIT]Name");
            if (list != null) {
                ed_modify_name.setText(list.get(position).getName());
                if ("".equals((list.get(position).getName())) || null == (list.get(position).getName())) {
                } else {
                    ed_modify_name.setSelection(list.get(position).getName().length());//光标定位到最后一个字符
                }
            }
        } else if (index == 1) {
            tv_edit_title.setText("[New]JPName");
            if (list != null) {
                ed_modify_name.setText(list.get(position).getJpname());
                if ("".equals((list.get(position).getJpname())) || null == (list.get(position).getJpname())) {
                } else {
                    ed_modify_name.setSelection(list.get(position).getJpname().length());//光标定位到最后一个字符
                }
            }

        } else if (index == 2) {
            tv_edit_title.setText("[NEW]Phone1");
            if (list != null) {
                ed_modify_name.setText(list.get(position).getNumber1());
                if ("".equals((list.get(position).getNumber1())) || null == (list.get(position).getNumber1())) {
                } else {
                    ed_modify_name.setSelection(list.get(position).getNumber1().length());//光标定位到最后一个字符
                }
            }
        } else if (index == 3) {
            tv_edit_title.setText("[NEW]Phone2");
            if (list != null) {
                ed_modify_name.setText(list.get(position).getNumber2());
                if ("".equals((list.get(position).getNumber2())) || null == (list.get(position).getNumber2())) {
                } else {
                    ed_modify_name.setSelection(list.get(position).getNumber2().length());
                }
            }

        } else if (index == 4) {
            tv_edit_title.setText("[NEW]Phone3");
            if (list != null) {
                ed_modify_name.setText(list.get(position).getNumber3());
                if ("".equals((list.get(position).getNumber3())) || null == (list.get(position).getNumber3())) {
                } else {
                    ed_modify_name.setSelection(list.get(position).getNumber3().length());//光标定位到最后一个字符
                }
            }

        }
        ed_modify_name.setCursorVisible(true);// 显示光标
        //获取键盘事件
        ed_modify_name.setFocusable(true);
        ed_modify_name.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) ed_modify_name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(ed_modify_name, 0);
    }

    /**
     * 显示编辑提醒Dialog  /Edit
     */
    private void showDialogEdit() {
        editDialog = new MyDialog(this);
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_edit_phone_book, null);
        editDialog.setLayoutView(dialog);
        Window window = editDialog.getWindow();
        final WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = 400;
        p.height = 300;
        window.setAttributes(p);
        editDialog.setCanceledOnTouchOutside(false);
        editDialog.show();

    }


    //设置条目高亮
    private void initViewSetHighlight() {
        Log.e(TAG, "index=" + index);
        switch (index) {
            case 0:
                ly_name.setBackgroundColor(getResources().getColor(R.color.bg_selected));
                ly_jpname.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number1.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number2.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number3.setBackgroundColor(getResources().getColor(R.color.backgroud));
                break;
            case 1:
                ly_name.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_jpname.setBackgroundColor(getResources().getColor(R.color.bg_selected));
                ly_number1.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number2.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number3.setBackgroundColor(getResources().getColor(R.color.backgroud));
                break;
            case 2:
                ly_name.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_jpname.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number1.setBackgroundColor(getResources().getColor(R.color.bg_selected));
                ly_number2.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number3.setBackgroundColor(getResources().getColor(R.color.backgroud));
                break;
            case 3:
                ly_name.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_jpname.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number1.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number2.setBackgroundColor(getResources().getColor(R.color.bg_selected));
                ly_number3.setBackgroundColor(getResources().getColor(R.color.backgroud));
                break;
            case 4:
                ly_name.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_jpname.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number1.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number2.setBackgroundColor(getResources().getColor(R.color.backgroud));
                ly_number3.setBackgroundColor(getResources().getColor(R.color.bg_selected));
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        Log.e(TAG, "onDetory");
    }

}
