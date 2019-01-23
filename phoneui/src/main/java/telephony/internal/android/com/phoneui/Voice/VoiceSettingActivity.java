package telephony.internal.android.com.phoneui.Voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.Voice.SettingActivity.VoiceSettingAnswerActivity;
import telephony.internal.android.com.phoneui.Voice.SettingActivity.VoiceSettingRecordingActivity;
import telephony.internal.android.com.phoneui.Voice.SettingActivity.VoiceSettingUserActivity;

/**
 * Created by yangbofeng on 2018/7/12.
 */

public class VoiceSettingActivity extends Activity implements View.OnClickListener {
    private LinearLayout tv_voice_answer, tv_voice_recoding, tv_voice_greeting, tv_voice_user_voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_setting);
        iniView();
    }

    private void iniView() {
        tv_voice_answer = findViewById(R.id.tv_voice_answer);
        tv_voice_recoding = findViewById(R.id.tv_voice_recoding);
        tv_voice_greeting = findViewById(R.id.tv_voice_greeting);
        tv_voice_user_voice = findViewById(R.id.tv_voice_user_voice);
        tv_voice_answer.setOnClickListener(this);
        tv_voice_recoding.setOnClickListener(this);
        tv_voice_greeting.setOnClickListener(this);
        tv_voice_user_voice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_voice_answer:
                Intent intent = new Intent(this,VoiceSettingAnswerActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_voice_recoding:
                Intent intent2 = new Intent(this,VoiceSettingRecordingActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_voice_greeting:
                break;
            case R.id.tv_voice_user_voice:
                Intent intent4 = new Intent(this,VoiceSettingUserActivity.class);
                startActivity(intent4);
                break;
        }
    }
}
