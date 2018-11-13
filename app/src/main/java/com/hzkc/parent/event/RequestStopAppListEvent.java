package com.hzkc.parent.event;


import com.hzkc.parent.jsondata.AppData;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class RequestStopAppListEvent {
    public String childUUID;
    public String parentUUID;
    public List<AppData>list;
    public String  data;

    public RequestStopAppListEvent(String childUUID, String parentUUID, List<AppData> list, String data){
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
        this.list=list;
        this.data=data;
    }
}
