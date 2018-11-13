package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 应用追踪
 */
@Entity
public class AppUseBean {
    @Id
    private Long _id;
    private String a; //应用名
    private String b;//包名
    private String c; //时间
    private String d; //使用时间点
    private String childuuid;

    @Generated(hash = 160042435)
    public AppUseBean(Long _id, String a, String b, String c, String d,
            String childuuid) {
        this._id = _id;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.childuuid = childuuid;
    }

    @Generated(hash = 182264854)
    public AppUseBean() {
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getChilduuid() {
        return childuuid;
    }

    public void setChilduuid(String childuuid) {
        this.childuuid = childuuid;
    }
}
