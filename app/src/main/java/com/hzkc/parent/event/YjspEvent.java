package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/3.
 */

public class YjspEvent {
    public String childUUID;
    public String parentUUID;
    public String yjspState;

    public YjspEvent(String parentUUID,String childUUID,String yjspState) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
        this.yjspState = yjspState;
    }
}
