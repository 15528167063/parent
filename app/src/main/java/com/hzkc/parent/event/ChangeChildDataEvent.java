package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/6.
 */

public class ChangeChildDataEvent {
    public String Childuuid;
    public String childname;
    public String childsex;
    public String childclass;
    public String childSchool;
    public String state;

    public ChangeChildDataEvent(String childuuid, String childname, String childsex, String childclass,String childSchool,String state){
        Childuuid = childuuid;
        this.childname = childname;
        this.childsex = childsex;
        this.childclass = childclass;
        this.childSchool=childSchool;
        this.state=state;
    }
}
