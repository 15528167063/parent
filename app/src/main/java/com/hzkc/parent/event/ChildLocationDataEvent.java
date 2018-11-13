package com.hzkc.parent.event;

import com.hzkc.parent.jsondata.LocationData;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class ChildLocationDataEvent {
    public LocationData returnData;
    public ChildLocationDataEvent(LocationData returnData) {
        this.returnData = returnData;
    }
}
