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
import telephony.internal.android.com.phoneui.Voice.adapter.VoiceRecodingAdapter;
import telephony.internal.android.com.phoneui.Voice.mode.ItemBean;
import telephony.internal.android.com.phoneui.utiles.SettingsUtil;

import static android.app.ActivityThread.TAG;

/**
 * Created by yangbofeng on 2018/7/16.
 * 设置录音时长
 */

public class VoiceSettingRecordingActivity extends Activity implements View.OnClickListener {

    private Button bt_up_recoding, bt_dowm_recoding, bt_ok_recoding;
    private ListView lv_voice_recoding;
    private ArrayList<ItemBean> list = new ArrayList<>();
    private Context mContext;
    private VoiceRecodingAdapter adapter;
    private int recodingTime = 0;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_setting_recoding);
        mContext = getApplicationContext();
        initButton();
        initView();
        initListData();
        initData();
    }

    private void initButton() {
        bt_up_recoding = findViewById(R.id.bt_up_recoding);
        bt_dowm_recoding =  findViewById(R.id.bt_dowm_recoding);
        bt_ok_recoding =  findViewById(R.id.bt_ok_recoding);
        bt_up_recoding.setOnClickListener(this);
        bt_dowm_recoding.setOnClickListener(this);
        bt_ok_recoding.setOnClickListener(this);
    }

    private void initListData() {
        recodingTime = SettingsUtil.getInstance().getdataInt(mContext, "RECODING_TIME");
        Log.e(TAG,"recodingTime="+recodingTime);
        if(-1 ==recodingTime){
            recodingTime = 30;
        }
        list.clear();
        ItemBean item1 = new ItemBean();
        item1.setTitle("30sec");
        if (30*1000 == recodingTime) {
            item1.setTag(recodingTime);
        } else {
            item1.setTag(0);
        }
        list.add(item1);
        ItemBean item2 = new ItemBean();
        item2.setTitle("1min");
        if (60*1000 == recodingTime) {
            item2.setTag(recodingTime);
        } else {
            item2.setTag(0);
        }
        list.add(item2);
        ItemBean item3 = new ItemBean();
        item3.setTitle("3min");
        if (180*1000 == recodingTime) {
            item3.setTag(recodingTime);
        } else {
            item3.setTag(0);
        }
        list.add(item3);
        ItemBean item4 = new ItemBean();
        item4.setTitle("5min");
        if (300*1000 == recodingTime) {
            item4.setTag(recodingTime);
        } else {
            item4.setTag(0);
        }
        list.add(item4);
    }


    private void initView() {
        bt_up_recoding = findViewById(R.id.bt_up_recoding);
        bt_dowm_recoding = findViewById(R.id.bt_dowm_recoding);
        bt_ok_recoding = findViewById(R.id.bt_ok_recoding);
        lv_voice_recoding = findViewById(R.id.lv_voice_recoding);
    }

    private void initData() {
        adapter = new VoiceRecodingAdapter(mContext, list);
        lv_voice_recoding.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_up_recoding:
                if (index <= 0) {
                    index = 4;
                }
                index -= 1;
                setLight();
                break;
            case R.id.bt_dowm_recoding:
                if (index >= 3) {
                    index = -1;
                }
                index += 1;
                Log.e(TAG, "index=" + index);
                setLight();
                break;
            case R.id.bt_ok_recoding:
                switch (index){
                    case 0: //30s
                        SettingsUtil.getInstance().setdataInt(mContext, "RECODING_TIME", 30*1000);
                        break;
                    case 1://60s
                        SettingsUtil.getInstance().setdataInt(mContext, "RECODING_TIME", 60*1000);
                        break;
                    case 2://180s
                        SettingsUtil.getInstance().setdataInt(mContext, "RECODING_TIME", 180*1000);
                        break;
                    case 3://300s
                        SettingsUtil.getInstance().setdataInt(mContext, "RECODING_TIME", 300*1000);
                        break;
                }

                initListData();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 设置高亮
     *
     * @param
     */
    public void setLight() {
        adapter.setSelectPosition(index);
        lv_voice_recoding.smoothScrollToPosition(index);
    }
}
