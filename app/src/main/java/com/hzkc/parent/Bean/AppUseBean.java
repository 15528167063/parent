package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/9/11.
 */

public class AppUseBean {
    private String a; //应用名
    private String b;//包名
    private String c; //时间
    private String d; //使用时间长度

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
        this.d = d;
    }

    public AppUseBean(String a, String b, String c, String d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
}
