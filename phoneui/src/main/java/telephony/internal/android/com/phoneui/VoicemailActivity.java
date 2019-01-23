package telephony.internal.android.com.phoneui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import telephony.internal.android.com.phoneui.Voice.VoiceMessageActivity;
import telephony.internal.android.com.phoneui.Voice.VoiceSettingActivity;

/**
 * Created by yangbofeng on 2018/7/10.
 */

public class VoicemailActivity extends Activity implements View.OnClickListener {
    private String TAG = "VoicemailActivity";
    private Context mContext;
    private LinearLayout ly_message, ly_message_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicemail);
        mContext = getApplicationContext();
        initView();
    }

    private void initView() {
        ly_message = findViewById(R.id.ly_message);
        ly_message_setting = findViewById(R.id.ly_message_setting);
        ly_message.setOnClickListener(this);
        ly_message_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_message:
                Intent intent = new Intent(VoicemailActivity.this, VoiceMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.ly_message_setting:
                Intent intent2 = new Intent(VoicemailActivity.this, VoiceSettingActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
