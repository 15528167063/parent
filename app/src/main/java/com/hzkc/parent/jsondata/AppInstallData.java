package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/12/30.
 */

public class AppInstallData {

    /**
     * 包名
     */
    public String a;
    /**
     * 应用名
     */
    public String b;
    /**
     * 黑白名单
     */
    public String c;

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String d;

    public AppInstallData() {
        a = "";
        b = "";
        c = "";
    }

    public void setAppPkgName(String sss) {
        this.a = sss;
    }

    public String getAppPkgName() {
        return this.a;
    }

    public void setAppName(String xx) {
        this.b = xx;
    }

    public String getAppName() {
        return this.b;
    }

    public void setAppwhite(String xx) {
        this.c = xx;
    }

    public String getAppwhite() {
        return this.c;
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
