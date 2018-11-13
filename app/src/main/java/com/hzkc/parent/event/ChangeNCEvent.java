package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/6.
 */

public class ChangeNCEvent {
    public String nc;
    /**
     * 1代表成功2代表失败
     * */
    public ChangeNCEvent(String nc) {
        this.nc = nc;
    }
}
