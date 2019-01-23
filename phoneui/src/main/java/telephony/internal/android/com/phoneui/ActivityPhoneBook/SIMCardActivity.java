package telephony.internal.android.com.phoneui.ActivityPhoneBook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import telephony.internal.android.com.phoneui.R;

/**
 * Created by yangbofeng on 2018/7/4.
 * SIM卡电话本/一级界面、
 */

public class SIMCardActivity extends Activity {
    private String TAG = "SIMCardActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_card);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }
}
