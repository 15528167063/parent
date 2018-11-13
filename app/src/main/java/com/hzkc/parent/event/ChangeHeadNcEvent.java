package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/6.
 */

public class ChangeHeadNcEvent {
    /**
     * 1代表成功2代表失败
     * */
    public String nc;
    public ChangeHeadNcEvent(String nc) {
        this.nc = nc;
    }
}
