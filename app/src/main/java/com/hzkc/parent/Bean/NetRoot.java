package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by lenovo-s on 2017/2/10.
 */

public class NetRoot {
    private String msg;
    private List<NetInfoBean> data ;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<NetInfoBean> getData() {
        return data;
    }

    public void setData(List<NetInfoBean> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
