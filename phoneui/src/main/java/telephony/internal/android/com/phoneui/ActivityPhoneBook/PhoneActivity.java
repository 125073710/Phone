package telephony.internal.android.com.phoneui.ActivityPhoneBook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.adapter.PhoneBookAdapter;
import telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb.ContactObserver;
import telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb.DBoperations;
import telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneBookdb.DatabaseHelper;
import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.mode.PhoneContaceData;
import telephony.internal.android.com.phoneui.utiles.Utiles;
import telephony.internal.android.com.phoneui.view.JpSpellsSortView;
import telephony.internal.android.com.phoneui.view.SpellsSortView;

import static telephony.internal.android.com.phoneui.R.id.lv_people_item;

/**
 * Created by yangbofeng on 2018/6/29.
 * 电话本联系人显示
 */

public class PhoneActivity extends Activity implements View.OnClickListener {
    private String TAG = "PhoneActivity";
    private TextView tv_size;
    private ListView lv_people_Phone;
    private Button lv_left;
    private Button lv_up;
    private Button lv_dowm;
    private Button lv_right;
    private Button lv_ok;
    DatabaseHelper helper;
    SQLiteDatabase db;
    private ContactObserver mContactObserver;
    private Context mContext;
    ArrayList<PhoneContaceData> phonelist = new ArrayList<>();
    ArrayList<PhoneContaceData> lastCharlist = new ArrayList<>();
    PhoneBookAdapter adapter;
    private int REFERSH_ADAPTER = 0;//刷新adapter
    private int position = 0;
    private SpellsSortView en_phone_sort;
    private JpSpellsSortView jp_phone_sort;
    private int index_ABC = 0;
    private String Word = "A";
    private int people_size = 0; //判断字母中的人名数，为0 时打开插入页码
    private Handler mThreadHandler;
    private boolean isEn = false; //判断系统语言
    private LinearLayout en_Phone_layout;
    private LinearLayout jp_Phone_layout;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    tv_size.setText("# " + lastCharlist.size());
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    people_size = phonelist.size();
                    Log.e(TAG, "people_size=" + people_size);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_phone_activity_book);
        mContext = getApplicationContext();
        initWord();
        initView();
        initDB();
        phoneNumberSize();

    }

    /**
     * 获取联系人总数
     */
    public void phoneNumberSize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBoperations.getInstance().qury(mContext, lastCharlist, mHandler);
            }
        }).start();

    }

    private void initView() {
        isEn = Utiles.getInstance().isLanugEn(mContext);
        tv_size = findViewById(R.id.tv_size);
        lv_people_Phone = findViewById(lv_people_item);
        lv_left = findViewById(R.id.lv_left);
        lv_up = findViewById(R.id.lv_up);
        lv_dowm = findViewById(R.id.lv_dowm);
        lv_right = findViewById(R.id.lv_right);
        lv_ok = findViewById(R.id.lv_ok);

        lv_left.setOnClickListener(this);
        lv_right.setOnClickListener(this);
        lv_up.setOnClickListener(this);
        lv_dowm.setOnClickListener(this);
        lv_ok.setOnClickListener(this);
        en_Phone_layout = findViewById(R.id.en_Phone_layout);
        jp_Phone_layout = findViewById(R.id.jp_Phone_layout);
        if (isEn) {
            en_Phone_layout.setVisibility(View.VISIBLE);
            jp_Phone_layout.setVisibility(View.GONE);
        } else {
            en_Phone_layout.setVisibility(View.GONE);
            jp_Phone_layout.setVisibility(View.VISIBLE);
        }
        en_phone_sort = (SpellsSortView) findViewById(R.id.en_phone_sort);
        jp_phone_sort = (JpSpellsSortView) findViewById(R.id.jp_phone_sort);

        setQuitFindListener();
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
     * 设置快速查找监听
     */
    public void setQuitFindListener() {
        if (isEn) {
            en_phone_sort.setOnWordsChangeListener(new SpellsSortView.onWordsChangeListener() {
                @Override
                public void wordsChange(String words) {
                    Log.e(TAG, "words=" + words);
                    Word = words;
                    getdbData();

                }
            });
        } else {
            jp_phone_sort.setOnWordsChangeListener(new JpSpellsSortView.onWordsChangeListener() {
                @Override
                public void wordsChange(String words) {
                    Word = words;
                    getdbData();
                }
            });
        }
    }

    /**
     * 根据高亮，指定让显示到第一行
     * 未用
     *
     * @param words
     */
    private void updateListView(String words) {
        for (int i = 0; i < phonelist.size(); i++) {
            String headerWord = phonelist.get(i).getHeaderWord();
            //将手指按下的字母与列表中相同字母开头的项找出来
            String word = words.toLowerCase();//大写字母转小写字母
            if (word.equals(headerWord)) {
                //将列表选中哪一个
                lv_people_Phone.setSelection(i);
                //找到开头的一个即可
                return;
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initDB() {
        helper = new DatabaseHelper(this, "contact.db", null, 1);
        db = helper.getWritableDatabase();
        //注册内容观察者
        mContactObserver = new ContactObserver(mContext, mHandler);

        Uri uri = Uri.parse("content://com.tricheer.phone.phonebook/data");
        // 注册内容观察者
        getContentResolver().registerContentObserver(uri, true, mContactObserver);

        //设置adapter
        adapter = new PhoneBookAdapter(phonelist, mContext);
        lv_people_Phone.setAdapter(adapter);
       /* DBoperations.getInstance().insert(mContext,";","");
        DBoperations.getInstance().insert(mContext,"-","");
        DBoperations.getInstance().insert(mContext,"%","");*/
        /*mDBoperations.insert(mContext,"dfadf","adfad");
        mDBoperations.insert(mContext,"yyyyy","");
        mDBoperations.insert(mContext,"bbbbb","adfad");
        mDBoperations.insert(mContext,"cccc","adfad");
        mDBoperations.insert(mContext,"dddd","");*/
        //管理子线程销毁问题
        HandlerThread mHandlerThread = new HandlerThread("PhoneThread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
        getdbData();
    }

    /**
     * 第一次获取数据库
     */
    public void getdbData() {
        mThreadHandler.post(mBackgroundRunnable);
    }

    Runnable mBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            DBoperations.getInstance().querydata(db, phonelist, mHandler, Word, mContext);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "[onResume]phone size=" + phonelist.size());
        getdbData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lv_left:
                index_ABC -= 1;
                Log.e(TAG, "index_ABC=" + index_ABC);
                if (isEn) {
                    if (index_ABC < 0) {
                        index_ABC = 26;
                    }
                    en_phone_sort.setKeyEvent(index_ABC);
                } else {
                    if (index_ABC < 0) {
                        index_ABC = 10;
                    }
                    jp_phone_sort.setKeyEvent(index_ABC);
                }


                break;
            case R.id.lv_right:
                index_ABC += 1;

                if (isEn) {
                    if (index_ABC >= 27) {
                        index_ABC = 0;
                    }
                    en_phone_sort.setKeyEvent(index_ABC);
                } else {
                    if (index_ABC >= 11) {
                        index_ABC = 0;
                    }
                    jp_phone_sort.setKeyEvent(index_ABC);
                }
                Log.e(TAG, "index_ABC=" + index_ABC);

                break;
            case R.id.lv_up:
                position -= 1;
                if (position <= 0) {
                    position = 0;
                }
                adapter.setSelectPosition(position);
                lv_people_Phone.smoothScrollToPosition(position);
                Log.e(TAG, "[position=]" + position);
                break;
            case R.id.lv_dowm:
                Log.e(TAG, "[List size=]" + phonelist.size());
                position += 1;
                if (position >= phonelist.size()) {
                    position = phonelist.size() - 1;
                }
                adapter.setSelectPosition(position);
                lv_people_Phone.smoothScrollToPosition(position);
                Log.e(TAG, "[position=]" + position);
                break;
            case R.id.lv_ok:
                if ( phonelist.size() > 0) {
                    Intent intent = new Intent(this, EditPhoneBookActivity.class);
                    intent.putExtra("position", position + "");
                    intent.putExtra("phonelist", phonelist);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, EditPhoneBookToastActivity.class);
                    startActivity(intent);
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        mThreadHandler.removeCallbacks(mBackgroundRunnable);
        super.onDestroy();

    }
}
