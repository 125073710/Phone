package telephony.internal.android.com.phoneui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.PhoneActivity;
import telephony.internal.android.com.phoneui.ActivityPhoneBook.SIMCardActivity;

/**
 * Created by yangbofeng on 2018/6/29.
 * 电话本
 */

public class PhoneBook extends Activity {
    private TextView tv_Phone;
    private TextView tv_SIM_card;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_phone_book);
        mContext = getApplicationContext();
        initView();
    }

    private void initView() {
        tv_Phone = findViewById(R.id.tv_Phone);
        tv_SIM_card = findViewById(R.id.tv_SIM_card);
        //本机联系人
        tv_Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PhoneActivity.class);
                startActivity(intent);
            }
        });
        //sim 卡联系人
        tv_SIM_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SIMCardActivity.class);
                startActivity(intent);
            }
        });
    }
}
