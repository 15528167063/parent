package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/22.
 */

public class RegiseterDataEvent {
    public String isRegistered;

    public String userid;

    public RegiseterDataEvent(String isRegistered,String userid) {
        this.isRegistered = isRegistered;
        this.userid = userid;
    }
}
