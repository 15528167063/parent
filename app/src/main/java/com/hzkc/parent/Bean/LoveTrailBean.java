package com.hzkc.parent.Bean;

/**
 * Created by lenovo-s on 2016/12/13.
 */

public class LoveTrailBean {
    private String requestTime;

    public String getRequestTime2() {
        return requestTime2;
    }

    public void setRequestTime2(String requestTime2) {
        this.requestTime2 = requestTime2;
    }

    private String requestTime2;
    private String address;
    private String stopTime;

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public LoveTrailBean(String requestTime, String address, String stopTime,String requestTime2) {
        this.requestTime = requestTime;
        this.requestTime2 = requestTime2;
        this.address = address;
        this.stopTime = stopTime;
    }
}
