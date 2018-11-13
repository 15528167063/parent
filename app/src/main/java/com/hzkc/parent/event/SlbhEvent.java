package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/3.
 */

public class SlbhEvent {
    public String childUUID;
    public String parentUUID;
    public String slbhState;
    public String JianGeTime;
    public String SleepTime;
    public SlbhEvent(String parentUUID, String childUUID, String slbhState,String JianGeTime,String SleepTime) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
        this.slbhState = slbhState;
        this.JianGeTime=JianGeTime;
        this.SleepTime=SleepTime;
    }
}
