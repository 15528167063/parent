package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/12/30.
 */

public class PhoneDatas {

    /**
     * 姓名
     */
    public String a;
    /**
     * 电话
     */
    public String b;
    /**
     * 黑白名单
     */
    public String c;

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

    public PhoneDatas(String a, String b, String c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public PhoneDatas() {
    }

    @Override
    public String toString() {
        return "AppInstallData{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                '}';
    }
}
