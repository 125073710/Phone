package telephony.internal.android.com.phoneui.ActivityPhoneBook.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.mode.PhoneContaceData;
import telephony.internal.android.com.phoneui.utiles.Utiles;

/**
 * Created by yangbofeng on 2018/7/4.
 * SIM卡联系人编辑界面
 */

public class EditSimBookFragmet extends Fragment {
    private String TAG="EditSimBookFragmet";
    private View view;
    private TextView tv_edit_name, tv_edit_phone1, tv_edit_phone2, tv_edit_phone3, tv_edit_jpname;
    private LinearLayout ly_jpname, ly_name, ly_number2, ly_number3, ly_number1;
    private Button bt_edit_up, bt_edit_dowm, bt_edit_people_ok;
    private FrameLayout fy_edit, fy_people_item;
    private TextView tv_edit_title, tv_edit_title_shurufa;
    private EditText ed_modify_name;
    private ArrayList<PhoneContaceData> list;
    private int position =0;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_edit_phone_book, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
        mContext = getActivity();
        initView();
        initGetActivityData();
        showPeopleInfo();
    }

    /**
     * 获取点击条目进来后显示的数据
     */
    private void initGetActivityData() {
        Intent intent = getActivity().getIntent();
        String positions = intent.getStringExtra("position");
        position = Integer.parseInt(positions);
        list = (ArrayList<PhoneContaceData>) intent.getSerializableExtra("phonelist");
        Log.e(TAG,"list size="+list.size());
    }

    /**
     * 初始化View
     */
    private void initView() {
        tv_edit_name = view.findViewById(R.id.tv_edit_name);
        tv_edit_jpname = view.findViewById(R.id.tv_edit_jpname);
        tv_edit_phone1 = view.findViewById(R.id.tv_edit_phone1);
        tv_edit_phone2 = view.findViewById(R.id.tv_edit_phone2);
        tv_edit_phone3 = view.findViewById(R.id.tv_edit_phone3);

        ly_name = view.findViewById(R.id.ly_name);
        ly_jpname = view.findViewById(R.id.ly_jpname);
        ly_number1 =view. findViewById(R.id.ly_number1);
        ly_number2 = view.findViewById(R.id.ly_number2);
        ly_number3 = view.findViewById(R.id.ly_number3);
        fy_edit = view.findViewById(R.id.fy_edit);
        fy_people_item = view.findViewById(R.id.fy_people_item);

        tv_edit_title = view.findViewById(R.id.tv_edit_title);
        tv_edit_title_shurufa = view.findViewById(R.id.tv_edit_title_shurufa);
        ed_modify_name = view.findViewById(R.id.ed_modify_name);
        if (Utiles.getInstance().isLanugEn(mContext)) {
            ly_jpname.setVisibility(View.GONE);
        } else {
            ly_jpname.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化进来，显示人员名字，电话信息
     */
    private void showPeopleInfo() {
        //如果从电话本界面打开直接填数据进来
        if (list.size() > 0) {
            String name = list.get(position).getName();
            String jpname = list.get(position).getJpname();
            if("".equals(jpname)||jpname == null){
                jpname = "";
            }
            String number1 = list.get(position).getNumber1();
            String number2 = list.get(position).getNumber2();
            String number3 = list.get(position).getNumber3();

            if (Utiles.getInstance().isLanugEn(mContext)) {
                ly_jpname.setVisibility(View.GONE);
                tv_edit_name.setText(name + jpname);
            } else {
                ly_jpname.setVisibility(View.VISIBLE);
                tv_edit_name.setText(name);
                tv_edit_jpname.setText(jpname);
            }
            tv_edit_phone1.setText(number1);
            tv_edit_phone2.setText(number2);
            tv_edit_phone3.setText(number3);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }
}
