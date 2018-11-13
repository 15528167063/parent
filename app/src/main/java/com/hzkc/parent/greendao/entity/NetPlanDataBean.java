package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lenovo-s on 2016/11/14.
 */
@Entity
public class NetPlanDataBean {
    @Id
    private Long _id;   //id,表的主键并自增长

    private String childuuid;   //每个孩子的uuid

    public String netplanname;//上网计划名称

    public String weekday;//星期天数 1 2 3 4 5 6 7

    public String startplantime;//计划开始时间

    public String endplantime;//计划结束时间

    public String planflag;//计划开关 0代表关 1代表开

    public String getEndplantime() {
        return this.endplantime;
    }

    public void setEndplantime(String endplantime) {
        this.endplantime = endplantime;
    }

    public String getStartplantime() {
        return this.startplantime;
    }

    public void setStartplantime(String startplantime) {
        this.startplantime = startplantime;
    }

    public String getWeekday() {
        return this.weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getNetplanname() {
        return this.netplanname;
    }

    public void setNetplanname(String netplanname) {
        this.netplanname = netplanname;
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

    public String getPlanflag() {
        return this.planflag;
    }

    public void setPlanflag(String planflag) {
        this.planflag = planflag;
    }

    @Generated(hash = 1236024040)
    public NetPlanDataBean(Long _id, String childuuid, String netplanname,
            String weekday, String startplantime, String endplantime,
            String planflag) {
        this._id = _id;
        this.childuuid = childuuid;
        this.netplanname = netplanname;
        this.weekday = weekday;
        this.startplantime = startplantime;
        this.endplantime = endplantime;
        this.planflag = planflag;
    }

    @Generated(hash = 1072140400)
    public NetPlanDataBean() {
    }
}
