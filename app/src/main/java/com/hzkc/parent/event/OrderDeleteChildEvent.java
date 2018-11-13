package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/22.
 */

public class OrderDeleteChildEvent {
    public String isRegistered;

    public OrderDeleteChildEvent(String isRegistered) {
        this.isRegistered = isRegistered;
    }
}
