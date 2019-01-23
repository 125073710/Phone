package com.tricheer.test.phone.phonebook;

/**
 * Created by yangbofeng on 2018/6/12.
 */

public class PhoneContaceData {
    private String name ;
    private String headerWord;

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    //拼音
    private String pinyin;

    private String jpname;
    private String number1;
    private String number2;
    private String number3;
    private Long ID;

    public String getHeaderWord() {
        return headerWord;
    }

    public void setHeaderWord(String headerWord) {
        this.headerWord = headerWord;
    }
    public String getJpname() {
        return jpname;
    }

    public void setJpname(String jpname) {
        this.jpname = jpname;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getNumber3() {
        return number3;
    }

    public void setNumber3(String number3) {
        this.number3 = number3;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.pinyin = PinYinUtils.getPinyin(name);
        if(pinyin.length()>0){
            headerWord = pinyin.substring(0, 1);
        }

    }


}
