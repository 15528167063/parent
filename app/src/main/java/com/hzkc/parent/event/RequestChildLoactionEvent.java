package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class RequestChildLoactionEvent {
    public String childUUID;
    public String parentUUID;

    public RequestChildLoactionEvent(String childUUID, String parentUUID) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
    }
}
