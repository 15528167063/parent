package com.hzkc.parent.jsondata;

public class LocationData {


    //经度
    public String  a;
    //维度
    public String  b;
    //经纬时间
    public String c;

    public LocationData()
    {
        this.a="";
        this.b="";
        this.c="";
    }

    //获取经度
    public String  getLongitude() {
        return  a;
    }

    //设置经度
    public void setLongitude(String  Longitude) {
        a = Longitude;
    }

    //获取维度
    public String  getLatitude() {
        return b;
    }

    //设置维度
    public void setLatitude(String  Latitude) {
        b = Latitude;
    }
    //获取时间
    //获取详细地址
    public String  getTime() {
        return c;
    }

    //设置时间
    public void setTime(String  time) {
        c = time;
    }

    public LocationData(String a, String b, String c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
