package com.tricheer.test.phone.View;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tricheer.test.phone.R;

/**
 * Created by yangbofeng on 2018/6/19.
 */

public class ContactInserPage extends Activity {
    private String TAG="ContactInserPage";
    private EditText ed_name;
    private TextView tv_number;
    private Button bt_inser;
    private  Context mContext;
    private   String  name;
    private   String  number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_inser_page);
        mContext= getApplicationContext();
        initView();

    }

    private void initView() {
        ed_name = findViewById(R.id.ed_inser_name);

        tv_number = findViewById(R.id.tv_text);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        number  =bundle.getString("phonenumber");
        tv_number.setText(number);
        bt_inser = findViewById(R.id.bt_inser_contact);
        bt_inser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name  = ed_name.getText().toString().trim();
            insert(mContext,name,number);
            finish();
            }
        });
    }

    /**
     * 插入到电话本
     * @return
     */
    public void insert(Context mContext, String name ,String number1) {

        ContentResolver contentResolver =mContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.tricheer.test.phone.contact/data");
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("number1", number1);
        Uri uri = contentResolver.insert(insertUri, values);
    }
}
