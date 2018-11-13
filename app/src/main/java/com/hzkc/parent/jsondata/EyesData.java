package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/11/9.
 * 视力保护计划数据
 */

public class EyesData {

    /**
     * 间隔时间
     */
    public String a;
    /**
     * 休息时间
     */
    public String b;
    /**
     * 计划是否开启
     */
    public String c;

    public EyesData() {
        this.a = "";
        this.b = "";
        this.c = "";
    }

    public String getJianGeTime() {
        return a;
    }

    public void setJianGeTime(String s) {
        a = s;
    }

    public String getSleepTime() {
        return b;
    }

    public void setSleepTime(String s) {
        b = s;
    }

    public String getIsOpen() {
        return c;
    }

    public void setIsOpen(String s) {
        c = s;
    }


}
