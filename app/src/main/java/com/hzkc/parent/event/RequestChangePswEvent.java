package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/12/6.
 */

public class RequestChangePswEvent {
    public String phoneNum;
    public String pwd;
    /**
     * 修改密码
     * */
    public RequestChangePswEvent(String phoneNum, String pwd) {
        this.phoneNum = phoneNum;
        this.pwd = pwd;
    }
}
