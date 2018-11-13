package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/30.
 */

public class MessageRoot {
    private String msg;
    private List<MessageResult> data ;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<MessageResult> getData() {
        return data;
    }

    public void setData(List<MessageResult> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
