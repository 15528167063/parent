package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lenovo-s on 2016/11/14.
 */
@Entity
public class AppDataBean {
    @Id
    private Long _id;   //id,表的主键并自增长

    private String childuuid;//每个孩子的uuid

    private String apppackgename;//包名

    private String appname;//应用名称

    private String appwhitelist;//黑白名单 //1为白名单 其他为黑名单
    private String issystem;//系统引用
    @Generated(hash = 809496451)
    public AppDataBean(Long _id, String childuuid, String apppackgename,
            String appname, String appwhitelist, String issystem) {
        this._id = _id;
        this.childuuid = childuuid;
        this.apppackgename = apppackgename;
        this.appname = appname;
        this.appwhitelist = appwhitelist;
        this.issystem = issystem;
    }

    @Generated(hash = 2030821229)
    public AppDataBean() {
    }
    public String getAppwhitelist() {
        return this.appwhitelist;
    }

    public void setAppwhitelist(String appwhitelist) {
        this.appwhitelist = appwhitelist;
    }

    public String getAppname() {
        return this.appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getApppackgename() {
        return this.apppackgename;
    }

    public void setApppackgename(String apppackgename) {
        this.apppackgename = apppackgename;
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

    public String getIssystem() {
        return issystem;
    }

    public void setIssystem(String issystem) {
        this.issystem = issystem;
    }
}
