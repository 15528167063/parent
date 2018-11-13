package com.hzkc.parent.event;

import com.hzkc.parent.jsondata.LocationData;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class ChildLocationPathDataEvent {

    public List<LocationData> locationDataList;

    public ChildLocationPathDataEvent(List<LocationData> locationDataList) {
        this.locationDataList = locationDataList;
    }
}
