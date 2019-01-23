package telephony.internal.android.com.phoneui.Voice.mode;

/**
 * Created by yangbofeng on 2018/7/13.
 */

public class VoiceBean {
    private String date; //日期
    private String name; //名字
    private String number;//号码
    private String tag; //是否已播放
    private String path;//文件路径

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;

    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
