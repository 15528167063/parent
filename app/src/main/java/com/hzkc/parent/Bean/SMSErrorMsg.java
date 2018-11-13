package com.hzkc.parent.Bean;

/**
 * Created by lenovo-s on 2017/4/27.
 */

public class SMSErrorMsg {

    /**
     * status : 477
     * detail : 当前手机号发送短信的数量超过限额
     */

    private int status;
    private String detail;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
