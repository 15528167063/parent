package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2018/5/14.
 */
@Entity
public class LoveTrailData {
    @Id
    private Long _id;
    private String a; //经度，纬度
    private String b;//地名
    private String c; //时间
    private String childuuid;
    public String getChilduuid() {
        return this.childuuid;
    }
    public void setChilduuid(String childuuid) {
        this.childuuid = childuuid;
    }
    public String getC() {
        return this.c;
    }
    public void setC(String c) {
        this.c = c;
    }
    public String getB() {
        return this.b;
    }
    public void setB(String b) {
        this.b = b;
    }
    public String getA() {
        return this.a;
    }
    public void setA(String a) {
        this.a = a;
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    @Generated(hash = 241157767)
    public LoveTrailData(Long _id, String a, String b, String c, String childuuid) {
        this._id = _id;
        this.a = a;
        this.b = b;
        this.c = c;
        this.childuuid = childuuid;
    }
    @Generated(hash = 156330460)
    public LoveTrailData() {
    }

}
