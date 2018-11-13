package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/11/9.
 * 学生信息
 */

public class AddStudentData1 {

    /**
     * 家长端电话
     */
    public String a;
    /**
     * 家长端电话密码
     */
    public String b;
    /**
     * 唯一标识
     */
    public String c;
    /**
     *  name
     */
    public String d;
    /**
     * 性别
     */
    public String f;
    /**
     * 学校
     */
    public String g;

    /**
     * 机型
     */
    public String h;

    public String i;

    public AddStudentData1() {
        a = "";
        b = "";
        c = "";
        d = "";
        g = "";
        h = "";
        i= "";
        f="";

    }
    public String getNianJi() {
        return g;
    }

    public void setNianJi(String s) {
        g = s;
    }

    public String getSchool() {
        return f;
    }

    public void setSchool(String s) {
        f = s;
    }
    public void setChildSex(String s) {
        f = s;
    }

    public String getChildSex() {
        return f;
    }
    public String getChildName() {
        return d;
    }

    public void setChildName(String s) {
        d = s;
    }

    @Override
    public String toString() {
        return "AddStudentData{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                ", d='" + d + '\'' +
                ", e='" + f + '\'' +
                ", f='" + g + '\'' +
                '}';
    }
}
