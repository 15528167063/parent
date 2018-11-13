package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/9.
 */

public class InitMainDataEvent {
    public String childUUID;
    public String childName ;

    public InitMainDataEvent(String  childUUID, String childName) {
        this.childUUID=childUUID;
        this.childName = childName;
    }
}
