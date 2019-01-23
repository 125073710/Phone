package com.tricheer.test.phone.presenter;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import com.tricheer.test.phone.model.PhoneNumberData;

import java.util.List;

/**
 * Created by yangbofeng on 2018/6/7.
 */

public interface Ibutton {
    /**
     * 打电话
     * 需要获取系统权限，需要到AndroidManifest.xml里面配置权限
     * <uses-permissionandroid:name="android.permission.CALL_PHONE"/>
     */
    void  call(TextView view, Context context);

    /**
     * 挂断电话
     */
    void end();

    /**
     * 删除按钮
     */
    void delete(TextView tv_phoneNumber);

    /**
     * 获取电话数据
     */
    void  change(TextView view ,String number);

    /**
     * 查询数据库获取来电记录
     * @return
     */
    void getDataListOut(List<PhoneNumberData>  ListOut,Handler mhandler);
    void  getDataListIn(List<PhoneNumberData> ListIn, Handler mhandler);
    void getDataListUncall(List<PhoneNumberData> ListUncall,Handler mhandler);


}
