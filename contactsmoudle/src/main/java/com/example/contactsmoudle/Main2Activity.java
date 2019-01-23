package com.example.contactsmoudle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private Button btn_mytongxunlu;
    private int READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn_mytongxunlu = (Button) findViewById(R.id.btn_mytongxunlu);

        btn_mytongxunlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {     //表示ANdroid6.0
                    if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_CONTACTS)//定位,oncreate里此方法，如果用户选择打开蓝牙，则不调用，此处调用
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
                    } else {   //已经授权权限
                        startActivity(new Intent(Main2Activity.this, MainActivity.class));
                    }
                } else {
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //地址
                startActivity(new Intent(Main2Activity.this, MainActivity.class));
            } else {
                Toast.makeText(Main2Activity.this, "您没有授权，请在设置中打开授权,保证正常使用", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
