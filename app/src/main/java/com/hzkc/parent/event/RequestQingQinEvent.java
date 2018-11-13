package com.hzkc.parent.event;

import com.hzkc.parent.greendao.entity.PhoneData;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class RequestQingQinEvent {
    public String childUUID;
    public String parentUUID;
    public List<PhoneData> list;
    public RequestQingQinEvent(String childUUID, String parentUUID,List<PhoneData> list) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
        this.list=list;
    }
}
