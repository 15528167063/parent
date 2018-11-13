package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/3.
 */

public class YjgkEvent {
    public String yjgkState;
    public String childUUID;
    public String parentUUID;
    public YjgkEvent(String parentUUID,String childUUID,String yjgkState) {
        this.parentUUID=parentUUID;
        this.childUUID=childUUID;
        this.yjgkState = yjgkState;
    }
}
