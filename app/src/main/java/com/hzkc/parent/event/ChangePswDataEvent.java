package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/6.
 */

public class ChangePswDataEvent {
    public String changeFlag;
    /**
     * 1代表成功2代表失败
     * */
    public ChangePswDataEvent(String changeFlag) {
        this.changeFlag = changeFlag;
    }
}
