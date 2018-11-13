package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/11/9.
 * 学生信息
 */

public class AddStudentData {

    /**
     * 家长端parentuuid
     * //"{\"a\":\"19f2de7529eb4861839e38981be31b75\",\"b\":\"AC7Gnz3+4WwDAI/L1kLsFoJ2\",\"c\":\"ww\",\"d\":\"1\",
     * \"e\":\"\",\"f\":\"\",\"g\":\"vivo_vivoY55\",\"h\":\"ycz\",\"i\":\"1\"}
     */
    public String a;
    /**
     * 孩子端的唯一标示
     */
    public String b;
    /**
     * 姓名
     */
    public String c;
    /**
     * 性别 1表示男孩 2表示女孩
     */
    public String d;
    /**
     * 年级
     */
    public String e;
    /**
     * 学校
     */
    public String f;
    /**
     * 机型
     */
    public String g;
    /**
     * 来源
     */
    public String h;

    public String i;

    public AddStudentData() {
        a = "";
        b = "";
        c = "";
        d = "";
        e = "";
        f = "";
        g = "";
        h = "";
        i = "";

    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getChildFrom() {
        return h;
    }

    public void setChildFrom(String h) {
        this.h = h;
    }

    public String getJiXin() {
        return g;
    }

    public void setJiXin(String g) {
        this.g = g;
    }

    public String getNianJi() {
        return e;
    }

    public void setNianJi(String s) {
        e = s;
    }

    public String getSchool() {
        return f;
    }

    public void setSchool(String s) {
        f = s;
    }
    public void setChildSex(String s) {
        d = s;
    }

    public String getChildSex() {
        return d;
    }
    public String getChildName() {
        return c;
    }

    public void setChildName(String s) {
        c = s;
    }

    @Override
    public String toString() {
        return "AddStudentData{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                ", d='" + d + '\'' +
                ", e='" + e + '\'' +
                ", f='" + f + '\'' +
                '}';
    }
}
