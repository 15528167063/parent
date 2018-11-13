package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/9/11.
 */

public class QinqPhoneBean {
    private String a; //昵称
    private String b;//电话
    private String c; //状态

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


    public QinqPhoneBean(String a, String b, String c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
