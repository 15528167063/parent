package com.hzkc.parent.event;


import com.hzkc.parent.greendao.entity.AppDataBean;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class UnInstallAppListEvent {
    public String childUUID;
    public String parentUUID;
    public List<AppDataBean> list;

    public UnInstallAppListEvent(String childUUID, String parentUUID, List<AppDataBean> f) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
        this.list = f;
    }
}
