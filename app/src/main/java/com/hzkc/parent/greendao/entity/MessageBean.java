package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class MessageBean {
    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    @Id
    private Long _id;   //id,表的主键并自增长

    private String b;//每个孩子的uuid （c）

    private String d;//消息Id  （d）


    private String g;//时间搓  （g）




    @Generated(hash = 230131609)
    public MessageBean(Long _id, String b, String d, String g) {
        this._id = _id;
        this.b = b;
        this.d = d;
        this.g = g;
    }

    @Generated(hash = 1588632019)
    public MessageBean() {
    }




    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }


    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }
}
