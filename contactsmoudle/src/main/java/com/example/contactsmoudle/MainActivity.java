package com.example.contactsmoudle;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    private String TAG ="MainActivity";
    private List<Person> list=new ArrayList<>();
    private MyTongXunLuAdapter adapter;
    private ListView lv_list;
    private SpellsSortView ss_sort;
    private TextView tv_centerspell;
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏actionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        lv_list= (ListView) findViewById(R.id.lv_list);
        ss_sort= (SpellsSortView) findViewById(R.id.ss_sort);
        tv_centerspell= (TextView) findViewById(R.id.tv_centerspell);
        ss_sort.setOnWordsChangeListener(new SpellsSortView.onWordsChangeListener() {
            @Override
            public void wordsChange(String words) {
                updateWord(words);
                updateListView(words);
            }
        });
        init();
    }
    //初始化一些操作
    private void init() {
        list = new ArrayList<>();
        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                cols, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            // 取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String name = cursor.getString(nameFieldColumnIndex);
            String number = cursor.getString(numberFieldColumnIndex);
            Person person=new Person();
            person.setName(name);
            person.setUserphone(number);
            list.add(person);
        }

        Uri uri = Uri.parse("content://icc/adn"  );
        Cursor cursor1 = getContentResolver().query(uri, null, null, null, null);
        while (cursor1.moveToNext()) {
            String name = cursor1.getString(0);
            String number = cursor1.getString(1);
            String emails = cursor1.getString(2);
            String id = cursor1.getString(3);
            Log.d(TAG, "simcardinfo=" + name+number);
            Person person1=new Person();
            person1.setName(name);
            person1.setUserphone(number);
            list.add(person1);
        }


        //对集合排序
        Collections.sort(list, new Comparator<Person>() {
            @Override
            public int compare(Person lhs, Person rhs) {
                //根据拼音进行排序
                return lhs.getPinyin().compareTo(rhs.getPinyin());
            }
        });
        adapter = new MyTongXunLuAdapter(this, list);
        lv_list.setAdapter(adapter);
        lv_list.setOnScrollListener(this);
    }

    //查询sim联系人
    public void queryAllContact() {
        Uri uri = Uri.parse("content://icc/adn"  );
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        Log.d(TAG, "cursor count=" + cursor.getCount());
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String number = cursor.getString(1);
            String emails = cursor.getString(2);
            String id = cursor.getString(3);
            Log.d(TAG, "simcardinfo=" + name+number);
        }
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
        handler.removeCallbacksAndMessages(null);
        //1s后让tv隐藏
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {tv_centerspell.setVisibility(View.GONE);
            }
        }, 500);
    }
    /**
     * @param words 首字母
     */
    private void updateListView(String words) {
        for (int i = 0; i < list.size(); i++) {
            String headerWord = list.get(i).getHeaderWord();
            //将手指按下的字母与列表中相同字母开头的项找出来
            if (words.equals(headerWord)) {
                //将列表选中哪一个
                lv_list.setSelection(i);
                //找到开头的一个即可
                return;
            }
        }
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //当滑动列表的时候，更新右侧字母列表的选中状态
        ss_sort.setTouchIndex(list.get(firstVisibleItem).getHeaderWord());
    }
}
