package com.hzkc.parent.jsondata;

/**
 * Created by lenovo-s on 2017/1/12.
 */

public class AppData {
    /**
     * 包名
     * */
    public String a;
    public String c;
    public String b;
    public String d="0";
    public String getAppname() {
        return b;
    }

    public void setAppname(String appname) {
        this.b = appname;
    }
    public String getB() {
        return c;
    }

    public void setB(String c) {
        this.c = c;
    }

    public AppData() {
        a = "";
    }
    public AppData(String a, String c, String b, String d) {
        this.a = a;
        this.c = c;
        this.b = b;
        this.d = d;
    }
    public void setAppPkgName(String a) {
        this.a = a;
    }

    public String getAppPkgName() {
        return this.a;
    }

    @Override
    public String toString() {
        return "AppInstallData{" +
                "a='" + a + '\'' +
                '}';
    }
}
