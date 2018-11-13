package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/11/9.
 */
//家长端注册的时候带的注册数据信息
public class RegisterInfo {


    //注册手机号码  parentUUID
    public String a;
    //注册密码  childUUID //最多12位
    public String b;
    //代理id
    public String c;

    /**
     * 是否已经注册 1代表成功 2代表已经存在 //家长唯一标示
     */
    public String d;

    /**
     * 手机信息 json格式
     */
    public String e;
    /**
     * 学生信息 json格式
     */
    public String f;

    public RegisterInfo() {
        this.a = "";
        this.b = "";
        this.c = "";
        this.d = "";
        this.e = "";
        this.f = "";
        this.f = "";
    }


    public void setPhoneInfoData(String xx) {
        this.e = xx;
    }

    public String getPhoneInfoData() {
        return this.e;
    }

    public void setStudentData(String xx) {
        this.f = xx;
    }

    public String getStudentData() {
        return this.f;
    }


    /*
    设置手机号
     */
    public void setTelNo(String s) {
        this.a = s;
    }

    /*
    获取手机号
     */
    public String getTelNo() {
        return a;
    }

    public String getPwd() {
        return this.b;
    }

    public void setPwd(String s) {
        this.b = s;
    }

    public String getDaiLiId() {
        return this.c;
    }

    public void setDaiLiId(String s) {
        this.c = s;
    }

    public String getIsRegistered() {
        return d;
    }

    public void setIsRegistered(String s) {
        d = s;
    }

}
