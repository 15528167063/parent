package com.hzkc.parent.event;

import com.hzkc.parent.jsondata.AppUsageData;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class AppUseDataEvent {

    public List<AppUsageData> datas;

    public AppUseDataEvent( List<AppUsageData> datas) {
        this.datas = datas;
    }
}
