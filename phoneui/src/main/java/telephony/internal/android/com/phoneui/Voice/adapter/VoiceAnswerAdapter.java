package telephony.internal.android.com.phoneui.Voice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import telephony.internal.android.com.phoneui.R;
import telephony.internal.android.com.phoneui.Voice.mode.ItemBean;

/**
 * Created by yangbofeng on 2018/7/16.
 */

public class VoiceAnswerAdapter extends BaseAdapter {
    private String TAG = "VoiceAnswerAdapter";
    private Context mContext;
    private ArrayList<ItemBean> list;
    private int defaultSelection = 0;

    public  VoiceAnswerAdapter(Context mContext, ArrayList<ItemBean> list){
        this.mContext =mContext;
        this.list =list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder hodel = null;
        if(convertView == null ){
            hodel = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.adapter_voice_answer, null);
            hodel.tv_adapter_voice_title = convertView.findViewById(R.id.tv_adapter_voice_title);
            hodel.img_liang = convertView.findViewById(R.id.img_liang);
            convertView.setTag(hodel);
        }else {
            hodel =(ViewHolder) convertView.getTag();
        }
        hodel.tv_adapter_voice_title.setText(list.get(position).getTitle());
        int tag = list.get(position).getTag();
        switch (tag){
            case -1:
                hodel.img_liang.setVisibility(View.GONE);
                break;
            case 0:
                hodel.img_liang.setImageDrawable(mContext.getDrawable(R.drawable.sanjiao));
                hodel.img_liang.setVisibility(View.VISIBLE);
                break;
            case 3*1000:
                hodel.img_liang.setImageDrawable(mContext.getDrawable(R.drawable.sanjiao));
                hodel.img_liang.setVisibility(View.VISIBLE);
                break;
            case 5*1000:
                hodel.img_liang.setImageDrawable(mContext.getDrawable(R.drawable.sanjiao));
                hodel.img_liang.setVisibility(View.VISIBLE);
                break;
            case 10*1000:
                hodel.img_liang.setImageDrawable(mContext.getDrawable(R.drawable.sanjiao));
                hodel.img_liang.setVisibility(View.VISIBLE);
                break;
            case 30*1000:
                hodel.img_liang.setImageDrawable(mContext.getDrawable(R.drawable.sanjiao));
                hodel.img_liang.setVisibility(View.VISIBLE);
                break;
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

    class  ViewHolder {
        TextView tv_adapter_voice_title;
        ImageView img_liang;
    }
}
