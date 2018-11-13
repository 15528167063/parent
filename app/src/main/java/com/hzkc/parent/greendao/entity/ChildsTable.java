package com.hzkc.parent.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by lenovo-s on 2016/11/8.
 */
@Entity
public class ChildsTable implements Serializable{
    /**
     * 这里使用greenDao注解的方式来生成相关的方法。
     *
     * @Id 可以将该属性设计为Category表的主键并自增长，注意类型为Long。
     * @Transient 可以设置保留属性，该属性将不会为表字段。
     */
    @Id
    private Long _id;   //id,表的主键并自增长
    private String childuuid;   //每个孩子的uuid
    private String name;    //孩子的昵称
    private String sex;     //孩子性别
    private String school;   //孩子班级
    private String nianji;  //孩子年级
    private String childfrom;  //孩子年级
    private String childztl;  //孩子是不是被使用状态栏
    private String imageurl;  //孩子头像
    public String getImageurl() {
        return this.imageurl;
    }
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public String getChildztl() {
        return this.childztl;
    }
    public void setChildztl(String childztl) {
        this.childztl = childztl;
    }
    public String getChildfrom() {
        return this.childfrom;
    }
    public void setChildfrom(String childfrom) {
        this.childfrom = childfrom;
    }
    public String getNianji() {
        return this.nianji;
    }
    public void setNianji(String nianji) {
        this.nianji = nianji;
    }
    public String getSchool() {
        return this.school;
    }
    public void setSchool(String school) {
        this.school = school;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
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
    @Generated(hash = 229020901)
    public ChildsTable(Long _id, String childuuid, String name, String sex,
            String school, String nianji, String childfrom, String childztl,
            String imageurl) {
        this._id = _id;
        this.childuuid = childuuid;
        this.name = name;
        this.sex = sex;
        this.school = school;
        this.nianji = nianji;
        this.childfrom = childfrom;
        this.childztl = childztl;
        this.imageurl = imageurl;
    }
    @Generated(hash = 1414051048)
    public ChildsTable() {
    }


}
