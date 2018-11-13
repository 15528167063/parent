package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class RequestInstallEvent {
    public String childUUID;
    public String parentUUID;
    public String state;

    public RequestInstallEvent(String childUUID, String parentUUID, String state) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
        this.state = state;

    }
}
