package com.hzkc.parent.event;

import com.hzkc.parent.jsondata.NetPlanData;

import java.util.ArrayList;

/**
 * Created by lenovo-s on 2016/12/12.
 */

public class TeamInternetPlanEvent {
    public String childUUID;
    public String parentUUID;
    public String data;
    public ArrayList<NetPlanData> planlist;
    public TeamInternetPlanEvent(String childUUID, String parentUUID, String data, ArrayList<NetPlanData> planlist) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
        this.data = data;
        this.planlist = planlist;
    }
}
