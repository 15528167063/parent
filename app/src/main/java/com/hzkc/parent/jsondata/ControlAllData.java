package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/12/12.
 */

public class ControlAllData {

    /**
     * 功能是否已开启
     */
    public  String a;
    public ControlAllData()
    {
        a=CmdCommon.CMD_FLAG_CLOSE;
    }
    public String  getIsOpen()
    {
        return a;
    }
    public void setIsOpen(String ss)
    {
        this.a=ss;
    }
}
