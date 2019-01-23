package telephony.internal.android.com.test;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String TAG ="MainActivity test ";

    private Button bt_ok1,bt_ok2,bt_ok3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_main);

        Log.e("1103","oncreat");
        bt_ok1 = (Button) findViewById(R.id.bt_ok1);
        bt_ok2 = (Button) findViewById(R.id.bt_ok2);
        bt_ok3 = (Button) findViewById(R.id.bt_ok3);
        bt_ok1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentB fragment = new FragmentB();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.add(R.id.fragment1,fragment);
                transaction2.commit();
            }
        });
        bt_ok2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentA fragment = new FragmentA();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.add(R.id.fragment1,fragment);
                transaction2.commit();
            }
        });

        bt_ok3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFiles("/storage/sdcard0/YBF_record/");

            }
        });
    }



    public ArrayList<String> getFiles(String realpath) {
         ArrayList<String> files = new ArrayList<>();
        files.clear();
        File realFile = new File(realpath);
        File[] subfiles = realFile.listFiles();
        for (File file : subfiles) {
            if (file.isDirectory()) {
                getFiles(file.getAbsolutePath());
            } else {
                files.add(file.toString());
                Log.e(TAG, "file.toString()=" + file.toString());
            }
        }
        Log.e(TAG, "file size=" + files.size());
        return files;
    }
    /**
     * 产生4位随机数
     * @return
     */
    public long getRandomNumber(int n){
        if(n<1){
            throw new IllegalArgumentException("随机数位数必须大于0");
        }
        return (long)(Math.random()*9*Math.pow(10,n-1)) + (long)Math.pow(10,n-1);
    }

    //时间格式化
    public String   getTime(){
        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
        Date d1=new Date(time);
        String t1=format.format(d1);
        Log.e(TAG, t1);
        return t1;
    }
    public void insertName() {
        ContentValues values = new ContentValues();
        Uri uri = Uri.parse("content://icc/adn");
        values.put("tag", "bbb");
        values.put("number", "1111");
        values.put("emails", "88888");
        values.put("anrs", "66666");
        Uri insertInfo = getContentResolver().insert(uri, values);
        Log.d(TAG, insertInfo.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG,"onKeyDown="+keyCode);
        switch (keyCode){
            case 23:

                break;
        }
        return super.onKeyDown(keyCode, event);

    }
    public  void player1(){
       /* MediaPlayer mplayer=MediaPlayer.create(this,R.raw.yuyin);
        mplayer.start();//开始播放*/
    }


    //ybf
    final Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Log.e(TAG,"执行");
                    handler.sendEmptyMessageDelayed(0,6000);
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
