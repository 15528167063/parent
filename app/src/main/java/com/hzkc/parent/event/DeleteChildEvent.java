package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/12.
 */

public class DeleteChildEvent {
    public String childUUID;
    public String parentUUID;

    public DeleteChildEvent(String childUUID, String parentUUID) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
    }
}
