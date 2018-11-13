package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/5/14.
 */
@Entity
public class PhoneData {
    @Id
    private Long _id;
    private String childuuid; //每个孩子的uuid
    private String name;
    private String phone;
    private String state;
    public String getState() {
        return this.state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
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
    @Generated(hash = 1718118370)
    public PhoneData(Long _id, String childuuid, String name, String phone,
            String state) {
        this._id = _id;
        this.childuuid = childuuid;
        this.name = name;
        this.phone = phone;
        this.state = state;
    }
    @Generated(hash = 143274380)
    public PhoneData() {
    }


}
