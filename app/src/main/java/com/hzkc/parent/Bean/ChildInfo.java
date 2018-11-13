package com.hzkc.parent.Bean;

import com.hzkc.parent.jsondata.AppData;
import com.hzkc.parent.jsondata.AppInstallData;
import com.hzkc.parent.jsondata.EyesData;
import com.hzkc.parent.jsondata.NetPlanData;
import com.hzkc.parent.jsondata.StudentData;

import java.util.List;

/**
 * Created by lenovo-s on 2016/12/30.
 */

public class ChildInfo {
    public String a;

    public List<ChildControlInfo> b;
    public List<NetPlanData> c;
    public List<AppInstallData> d;
    public List<StudentData> e;
    public List<AppData> f;
    public List<NetControlBean> g;
    public List<EyesData> h;
    public String i;  //是不是允许被安装



    public String getInstallable() {
        return i;
    }

    public void setInstallable(String i) {
        this.i = i;
    }

    public String getChilduuid() {
        return a;
    }

    public void setChilduuid(String a) {
        this.a = a;
    }

    public List<ChildControlInfo> getConfigInfo() {
        return b;
    }

    public void setConfigInfo(List<ChildControlInfo> b) {
        this.b = b;
    }

    public List<NetPlanData> getNetPlanList() {
        return c;
    }

    public void setNetPlanList(List<NetPlanData> c) {
        this.c = c;
    }

    public List<AppInstallData> getAppList() {
        return d;
    }

    public void setAppList(List<AppInstallData> d) {
        this.d = d;
    }

    public List<StudentData> getStudentDatas() {
        return e;
    }

    public void setStudentDatas(List<StudentData> e) {
        this.e = e;
    }

    public List<com.hzkc.parent.jsondata.AppData> getAppData() {
        return f;
    }

    public void setAppData(List<AppData> f) {
        this.f = f;
    }

    public List<NetControlBean> getNetControlBean() {
        return g;
    }

    public void setNetControlBean(List<NetControlBean> g) {
        this.g = g;
    }

    public List<EyesData> getEyesData() {
        return h;
    }

    public void setEyesData(List<com.hzkc.parent.jsondata.EyesData> h) {
        this.h = h;
    }
}
