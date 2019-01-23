package com.tricheer.test.phone.phonebook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tricheer.test.phone.R;
import com.tricheer.test.phone.phonebook.PhoneContaceData;

import java.util.ArrayList;

/**
 * Created by yangbofeng on 2018/6/12.
 */

public class ContactAdapter extends BaseAdapter {
    private String TAG = "ContactAdapter";
    private ArrayList<PhoneContaceData> list;
    private Context mcontext;
   /* private String[] numb2;
    private String[] numb3;
    private String number2;
    private String number3;
    private String name;*/


    public ContactAdapter(ArrayList<PhoneContaceData> list, Context mcontext) {
        this.list = list;
        this.mcontext = mcontext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder hodel = null;
        if (convertView == null) {
            hodel = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
            convertView = layoutInflater.inflate(R.layout.contact_item, null);

            hodel.tv_name = convertView.findViewById(R.id.tv_name);
            hodel.tv_number1 = convertView.findViewById(R.id.tv_number1);
            hodel.tv_number2 = convertView.findViewById(R.id.tv_number2);
            hodel.tv_number3 = convertView.findViewById(R.id.tv_number3);

            convertView.setTag(hodel);
        } else {
            hodel = (ViewHolder) convertView.getTag();
        }
        final String name = list.get(i).getName();
         String jpname = list.get(i).getJpname();
        String number1 = list.get(i).getNumber1();
        String  number2 = list.get(i).getNumber2();
        String  number3 = list.get(i).getNumber3();
        Log.e(TAG,"[number2]="+number2+"[---number3]="+number3);

        if ("".equals(name)) {
            hodel.tv_name.setText("");
        } else {
            if (jpname == null || "".equals(jpname)) {
                hodel.tv_name.setText(name);
            } else {
                hodel.tv_name.setText(name + "\n" + jpname);
            }

        }


        if ("".equals(number1)) {
            hodel.tv_number1.setText("");
        } else {
            hodel.tv_number1.setText(number1);
        }
        if ("".equals(number2)||number2 == null ) {
            hodel.tv_number2.setText("");
        } else {
            hodel.tv_number2.setText(number2.split(",")[0]);
        }
        if ("".equals(number3)||number3 == null) {
            hodel.tv_number3.setText("");
        } else {
            hodel.tv_number3.setText(number3.split(":")[0]);
        }
/*

        hodel.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "name");

            }
        });
        hodel.tv_number1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "number1");
            }
        });
        hodel.tv_number2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "number2");
            }
        });
        hodel.tv_number3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "number3");
            }
        });
*/
        return convertView;
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_number1;
        TextView tv_number2;
        TextView tv_number3;

    }


}
