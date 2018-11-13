package com.hzkc.parent.event;

import com.hzkc.parent.jsondata.AppInstallData;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class WhiteAppListEvent {

    public List<AppInstallData> appDataList;

    public WhiteAppListEvent(List<AppInstallData> appDataList) {
        this.appDataList = appDataList;
    }
}
