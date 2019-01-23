package telephony.internal.android.com.phoneui.ActivityPhoneBook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import telephony.internal.android.com.phoneui.R;

/**
 * Created by yangbofeng on 2018/7/10.
 */

public class EditPhoneBookToastActivity extends Activity {

    private LinearLayout ly_toast_NE,ly_delete_all;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone_book_toast);
        ly_toast_NE = findViewById(R.id.ly_toast_NE);
        ly_delete_all = findViewById(R.id.ly_delete_all);
        ly_toast_NE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditBookActivity();
                finish();
            }
        });
    }


    public void openEditBookActivity(){
        Intent intent = new Intent(this,EditPhoneBookActivity.class);
        intent.putExtra("tag", "EditPhoneBookToastActivity");
        startActivity(intent);
    }

}
