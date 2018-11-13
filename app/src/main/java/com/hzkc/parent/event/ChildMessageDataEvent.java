package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/5.
 */

public class ChildMessageDataEvent {
    public String childUUID;
    public String childName ;
    public String childSex;
    public String childFrom;
    public String childztl;
    public String childimage;

    public ChildMessageDataEvent(String childUUID, String childName, String childSex, String childFrom, String childztl, String childimage) {
        this.childUUID = childUUID;
        this.childName = childName;
        this.childSex = childSex;
        this.childFrom = childFrom;
        this.childztl = childztl;
        this.childimage = childimage;
    }
}
