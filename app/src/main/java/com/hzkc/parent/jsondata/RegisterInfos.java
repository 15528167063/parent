package com.hzkc.parent.jsondata;

import com.hzkc.parent.utils.AppUtils;

/**
 * Created by lenovo on 2016/11/9.
 */
//家长端注册的时候带的注册数据信息
public class RegisterInfos {


    //注册手机号码  parentUUID
    public String a;
    //注册密码  childUUID //最多12位
    public String b;
    //昵称
    public String c;

    /**
     * 手机型号
     */
    public String d;

    /**
     * 标志
     */
    public String e;
    /**
     * 学生信息 json格式
     */
    public String f;
//{"a":"0","b":"","c":"","d":"1","e":"{\"a\":\"15528167050\",\"b\":\"000000\",\"c\":\"haier\",\"d\":\"vivo Y55;banben\",\"e\":\"ycz\",\"f\":\"\"}","f":"","g":"2018-07-11","v":"1.0.0"}
  //{"a":"0","b":"","c":"","d":"23","e":"{\"a\":\"15528167050\",\"b\":\"000000\"}","f":"","g":"1531295640701","v":"1.0.0;360"}

    public RegisterInfos() {
        this.a = "";
        this.b = "";
        this.c = "";
        this.d = "";
        this.e = "";
        this.f = "";
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d+";" +AppUtils.getSpecificMobileRomInfo();
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }
}
