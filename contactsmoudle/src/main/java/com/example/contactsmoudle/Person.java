package com.example.contactsmoudle;
/*
 * 文件名:     Person
 * 创建者:     阿钟
 * 创建时间:   2016/11/17 19:07
 * 描述:       封装联系人列表信息
 */


public class Person {
    //姓名
    private String name;
    //拼音
    private String pinyin;
    //拼音首字母
    private String headerWord;
    //电话号
    private String userphone;
    public Person() {

    }

    public String getPinyin() {
        return pinyin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.pinyin = PinYinUtils.getPinyin(name);
        headerWord = pinyin.substring(0, 1);
    }

    public String getHeaderWord() {
        return headerWord;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }
}
