package telephony.internal.android.com.phoneui.Voice.SettingActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.Voice.adapter.VoiceAnswerAdapter;
import telephony.internal.android.com.phoneui.Voice.mode.ItemBean;
import telephony.internal.android.com.phoneui.utiles.SettingsUtil;


/**
 * Created by yangbofeng on 2018/7/16.
 * 设置页面
 */

public class VoiceSettingAnswerActivity extends Activity implements View.OnClickListener {
    private String TAG = "VoiceSettingAnswerActivity";
    private int index = 0;
    private int time;
    private Context mContext;
    private ListView lv_voice_answer;
    private ArrayList<ItemBean> list = new ArrayList<>();
    private Button bt_up_answer, bt_dowm_answer, bt_ok_answer;
    private VoiceAnswerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_setting_answer);
        mContext = getApplicationContext();
        initView();
        initListData();
        setLight();
    }

    /**
     * 添加数据
     */
    private void initListData() {
        time = SettingsUtil.getInstance().getdataInt(mContext, "ANSWER_TIME");
        Log.e(TAG, "time=" + time);
        list.clear();
        ItemBean item1 = new ItemBean();
        item1.setTitle("0sec");
        if (0 == time) {
            item1.setTag(time);
            index =0;
        } else {
            item1.setTag(-1);
        }
        list.add(item1);
        ItemBean item2 = new ItemBean();
        item2.setTitle("3sec");
        if (3*1000 == time) {
            item2.setTag(time);
            index =1;
        } else {
            item2.setTag(-1);
        }
        list.add(item2);
        ItemBean item3 = new ItemBean();
        item3.setTitle("5sec");
        if (5*1000 == time) {
            item3.setTag(time);
            index =2;
        } else {
            item3.setTag(-1);
        }
        list.add(item3);
        ItemBean item4 = new ItemBean();
        item4.setTitle("10sec");
        if (10*1000 == time) {
            item4.setTag(time);
            index =3;
        } else {
            item4.setTag(-1);
        }

        list.add(item4);
        ItemBean item5 = new ItemBean();
        item5.setTitle("30sec");
        if (30*1000 == time) {
            item5.setTag(time);
            index =4;
        } else {
            item5.setTag(-1);
        }

        list.add(item5);
    }

    private void initView() {
        //bt up dowm
        bt_up_answer = findViewById(R.id.bt_up_answer);
        bt_dowm_answer = findViewById(R.id.bt_dowm_answer);
        bt_ok_answer = findViewById(R.id.bt_ok_answer);
        bt_up_answer.setOnClickListener(this);
        bt_dowm_answer.setOnClickListener(this);
        bt_ok_answer.setOnClickListener(this);

        lv_voice_answer = findViewById(R.id.lv_voice_answer);
        adapter = new VoiceAnswerAdapter(mContext, list);
        lv_voice_answer.setAdapter(adapter);
    }


    /**
     * 设置高亮
     *
     * @param
     */
    public void setLight() {
        adapter.setSelectPosition(index);
        lv_voice_answer.smoothScrollToPosition(index);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_up_answer:
                if (index <= 0) {
                    index = 5;
                }
                index -= 1;
                setLight();
                break;
            case R.id.bt_dowm_answer:
                if (index >= 4) {
                    index = -1;
                }
                index += 1;
                Log.e(TAG, "index=" + index);
                setLight();
                break;
            case R.id.bt_ok_answer:
                switch (index){
                    case 0:
                        SettingsUtil.getInstance().setdataInt(mContext, "ANSWER_TIME", 0);
                        break;
                    case 1:
                        SettingsUtil.getInstance().setdataInt(mContext, "ANSWER_TIME", 3*1000);
                        break;
                    case 2:
                        SettingsUtil.getInstance().setdataInt(mContext, "ANSWER_TIME", 5*1000);
                        break;
                    case 3:
                        SettingsUtil.getInstance().setdataInt(mContext, "ANSWER_TIME", 10*1000);
                        break;
                    case 4:
                        SettingsUtil.getInstance().setdataInt(mContext, "ANSWER_TIME", 30*1000);
                        break;
                }

                initListData();
                adapter.notifyDataSetChanged();
                break;
        }
    }
}
