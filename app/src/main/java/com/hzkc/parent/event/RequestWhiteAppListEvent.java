package com.hzkc.parent.event;

import com.hzkc.parent.greendao.entity.AppDataBean;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class RequestWhiteAppListEvent {
    public String childUUID;
    public String parentUUID;
    public List<AppDataBean> datas ;
    public RequestWhiteAppListEvent(String childUUID, String parentUUID, List<AppDataBean> datas) {
        this.childUUID = childUUID;
        this.parentUUID = parentUUID;
        this.datas = datas;
    }
}
