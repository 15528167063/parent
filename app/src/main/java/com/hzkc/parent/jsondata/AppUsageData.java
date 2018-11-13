package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/12/30.//[{\"c\":\"设置\",\"d\":1538033310974,\"a\":\"com.android.settings\",\"b\":\"312\"}
 */

public class AppUsageData {
    //app包名
    public String a;
    //app使用时长
    public String b;
    //app使用应用名
    public String c ;
    //app使用时间
    public String d;
    public AppUsageData() {
        this.a = "";
        this.b = "";
        this.c = "";
        this.d= "";
    }

    public void setAppPkgName(String ss) {
        this.a = ss;
    }

    public String getAppPkgName() {
        return this.a;
    }

    public void setAppUsage(String ss) {
        this.b = ss;
    }

    public String getAppUsage() {
        return this.b;
    }
    public void setAppName(String c) {
        this.c = c;
    }

    public String getAppName() {
        return this.c;
    }
    public void setAppTime(String d) {
        this.d = d;
    }
    public String getAppTime() {
        return this.d;
    }

    public AppUsageData(String a, String b, String c, String d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
}
