package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/22.
 */

public class RequestLoginEvent {

    public String phoneNum;
    public String psw;
    public String locations;
    public String token;



    public RequestLoginEvent(String phoneNum, String psw,String token,String locations) {
        this.phoneNum = phoneNum;
        this.psw = psw;
        this.token = token;
        this.locations = locations;
    }
}
