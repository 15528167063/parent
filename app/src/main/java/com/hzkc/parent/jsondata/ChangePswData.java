package com.hzkc.parent.jsondata;

/**
 * Created by lenovo-s on 2016/11/22.
 */

public class ChangePswData {
    //电话号码
    public String a;
    //新密码
    public String b;

    public ChangePswData() {
        this.b ="";
        this.a="";
    }
    public String getPhoneNum() {
        return a;
    }

    public void setPhoneNum(String a) {
        this.a = a;
    }
    public String getPsw() {
        return b;
    }

    public void setPsw(String b) {
        this.b = b;
    }


}
