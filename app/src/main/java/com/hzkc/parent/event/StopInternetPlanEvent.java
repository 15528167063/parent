package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/12.
 */

public class StopInternetPlanEvent {
    public String childUUID;
    public String parentUUID;

    public StopInternetPlanEvent(String childUUID, String parentUUID) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
    }
}
