package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by lenovo-s on 2016/11/8.
 */
@Entity
public class ChildContrlFlag {
    /**
     * 这里使用greenDao注解的方式来生成相关的方法。
     *
     * @Id 可以将该属性设计为Category表的主键并自增长，注意类型为Long。
     * @Transient 可以设置保留属性，该属性将不会为表字段。
     */
    @Id
    private Long _id;   //id,表的主键并自增长
    @Unique
    private String childuuid; //每个孩子的uuid

    private String parentuuid; //家长uuid

    private String status;  //孩子在线状态

    private String lasttime; //最后登录时间

    private String yjgkflag; //一键管控状态

    private String yjspfalg; //一键锁屏状态

    private String slbhflag; //视力保护

    public String  slbhSpacetime;//间隔时间

    public String slbhResttime;//休息时间

    private String yygkflag; //应用管控（禁止上网计划）

    private String sjsjflag; //睡觉时间段管控

    public String getSjsjflag() {
        return this.sjsjflag;
    }

    public void setSjsjflag(String sjsjflag) {
        this.sjsjflag = sjsjflag;
    }

    public String getYygkflag() {
        return this.yygkflag;
    }

    public void setYygkflag(String yygkflag) {
        this.yygkflag = yygkflag;
    }

    public String getSlbhflag() {
        return this.slbhflag;
    }

    public void setSlbhflag(String slbhflag) {
        this.slbhflag = slbhflag;
    }

    public String getYjspfalg() {
        return this.yjspfalg;
    }

    public void setYjspfalg(String yjspfalg) {
        this.yjspfalg = yjspfalg;
    }

    public String getYjgkflag() {
        return this.yjgkflag;
    }

    public void setYjgkflag(String yjgkflag) {
        this.yjgkflag = yjgkflag;
    }

    public String getLasttime() {
        return this.lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChilduuid() {
        return this.childuuid;
    }

    public void setChilduuid(String childuuid) {
        this.childuuid = childuuid;
    }

    public String getSlbhResttime() {
        return this.slbhResttime;
    }

    public void setSlbhResttime(String slbhResttime) {
        this.slbhResttime = slbhResttime;
    }

    public String getSlbhSpacetime() {
        return this.slbhSpacetime;
    }

    public void setSlbhSpacetime(String slbhSpacetime) {
        this.slbhSpacetime = slbhSpacetime;
    }

    public String getParentuuid() {
        return this.parentuuid;
    }

    public void setParentuuid(String parentuuid) {
        this.parentuuid = parentuuid;
    }
    

    public void set_id(long _id) {
        this._id = _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Long get_id() {
        return this._id;
    }

    @Generated(hash = 1066722058)
    public ChildContrlFlag(Long _id, String childuuid, String parentuuid, String status,
            String lasttime, String yjgkflag, String yjspfalg, String slbhflag,
            String slbhSpacetime, String slbhResttime, String yygkflag, String sjsjflag) {
        this._id = _id;
        this.childuuid = childuuid;
        this.parentuuid = parentuuid;
        this.status = status;
        this.lasttime = lasttime;
        this.yjgkflag = yjgkflag;
        this.yjspfalg = yjspfalg;
        this.slbhflag = slbhflag;
        this.slbhSpacetime = slbhSpacetime;
        this.slbhResttime = slbhResttime;
        this.yygkflag = yygkflag;
        this.sjsjflag = sjsjflag;
    }

    @Generated(hash = 698416961)
    public ChildContrlFlag() {
    }

}
