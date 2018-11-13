package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/6.
 */

public class ChildOnlineAndOfflineEvent {
    public String childUUID;
    public String onlinFlag;
    /**
     * 1代表上线2代表离线
     * */
    public ChildOnlineAndOfflineEvent(String childUUID, String onlinFlag) {
        this.childUUID = childUUID;
        this.onlinFlag = onlinFlag;
    }
}
