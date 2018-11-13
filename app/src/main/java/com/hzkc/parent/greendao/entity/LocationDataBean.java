package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lenovo-s on 2016/11/14.
 */
@Entity
public class LocationDataBean {
    @Id
    private Long _id;   //id,表的主键并自增长

    private String childuuid;//每个孩子的uuid

    public String  longitude;//经度

    public String  latitude;//维度

    public String lasttime;//时间

    public String getLasttime() {
        return this.lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getChilduuid() {
        return this.childuuid;
    }

    public void setChilduuid(String childuuid) {
        this.childuuid = childuuid;
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    @Generated(hash = 120044203)
    public LocationDataBean(Long _id, String childuuid, String longitude,
            String latitude, String lasttime) {
        this._id = _id;
        this.childuuid = childuuid;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lasttime = lasttime;
    }

    @Generated(hash = 844334381)
    public LocationDataBean() {
    }
}
