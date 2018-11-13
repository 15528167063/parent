package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/22.
 */

public class LoginDataEvent {
    public String isRegistered;
    public String parentUUID;
    public String e ;
    public LoginDataEvent(String isRegistered, String parentUUID,String e) {
        this.isRegistered = isRegistered;
        this.parentUUID = parentUUID;
        this.e=e;
    }
}
