package telephony.internal.android.com.phoneui.ActivityPhoneBook.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import telephony.internal.android.com.phoneui.ActivityPhoneBook.EditSimBookActivity;
import telephony.internal.android.com.phoneui.ActivityPhoneBook.EditSimPhoneBookActivity;
import telephony.internal.android.com.phoneui.R;



/**
 * Created by yangbofeng on 2018/7/4.
 * 如果当前字母下没有联系人，弹出此界面
 */

public class EditSimBookAddPeopelFragmet extends Fragment implements EditSimBookActivity.Pressed {
    private String TAG ="EditSimBookAddFragment";
    private View view;
    private LinearLayout ly_toast_NE;
    private EditSimBookActivity mEditSimBookActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sim_book_add_people, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mEditSimBookActivity  = (EditSimBookActivity) getActivity();
        mEditSimBookActivity.setonKeyDownListener(this,true);
    }

    private void initView() {
        ly_toast_NE = view.findViewById(R.id.ly_toast_NE);
        ly_toast_NE.setBackgroundColor(getResources().getColor(R.color.bg_selected));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onkeyDown(int code) {
        switch (code) {
            case 23:
                Log.e(TAG,"ok");
                Intent intent = new Intent(getActivity(), EditSimPhoneBookActivity.class);
                startActivity(intent);
                getActivity().onBackPressed();
            break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEditSimBookActivity.unSetKeyDownLinster(false);
        Log.e(TAG,"unSetKeyDownLinster");
    }
}
