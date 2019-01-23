package com.example.contactsmoudle;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/* auther cgq
 * 创建时间:   2016/11/17 19:22
 * 描述:       ListView列表适配器
 */
public class MyTongXunLuAdapter extends BaseAdapter {
    private List<Person> list;
    private Context mcontext;
    private LayoutInflater inflater;
    private int CALL_PHONE_CODE=1001;
    private int SEND_SMS_CODE=1002;
    public MyTongXunLuAdapter(Context context, List<Person> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.mcontext=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_tongxunlu, null);
            holder.tv_word = (TextView) convertView.findViewById(R.id.tv_word);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Person person = list.get(position);
        holder.tv_word.setText(person.getHeaderWord());
        holder.tv_name.setText(person.getName());
        //将相同字母开头的合并在一起
        if (position == 0) {
            //第一个是一定显示的
            holder.tv_word.setVisibility(View.VISIBLE);
        } else {
            //后一个与前一个对比,判断首字母是否相同，相同则隐藏
            String headerWord = list.get(position - 1).getHeaderWord();
            if (person.getHeaderWord().equals(headerWord)) {
                holder.tv_word.setVisibility(View.GONE);
            } else {
                holder.tv_word.setVisibility(View.VISIBLE);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(person.getUserphone());
            }
        });
        return convertView;
    }

    private void showDialog(final String phoneNum) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mcontext).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.dialog_tongxulu_info);
                TextView tv_title = (TextView) window
                        .findViewById(R.id.tv_callphone);
                TextView tv_senmessage = (TextView) window
                        .findViewById(R.id.tv_senmessage);
                tv_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if(Build.VERSION.SDK_INT >= 23){     //表示ANdroid6.0
                            if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.CALL_PHONE)//定位,oncreate里此方法，如果用户选择打开蓝牙，则不调用，此处调用
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mcontext, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CODE);
                            } else {   //已经授权权限
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                Uri data = Uri.parse("tel:" + phoneNum);
                                intent.setData(data);
                                mcontext.startActivity(intent);
                            }
                        }else{
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Uri data = Uri.parse("tel:" + phoneNum);
                            intent.setData(data);
                            mcontext.startActivity(intent);
                        }
                    }
                });
                tv_senmessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if(Build.VERSION.SDK_INT >= 23){     //表示ANdroid6.0
                            if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.SEND_SMS)//定位,oncreate里此方法，如果用户选择打开蓝牙，则不调用，此处调用
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mcontext, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_CODE);
                            } else {   //已经授权权限
                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri
                                        .parse("smsto:" + phoneNum));
                                // 如果需要将内容传过去增加如下代码
                                mcontext.startActivity(intent);
                            }
                        }else{
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri
                                    .parse("smsto:" + phoneNum));
                            // 如果需要将内容传过去增加如下代码
                            mcontext.startActivity(intent);
                        }
                    }
                });

    }

    private class ViewHolder {
        private TextView tv_word;
        private TextView tv_name;
    }
}
