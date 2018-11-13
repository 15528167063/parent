package com.hzkc.parent.event;

/**
 * Created by lenovo-s on 2016/11/22.
 */

public class RequestRegisterEvent {

    public String phoneNum;
    public String psw;
    public String phonetype;
    public String nichen;
    public String qudao;
    public RequestRegisterEvent(String phoneNum, String psw,String nichen,String phonetype,String qudao) {
        this.phoneNum = phoneNum;
        this.psw = psw;
        this.phonetype=phonetype;
        this.nichen=nichen;
        this.qudao=qudao;
    }
}
