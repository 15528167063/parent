package com.hzkc.parent.event;



public class OfflinSuccessEvent {
    public String parentuuid;

    public OfflinSuccessEvent(String parentuuid) {
        this.parentuuid = parentuuid;
    }

    public String getParentuuid() {
        return parentuuid;
    }

    public void setParentuuid(String parentuuid) {
        this.parentuuid = parentuuid;
    }
}
