package telephony.internal.android.com.phoneui.ActivityPhoneBook;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.fragment.EditSimBookAddPeopelFragmet;
import telephony.internal.android.com.phoneui.ActivityPhoneBook.fragment.EditSimBookFragmet;
import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.mode.PhoneContaceData;

/**
 * Created by yangbofeng on 2018/7/4.
 * 显示编辑界面 或提醒界面
 */

public class EditSimBookActivity extends Activity {
    private String TAG = "EditSimBookActivity";
    private int position =0;
    private ArrayList<PhoneContaceData> list; //获取传递过来的集合
    private Pressed mPressed;
    private boolean isregeist = false;
    private EditSimBookFragmet mEditSimBookFragmet;
    private   EditSimBookAddPeopelFragmet mEditSimBookAddPeopelFragmet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sim_book);
        initGetActivityData();
        initOpenFragment();

    }

    /**
     * 获取传递过来的数据
     */
    private void initGetActivityData() {
        Intent intent = getIntent();
        String positions = intent.getStringExtra("position");
        if(positions != null){
            position = Integer.parseInt(positions);
        }
        list = (ArrayList<PhoneContaceData>) intent.getSerializableExtra("phonelist");

    }
    /**
     * 根集合数判断打开哪个页面
     */
    private void initOpenFragment() {
        if(list.size()>0){ //编辑界面
            Log.e(TAG,"[list size]="+list.size());
           /* mEditSimBookFragmet = new EditSimBookFragmet();
            FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
            transaction2.add(R.id.framelayout,mEditSimBookFragmet);
            transaction2.commit();*/
            Intent intent = new Intent(this, EditSimPhoneBookActivity.class);
            intent.putExtra("position", position + "");
            intent.putExtra("phonelist", list);
            startActivity(intent);
            finish();


        }else { //提醒界面
            Log.e(TAG,"[list size]="+list.size());
             mEditSimBookAddPeopelFragmet = new EditSimBookAddPeopelFragmet();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.framelayout,mEditSimBookAddPeopelFragmet);
            transaction.commit();

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG,"keyCode="+keyCode);
        if(isregeist){
            mPressed.onkeyDown(keyCode);
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * fragment 里面注册键盘监听事件
     */
    public interface Pressed{
       void  onkeyDown(int code);
    }

    public void setonKeyDownListener(Pressed mPressd,boolean isregeist){
        this.mPressed = mPressd;
        this.isregeist =isregeist;
    }

    public void unSetKeyDownLinster(boolean isregest){
      this.isregeist  =isregest;
    }
}
