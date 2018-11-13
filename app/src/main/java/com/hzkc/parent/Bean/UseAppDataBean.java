package com.hzkc.parent.Bean;

/**
 * Created by lenovo-s on 2017/1/18.
 */

public class UseAppDataBean {
    private String appname;
    private String useTime;
    private String timePercent;
    private String packegname;
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPackegname() {
        return packegname;
    }

    public void setPackegname(String packegname) {
        this.packegname = packegname;
    }

    public UseAppDataBean(String appname, String useTime, String timePercent, String packegname) {
        this.appname = appname;
        this.useTime = useTime;
        this.timePercent = timePercent;
        this.packegname =packegname;
    }

    public UseAppDataBean(String appname, String useTime, String timePercent, String packegname, int number) {
        this.appname = appname;
        this.useTime = useTime;
        this.timePercent = timePercent;
        this.packegname = packegname;
        this.number = number;
    }

    public UseAppDataBean() {
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getTimePercent() {
        return timePercent;
    }

    public void setTimePercent(String timePercent) {
        this.timePercent = timePercent;
    }
}
