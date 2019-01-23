package com.tricheer.test.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tricheer.test.phone.R;
import com.tricheer.test.phone.model.PhoneNumberData;

import java.util.ArrayList;

/**
 * Created by yangbofeng on 2018/6/8.
 * 通话记录
 */

public class CarllRecoderAdapter extends BaseAdapter {

    private static final String TAG = "CarllRecoderAdapter_ybf";
    private ArrayList<PhoneNumberData> list;
    private Context mcontext;
    private int defaultSelection = -1;
    private String name = "";

    public CarllRecoderAdapter(ArrayList<PhoneNumberData> list, Context mcontext) {
        this.mcontext = mcontext;
        this.list = list;
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        //  Log.e(TAG,"getView");
        ViewHolder hodel = null;
        if (convertView == null) {
            hodel = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
            convertView = layoutInflater.inflate(R.layout.list_item, null);

            hodel.tv_phonenumber = convertView.findViewById(R.id.tv_numb);
            hodel.tv_name = convertView.findViewById(R.id.tv_name);
            hodel.tv_date = convertView.findViewById(R.id.tv_date);
            hodel.tv_times = convertView.findViewById(R.id.tv_times);
            hodel.tv_states = convertView.findViewById(R.id.tv_states);
            convertView.setTag(hodel);
        } else {
            hodel = (ViewHolder) convertView.getTag();
        }

        hodel.tv_phonenumber.setText(list.get(position).getPhoneNumber() + "");
        hodel.tv_times.setText(list.get(position).getTimes() + "秒");
        hodel.tv_date.setText(list.get(position).getDate() + "");
        hodel.tv_states.setText(list.get(position).getStates());
        hodel.tv_name.setText(list.get(position).getName());
        if (position == defaultSelection) {// 选中时设置单纯颜色
            int bg_selected_color = mcontext.getResources().getColor(R.color.bg_selected);// 背景选中的颜色
            convertView.setBackgroundColor(bg_selected_color);
        } else {// 未选中时设置selector
            convertView.setBackgroundResource(R.drawable.listview_color_selector);
        }
        return convertView;
    }




    /**
     * @param position
     *            设置高亮状态的item
     */
    public void setSelectPosition(int position) {
        if (!(position < 0 || position > list.size())) {
            defaultSelection = position;
            notifyDataSetChanged();
        }
    }

    class ViewHolder  {
        TextView tv_phonenumber;
        TextView tv_name;
        TextView tv_date;
        TextView tv_times;
        TextView tv_states;
    }

}
