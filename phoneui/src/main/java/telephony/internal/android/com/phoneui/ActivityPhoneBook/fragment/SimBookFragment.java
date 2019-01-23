package telephony.internal.android.com.phoneui.ActivityPhoneBook.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.EditSimBookActivity;
import telephony.internal.android.com.phoneui.ActivityPhoneBook.adapter.PhoneBookAdapter;
import telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb.SimUtil;
import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.mode.PhoneContaceData;
import telephony.internal.android.com.phoneui.utiles.Utiles;
import telephony.internal.android.com.phoneui.view.JpSpellsSortView;
import telephony.internal.android.com.phoneui.view.SpellsSortView;

/**
 * Created by yangbofeng on 2018/7/4.
 * SIM卡 联系人主界面
 */

public class SimBookFragment extends Fragment implements View.OnClickListener {

    private String TAG = "SimBookFragment";
    private View view;
    private JpSpellsSortView mJpSpellsSortView;
    private SpellsSortView mSpellsSortView;
    private ListView lv_people_sim;
    private ArrayList<PhoneContaceData> simlist = new ArrayList<>();
    private Context mContext;
    private PhoneBookAdapter adapter;
    private Handler mThreadHandler;
    private int REFRESH_SIM_DATA = 0;
    private String Word = "";
    //显示日文还是英文匹配
    private LinearLayout jp_layout;
    private LinearLayout en_layout;
    private TextView tv_jpsim_size;
    private TextView tv_en_size;
    //当前系统语言是否为英语
    private boolean isEn;
    private int position = 0;//第一个条目
    private int index_ABC = 0;//第一个字母
    private int people_size = 0; //判断字母中的人名数，为0 时打开插入页码

    private Button bt_left_sim, bt_right_sim, bt_ok_sim, bt_up_sim, bt_dowm_sim;

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    people_size =  simlist.size();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化线程 只需创建一次
        HandlerThread mHandlerThread = new HandlerThread("SimThread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sim_book, container, false);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();
        initOnClickView();
        initWord();
        initView();
        questFind();
        initListView();

        querySimData();
    }

    /**
     * 初始化点击事件
     */
    private void initOnClickView() {
        bt_left_sim = view.findViewById(R.id.bt_left_sim);
        bt_right_sim = view.findViewById(R.id.bt_right_sim);
        bt_ok_sim = view.findViewById(R.id.bt_ok_sim);
        bt_up_sim = view.findViewById(R.id.bt_up_sim);
        bt_dowm_sim = view.findViewById(R.id.bt_dowm_sim);

        bt_left_sim.setOnClickListener(this);
        bt_right_sim.setOnClickListener(this);
        bt_ok_sim.setOnClickListener(this);
        bt_up_sim.setOnClickListener(this);
        bt_dowm_sim.setOnClickListener(this);
    }

    /**
     * 初始化 根据系统语言判断第一个要匹配的的字母
     */
    private void initWord() {
        isEn = Utiles.getInstance().isLanugEn(mContext);
        if (isEn) {
            Word = "A";
        } else {
            Word = "あ";
        }
    }

    /**
     * 根据系统语言显示 索引是日文还是英文
     */
    private void initView() {
        jp_layout = view.findViewById(R.id.jp_layout);
        en_layout = view.findViewById(R.id.en_layout);
        tv_jpsim_size = view.findViewById(R.id.tv_jpsim_size);
        tv_en_size = view.findViewById(R.id.tv_en_size);
        //填写SIM卡联系人数



        if (isEn) {
            jp_layout.setVisibility(View.GONE);
            en_layout.setVisibility(View.VISIBLE);
            tv_en_size.setText(SimUtil.getInstance().getSIMsize(mContext) + "");
        } else {
            jp_layout.setVisibility(View.VISIBLE);
            en_layout.setVisibility(View.GONE);
            tv_jpsim_size.setText(SimUtil.getInstance().getSIMsize(mContext) + "");
        }


    }

    /**
     * 查询SIM卡数据库
     */
    private void querySimData() {
        //管理子线程销毁问题
        mThreadHandler.post(mSimThread);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isEn){
            mSpellsSortView.setKeyEvent(index_ABC);
        }else {
            mJpSpellsSortView.setKeyEvent(index_ABC);
        }


    }

    /**
     * 子线程查数据
     */
    Runnable mSimThread = new Runnable() {
        @Override
        public void run() {
            SimUtil.getInstance().queryAllContact(mContext, simlist, mhandler, Word);
        }
    };

    /**
     * 初始化ListView
     */
    public void initListView() {
        lv_people_sim = view.findViewById(R.id.lv_people_sim);
        adapter = new PhoneBookAdapter(simlist, mContext);
        lv_people_sim.setAdapter(adapter);
    }

    /**
     * 快速索引jp
     */
    public void questFind() {
        mJpSpellsSortView = (JpSpellsSortView) view.findViewById(R.id.js_sort);
        mSpellsSortView = (SpellsSortView) view.findViewById(R.id.en_sort);
        if (isEn) { //如果是英文，给英文状态栏设置监听
            mSpellsSortView.setOnWordsChangeListener(new SpellsSortView.onWordsChangeListener() {
                @Override
                public void wordsChange(String words) {
                    Word = words;
                    querySimData();
                }
            });
        } else {//如果是日文，给日文状态栏设置监听
            mJpSpellsSortView.setOnWordsChangeListener(new JpSpellsSortView.onWordsChangeListener() {
                @Override
                public void wordsChange(String words) {
                    Word = words;
                    Log.e(TAG, " word=" + words);
                    querySimData();
                }
            });
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mThreadHandler.removeCallbacks(mSimThread);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_up_sim:
                position -= 1;
                if (position <= 0) {
                    position = 0;
                }
                adapter.setSelectPosition(position);
                lv_people_sim.smoothScrollToPosition(position);
                break;
            case R.id.bt_dowm_sim:
                position +=1;
                if (position >= simlist.size()) {
                    position = simlist.size() - 1;
                }
                adapter.setSelectPosition(position);
                lv_people_sim.smoothScrollToPosition(position);
                break;
            case R.id.bt_left_sim:
                index_ABC -= 1;
                if(isEn){
                    if (index_ABC < 0) {
                        index_ABC = 26;
                    }
                    mSpellsSortView.setKeyEvent(index_ABC);
                }else {
                    if(index_ABC < 0){
                        index_ABC = 10;
                    }
                    mJpSpellsSortView.setKeyEvent(index_ABC);
                }
                break;
            case R.id.bt_right_sim:
                index_ABC += 1;
                if(isEn){
                    if (index_ABC >= 27) {
                        index_ABC = 0;
                    }
                    mSpellsSortView.setKeyEvent(index_ABC);
                }else {
                    if(index_ABC >=11){
                        index_ABC = 0;
                    }
                    mJpSpellsSortView.setKeyEvent(index_ABC);
                }
                break;
            case R.id.bt_ok_sim:
                    Intent intent = new Intent(getActivity(), EditSimBookActivity.class);
                    intent.putExtra("position", position + "");
                    intent.putExtra("phonelist", simlist);
                    startActivity(intent);
                break;
        }
    }
}
