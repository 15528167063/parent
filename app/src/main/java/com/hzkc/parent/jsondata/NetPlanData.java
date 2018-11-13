package com.hzkc.parent.jsondata;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/11/9.
 * 上网计划数据
 */

public class NetPlanData implements Serializable {

    //上网计划名称
    public String a;
    //星期天数 1 2 3 4 5 6 7
    public String b;
    //计划开始时间
    public String c;
    //计划结束时间
    public String d;
    //计划开关
    public String e;

    public NetPlanData()
    {
        this.a="";
        this.b="";
        this.c="";
        this.d="";
        this.e="";
    }

    public NetPlanData(String a, String b, String c, String d, String e) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    /*
        获取计划名称
         */
    public String getPlanName()
    {
        return a;
    }
    /*
    设置计划名称
     */
    public void setPlanName(String s)
    {
        this.a=s;
    }
    /*
    获取星期天数
     */
    public String  getDays()
    {
        return b;
    }
    public void setDays(String s)
    {
        b=s;
    }
    public String getPlanStartTime()
    {
        return c;
    }
    public void setPlanStartTime(String s)
    {
        c=s;
    }
    public String getPlanEndTime()
    {
        return d;
    }
    public void setPlanEndTime(String s)
    {
        d=s;
    }
    public String getIsOpen()
    {
        return e;
    }
    public void setIsOpen(String s)
    {
        e=s;
    }

}
