package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/11/7.
 */

public class PowerLevelData {

    //当前电池电量
    public String a;

    public PowerLevelData()
    {
        a="";
    }
    public void setPowerLevel(String s)
    {
        this.a=s;
    }
    public String getPowerLevel()
    {
        return a;
    }
}
