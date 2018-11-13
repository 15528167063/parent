package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class RequestAppUseTimeEvent {
    public String childUUID;

    public RequestAppUseTimeEvent(String childUUID) {
        this.childUUID = childUUID;
    }
}
