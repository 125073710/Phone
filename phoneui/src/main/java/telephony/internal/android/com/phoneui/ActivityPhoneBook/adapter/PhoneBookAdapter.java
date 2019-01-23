package telephony.internal.android.com.phoneui.ActivityPhoneBook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.mode.PhoneContaceData;

import static android.content.ContentValues.TAG;


/**
 * Created by yangbofeng on 2018/7/2.
 */

public class PhoneBookAdapter extends BaseAdapter {

    private ArrayList<PhoneContaceData> list;
    private Context mContext;
    private int defaultSelection = 0;



    public PhoneBookAdapter(ArrayList<PhoneContaceData> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder hodel = null;
        if(convertView == null ){
            hodel = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.phone_bool_lv_item, null);
            hodel.tv_name = convertView.findViewById(R.id.tv_name);
            convertView.setTag(hodel);
        }else {
            hodel =(ViewHolder) convertView.getTag();
        }
        String  name = list.get(position).getName();
        String jpname = list.get(position).getJpname();
        if ("".equals(name)) {
            hodel.tv_name.setText("");
        } else {
            if (jpname == null || "".equals(jpname)) {
                hodel.tv_name.setText(name);
            } else {
                hodel.tv_name.setText(name + " " + jpname);
            }

        }
        if (position == defaultSelection) {// 选中时设置单纯颜色
            int bg_selected_color = mContext.getResources().getColor(R.color.bg_selected);// 背景选中的颜色
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

    //只显示匹配的人
    private void updateListView(String words,View view) {
        for (int i = 0; i < list.size(); i++) {
            String headerWord = list.get(i).getHeaderWord();
            //将手指按下的字母与列表中相同字母开头的项找出来
            String word = words.toLowerCase();//大写字母转小写字母
            Log.e(TAG,"headerWord="+headerWord);
            Log.e(TAG,"word="+word);
            if (word.equals(headerWord)) {
                //将列表选中哪一个
                Log.e(TAG,"VISIBLE");
                view.setVisibility(View.VISIBLE);
            }else {
                Log.e(TAG,"gone");
                view.setVisibility(View.GONE);
            }
        }
    }
    class  ViewHolder {
        TextView tv_name;
    }
}
