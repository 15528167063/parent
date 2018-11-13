package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/11/9.
 * 学生信息
 */

public class StudentData {

    /**
     * 姓名
     */
    public String a;
    /**
     * 性别 1表示男孩 2表示女孩
     */
    public String b;
    /**
     * 年级
     */
    public String c;
    /**
     * 学校
     */
    public String d;
    /**
     * 来源
     */
    public String e;
    /**
     * 是不是使用状态栏
     */
    public String f;
    /**
     * 头像
     */
    public String g;



    public StudentData(String a, String b, String c, String d, String e, String f, String g) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
    }

    public String getChildName() {
        return a;
    }

    public void setChildName(String a) {
        this.a = a;
    }


    public void setChildSex(String b) {
        this.b = b;
    }

    public String getChildSex() {
        return b;
    }

    public String getNianJi() {
        return c;
    }

    public void setNianJi(String c) {
        this.c = c;
    }

    public String getSchool() {
        return d;
    }

    public void setSchool(String d) {
        this.d = d;
    }
    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }
}
