package com.hzkc.parent.jsondata;

/**
 * Created by lenovo-s on 2016/11/22.
 */

public class LoginData {
    //注册手机号码
    public String a;
    //注册密码
    public String b;
    //注册地址
    public String c;
    //注册推送token
    public String d;

    public LoginData() {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public String getTelNo() {
        return a;
    }

    public void setTelNo(String a) {
        this.a = a;
    }

    public String getPwd() {
        return b;
    }

    public void setPwd(String b) {
        this.b = b;
    }
    public String getTokenID() {
        return d;
    }

    public void setTokenID(String d) {
        this.d =d;
    }
    public String getAdress() {
        return c;
    }

    public void setAdress(String c) {
        this.c =c;
    }
}
