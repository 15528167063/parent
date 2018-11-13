package com.hzkc.parent.greendao.entity;

import java.io.Serializable;

public class ChildTable implements Serializable{
//    private Long _id;   //id,表的主键并自增长
    private String childuuid;   //每个孩子的uuid
    private String name;    //孩子的昵称
    private String sex;    //孩子的昵称
    private String place;     //孩子性别
    private String team;   //孩子班级
    private String state;  //孩子年级
    private String regist;  //孩子年级
    private String lasttime;  //孩子年级
    private String location;  //孩子年级

    public ChildTable(String childuuid, String name , String sex, String place, String team, String state, String regist, String lasttime, String location) {
        this.childuuid = childuuid;
        this.name = name;
        this.place = place;
        this.sex = sex;
        this.team = team;
        this.state = state;
        this.regist = regist;
        this.lasttime = lasttime;
        this.location = location;
    }

    public String getChilduuid() {
        return childuuid;
    }

    public void setChilduuid(String childuuid) {
        this.childuuid = childuuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTeam() {
        return team;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegist() {
        return regist;
    }

    public void setRegist(String regist) {
        this.regist = regist;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
