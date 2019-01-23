package telephony.internal.android.com.phoneui.mode;

/**
 * Created by yangbofeng on 2018/6/11.
 */

public class PhoneNumberData {
    /**
     * 电话号码
     */
    private  String PhoneNumber;
    /**
     * 电话状态，来电，去电，未接
     */
    private  String states;
    /**
     * 通话时常
     */
    private  String times;
    /**
     *  通话日期
     */
    private  String date;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private  String name;

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }


}
